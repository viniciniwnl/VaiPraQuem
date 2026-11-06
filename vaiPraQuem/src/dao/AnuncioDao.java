package dao;

import factory.ConnectionFactory;
import model.Anuncio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

public class AnuncioDao implements IAnuncioDao {

    private static final String COLUNAS =
            "a.id_anuncio, a.id_vendedor, a.id_material, a.id_endereco, a.titulo, a.descricao, "
                    + "a.quantidade, a.unidade, a.preco, a.status_anuncio, a.data_publicacao, p.nome AS nome_vendedor";

    // status_anuncio e data_publicacao são omitidos para o banco aplicar seus valores padrão.
    private static final String SQL_CADASTRAR =
            "INSERT INTO anuncio (id_vendedor, id_material, id_endereco, titulo, descricao, quantidade, unidade, preco) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_ATUALIZAR =
            "UPDATE anuncio SET id_material = ?, id_endereco = ?, titulo = ?, descricao = ?, "
                    + "quantidade = ?, unidade = ?, preco = ? "
                    + "WHERE id_anuncio = ? AND id_vendedor = ?";

    private static final String SQL_EXCLUIR =
            "DELETE FROM anuncio WHERE id_anuncio = ? AND id_vendedor = ?";

    private static final String SQL_LISTAR_DISPONIVEIS =
            "SELECT " + COLUNAS + " FROM anuncio a " +
                    "INNER JOIN pessoa p ON p.id_pessoa = a.id_vendedor " +
                    "WHERE a.status_anuncio = 'DISPONIVEL' AND a.quantidade > 0 " +
                    "ORDER BY a.data_publicacao DESC";

    private static final String SQL_LISTAR_VENDEDOR =
            "SELECT " + COLUNAS + " FROM anuncio a " +
                    "INNER JOIN pessoa p ON p.id_pessoa = a.id_vendedor " +
                    "WHERE a.id_vendedor = ? ORDER BY a.data_publicacao DESC";

    @Override
    public Anuncio cadastrar(Anuncio anuncio) {
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement ps = conexao.prepareStatement(SQL_CADASTRAR, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, anuncio.getIdVendedor());
            ps.setInt(2, anuncio.getIdMaterial());
            ps.setInt(3, anuncio.getIdEndereco());
            ps.setString(4, anuncio.getTitulo());
            ps.setString(5, anuncio.getDescricao());
            ps.setDouble(6, anuncio.getQuantidade());
            ps.setString(7, anuncio.getUnidade());
            ps.setDouble(8, anuncio.getPreco());

            if (ps.executeUpdate() == 0) {
                throw new SQLException("Nenhum anúncio foi cadastrado.");
            }

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    anuncio.setIdAnuncio(chaves.getInt(1));
                } else {
                    throw new SQLException("O banco não retornou o ID do anúncio.");
                }
            }
            return anuncio;

        } catch (SQLIntegrityConstraintViolationException erro) {
            throw new RuntimeException("Vendedor, material ou endereço inválido. Confira os IDs informados.", erro);
        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao cadastrar anúncio.", erro);
        }
    }

    @Override
    public boolean atualizar(Anuncio anuncio) {
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement ps = conexao.prepareStatement(SQL_ATUALIZAR)) {

            ps.setInt(1, anuncio.getIdMaterial());
            ps.setInt(2, anuncio.getIdEndereco());
            ps.setString(3, anuncio.getTitulo());
            ps.setString(4, anuncio.getDescricao());
            ps.setDouble(5, anuncio.getQuantidade());
            ps.setString(6, anuncio.getUnidade());
            ps.setDouble(7, anuncio.getPreco());
            ps.setInt(8, anuncio.getIdAnuncio());
            ps.setInt(9, anuncio.getIdVendedor());

            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException erro) {
            throw new RuntimeException("Material ou endereço inválido. Confira os IDs informados.", erro);
        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao atualizar anúncio.", erro);
        }
    }

    @Override
    public boolean excluir(Integer idAnuncio, Integer idVendedor) {
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement ps = conexao.prepareStatement(SQL_EXCLUIR)) {

            ps.setInt(1, idAnuncio);
            ps.setInt(2, idVendedor);
            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException erro) {
            throw new RuntimeException("O anúncio não pode ser excluído porque está relacionado a uma compra.", erro);
        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao excluir anúncio.", erro);
        }
    }

    @Override
    public ArrayList<Anuncio> listarDisponiveis() {
        ArrayList<Anuncio> anuncios = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement ps = conexao.prepareStatement(SQL_LISTAR_DISPONIVEIS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                anuncios.add(mapear(rs));
            }
            return anuncios;

        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao listar anúncios disponíveis.", erro);
        }
    }

    @Override
    public ArrayList<Anuncio> listarPorVendedor(Integer idVendedor) {
        ArrayList<Anuncio> anuncios = new ArrayList<>();

        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement ps = conexao.prepareStatement(SQL_LISTAR_VENDEDOR)) {

            ps.setInt(1, idVendedor);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    anuncios.add(mapear(rs));
                }
            }
            return anuncios;

        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao listar anúncios do vendedor.", erro);
        }
    }

    private Anuncio mapear(ResultSet rs) throws SQLException {
        Anuncio anuncio = new Anuncio();
        anuncio.setIdAnuncio(rs.getInt("id_anuncio"));
        anuncio.setIdVendedor(rs.getInt("id_vendedor"));
        anuncio.setNomeVendedor(rs.getString("nome_vendedor"));
        anuncio.setIdMaterial(rs.getInt("id_material"));
        anuncio.setIdEndereco(rs.getInt("id_endereco"));
        anuncio.setTitulo(rs.getString("titulo"));
        anuncio.setDescricao(rs.getString("descricao"));
        anuncio.setQuantidade(rs.getDouble("quantidade"));
        anuncio.setUnidade(rs.getString("unidade"));
        anuncio.setPreco(rs.getDouble("preco"));
        anuncio.setStatusAnuncio(rs.getString("status_anuncio"));

        Timestamp data = rs.getTimestamp("data_publicacao");
        if (data != null) {
            anuncio.setDataPublicacao(data.toLocalDateTime());
        }
        return anuncio;
    }
}
