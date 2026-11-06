package dao;

import factory.ConnectionFactory;
import model.Compra;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

public class CompraDao implements ICompraDao {

    private static final String SQL_ANUNCIO_BLOQUEAR =
            "SELECT id_vendedor, quantidade, preco, status_anuncio " +
                    "FROM anuncio WHERE id_anuncio = ? FOR UPDATE";

    private static final String SQL_INSERIR =
            "INSERT INTO compra (id_anuncio, id_comprador, quantidade, preco, status) " +
                    "VALUES (?, ?, ?, ?, 'CONCLUIDA')";

    private static final String SQL_ATUALIZAR_ESTOQUE =
            "UPDATE anuncio SET quantidade = ?, status_anuncio = ? WHERE id_anuncio = ?";

    private static final String SQL_LISTAR_COMPRADOR =
            "SELECT id_compra, id_anuncio, id_comprador, quantidade, preco, data_compra, status " +
                    "FROM compra WHERE id_comprador = ? ORDER BY data_compra DESC, id_compra DESC";

    private static final String SQL_LISTAR_VENDEDOR =
            "SELECT c.id_compra, c.id_anuncio, c.id_comprador, c.quantidade, " +
                    "c.preco, c.data_compra, c.status " +
                    "FROM compra c INNER JOIN anuncio a ON a.id_anuncio = c.id_anuncio " +
                    "WHERE a.id_vendedor = ? ORDER BY c.data_compra DESC, c.id_compra DESC";

    @Override
    public Compra registrar(Compra compra) {
        validarCompra(compra);

        try (Connection conexao = ConnectionFactory.getConnection()) {
            conexao.setAutoCommit(false);
            try {
                DadosAnuncio anuncio = bloquearAnuncio(conexao, compra.getIdAnuncio());
                if (anuncio == null) {
                    throw new IllegalArgumentException("Anuncio nao encontrado.");
                }
                if (anuncio.idVendedor.equals(compra.getIdComprador())) {
                    throw new IllegalArgumentException("Voce nao pode comprar seu proprio anuncio.");
                }
                if (!"DISPONIVEL".equals(anuncio.status)) {
                    throw new IllegalArgumentException("Este anuncio nao esta disponivel.");
                }

                BigDecimal quantidade = normalizarQuantidade(compra.getQuantidade());
                if (quantidade.compareTo(anuncio.quantidade) > 0) {
                    throw new IllegalArgumentException("Estoque insuficiente. Quantidade disponivel: "
                            + anuncio.quantidade.toPlainString());
                }

                // anuncio.preco e unitario; compra.preco registra o total da operacao.
                BigDecimal total = anuncio.precoUnitario.multiply(quantidade)
                        .setScale(2, RoundingMode.HALF_UP);
                BigDecimal restante = anuncio.quantidade.subtract(quantidade);
                String novoStatus = restante.compareTo(BigDecimal.ZERO) == 0
                        ? "VENDIDO" : "DISPONIVEL";

                try (PreparedStatement ps = conexao.prepareStatement(
                        SQL_INSERIR, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, compra.getIdAnuncio());
                    ps.setInt(2, compra.getIdComprador());
                    ps.setBigDecimal(3, quantidade);
                    ps.setBigDecimal(4, total);
                    if (ps.executeUpdate() == 0) {
                        throw new SQLException("A compra nao foi registrada.");
                    }
                    try (ResultSet chaves = ps.getGeneratedKeys()) {
                        if (!chaves.next()) {
                            throw new SQLException("O banco nao retornou o ID da compra.");
                        }
                        compra.setIdCompra(chaves.getInt(1));
                    }
                }

                try (PreparedStatement ps = conexao.prepareStatement(SQL_ATUALIZAR_ESTOQUE)) {
                    ps.setBigDecimal(1, restante);
                    ps.setString(2, novoStatus);
                    ps.setInt(3, compra.getIdAnuncio());
                    if (ps.executeUpdate() == 0) {
                        throw new SQLException("Nao foi possivel atualizar o estoque do anuncio.");
                    }
                }

                compra.setQuantidade(quantidade.doubleValue());
                compra.setPreco(total.doubleValue());
                compra.setStatus("CONCLUIDA");
                compra.setDataCompra(consultarDataCompra(conexao, compra.getIdCompra()));

                conexao.commit();
                return compra;
            } catch (SQLException | RuntimeException erro) {
                try {
                    conexao.rollback();
                } catch (SQLException erroRollback) {
                    erro.addSuppressed(erroRollback);
                }
                throw erro;
            }
        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao registrar a compra.", erro);
        }
    }

    @Override
    public ArrayList<Compra> listarPorComprador(Integer idComprador) {
        ArrayList<Compra> compras = new ArrayList<>();
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement ps = conexao.prepareStatement(SQL_LISTAR_COMPRADOR)) {
            ps.setInt(1, idComprador);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) compras.add(mapear(rs));
            }
            return compras;
        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao listar as compras do comprador.", erro);
        }
    }

    @Override
    public ArrayList<Compra> listarPorVendedor(Integer idVendedor) {
        ArrayList<Compra> compras = new ArrayList<>();
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement ps = conexao.prepareStatement(SQL_LISTAR_VENDEDOR)) {
            ps.setInt(1, idVendedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) compras.add(mapear(rs));
            }
            return compras;
        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao listar as compras dos anuncios do vendedor.", erro);
        }
    }

    private DadosAnuncio bloquearAnuncio(Connection conexao, Integer idAnuncio) throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(SQL_ANUNCIO_BLOQUEAR)) {
            ps.setInt(1, idAnuncio);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new DadosAnuncio(rs.getInt("id_vendedor"),
                        rs.getBigDecimal("quantidade"), rs.getBigDecimal("preco"),
                        rs.getString("status_anuncio"));
            }
        }
    }

    private java.time.LocalDateTime consultarDataCompra(Connection conexao, Integer idCompra)
            throws SQLException {
        try (PreparedStatement ps = conexao.prepareStatement(
                "SELECT data_compra FROM compra WHERE id_compra = ?")) {
            ps.setInt(1, idCompra);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp data = rs.getTimestamp("data_compra");
                    return data == null ? null : data.toLocalDateTime();
                }
            }
        }
        return null;
    }

    private Compra mapear(ResultSet rs) throws SQLException {
        Compra compra = new Compra();
        compra.setIdCompra(rs.getInt("id_compra"));
        compra.setIdAnuncio(rs.getInt("id_anuncio"));
        compra.setIdComprador(rs.getInt("id_comprador"));
        compra.setQuantidade(rs.getDouble("quantidade"));
        compra.setPreco(rs.getDouble("preco"));
        compra.setStatus(rs.getString("status"));
        Timestamp data = rs.getTimestamp("data_compra");
        if (data != null) compra.setDataCompra(data.toLocalDateTime());
        return compra;
    }

    private BigDecimal normalizarQuantidade(double quantidade) {
        if (!Double.isFinite(quantidade) || quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        try {
            return BigDecimal.valueOf(quantidade).setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException erro) {
            throw new IllegalArgumentException(
                    "A quantidade deve possuir no maximo duas casas decimais.", erro);
        }
    }

    private void validarCompra(Compra compra) {
        if (compra == null) throw new IllegalArgumentException("Compra nao informada.");
        if (compra.getIdAnuncio() == null || compra.getIdAnuncio() <= 0) {
            throw new IllegalArgumentException("Informe um anuncio valido.");
        }
        if (compra.getIdComprador() == null || compra.getIdComprador() <= 0) {
            throw new IllegalArgumentException("Informe um comprador valido.");
        }
        normalizarQuantidade(compra.getQuantidade());
    }

    private static class DadosAnuncio {
        private final Integer idVendedor;
        private final BigDecimal quantidade;
        private final BigDecimal precoUnitario;
        private final String status;

        private DadosAnuncio(Integer idVendedor, BigDecimal quantidade,
                             BigDecimal precoUnitario, String status) {
            this.idVendedor = idVendedor;
            this.quantidade = quantidade;
            this.precoUnitario = precoUnitario;
            this.status = status;
        }
    }
}
