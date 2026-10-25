package dao;

import java.sql.*;

import factory.ConnectionFactory;
import model.Pessoa;
import model.Vendedor;
import model.Comprador;

public class PessoaDao implements IPessoaDao {
    private static final String COLUNAS =
            "p.id_pessoa, p.tipo_pessoa, p.documento, p.nome, p.email, p.telefone, p.senha_hash, p.data_cadastro, "
                    + "EXISTS (SELECT 1 FROM vendedor xv WHERE xv.id_pessoa = p.id_pessoa) AS eh_vendedor, "
                    + "EXISTS (SELECT 1 FROM comprador xc WHERE xc.id_pessoa = p.id_pessoa) AS eh_comprador";

    private static final String SQL_CADASTRAR =
            "INSERT INTO pessoa (tipo_pessoa, documento, nome, email, telefone, senha_hash) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_CONSULTAR =
            "SELECT " + COLUNAS + " FROM pessoa p WHERE p.id_pessoa = ?";

    private static final String SQL_EXCLUIR =
            "DELETE FROM pessoa WHERE id_pessoa = ?";

    private static final String SQL_HABILITAR_VENDEDOR =
            "INSERT INTO vendedor (id_pessoa) VALUES (?)";

    private static final String SQL_HABILITAR_COMPRADOR =
            "INSERT INTO comprador (id_pessoa) VALUES (?)";

    private static final String SQL_VENDEDORES =
            "SELECT " + COLUNAS + " FROM pessoa p JOIN vendedor ve ON ve.id_pessoa = p.id_pessoa ORDER BY p.nome";

    private static final String SQL_COMPRADORES =
            "SELECT " + COLUNAS + " FROM pessoa p JOIN comprador co ON co.id_pessoa = p.id_pessoa ORDER BY p.nome";

    private static final String SQL_CONSULTAR_EMAIL =
            "SELECT " + COLUNAS + " FROM pessoa p WHERE p.email = ?";

    @Override
    public Pessoa cadastrar(Pessoa pessoa) {

        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(
                        SQL_CADASTRAR,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {

            ps.setString(1, pessoa.getTipoPessoa());
            ps.setString(2, pessoa.getDocumento());
            ps.setString(3, pessoa.getNome());
            ps.setString(4, pessoa.getEmail());
            ps.setString(5, pessoa.getTelefone());
            ps.setString(6, pessoa.getSenhaHash());

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas == 0) {
                throw new SQLException("Cadastro não realizado.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {
                    pessoa.setIdPessoa(rs.getInt(1));
                } else {
                    throw new SQLException("ID não retornado pelo banco.");
                }
            }

            return pessoa;

        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao cadastrar pessoa.", erro);
        }
    }

    @Override
    public Pessoa consultar(Integer idPessoa) {

        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps = conexao.prepareStatement(SQL_CONSULTAR)
        ) {
            ps.setInt(1, idPessoa);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapearPessoa(rs) : null;
            }

        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao consultar pessoa.", erro);
        }
    }

    @Override
    public Pessoa consultarPorEmail(String email) {

        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps =
                        conexao.prepareStatement(SQL_CONSULTAR_EMAIL)
        ) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapearPessoa(rs) : null;
            }

        } catch (SQLException erro) {
            throw new RuntimeException(
                    "Erro ao consultar pessoa pelo e-mail.", erro
            );
        }
    }

    private Pessoa mapearPessoa(ResultSet rs) throws SQLException {

        boolean ehVendedor = rs.getBoolean("eh_vendedor");
        boolean ehComprador = rs.getBoolean("eh_comprador");

        Pessoa pessoa;

        if (ehVendedor) {
            pessoa = new Vendedor();
        } else if (ehComprador) {
            pessoa = new Comprador();
        } else {
            throw new SQLException(
                    "Pessoa sem tipo concreto para representação."
            );
        }

        pessoa.setIdPessoa(rs.getInt("id_pessoa"));
        pessoa.setTipoPessoa(rs.getString("tipo_pessoa"));
        pessoa.setDocumento(rs.getString("documento"));
        pessoa.setNome(rs.getString("nome"));
        pessoa.setEmail(rs.getString("email"));
        pessoa.setTelefone(rs.getString("telefone"));
        pessoa.setSenhaHash(rs.getString("senha_hash"));

        Timestamp timestamp = rs.getTimestamp("data_cadastro");

        if (timestamp != null) {
            pessoa.setDataCadastro(timestamp.toLocalDateTime());
        }

        return pessoa;
    }

    @Override
    public Pessoa habilitarComoVendedor(Integer idPessoa) {

        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps =
                        conexao.prepareStatement(SQL_HABILITAR_VENDEDOR)
        ) {

            ps.setInt(1, idPessoa);

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas > 0) {
                return consultar(idPessoa);
            }

            return null;

        } catch (SQLException erro) {
            throw new RuntimeException(
                    "Erro ao habilitar pessoa como vendedor.", erro
            );
        }
    }


    @Override
    public Pessoa habilitarComoComprador(Integer idPessoa) {

        try (
                Connection conexao = ConnectionFactory.getConnection();
                PreparedStatement ps =
                        conexao.prepareStatement(SQL_HABILITAR_COMPRADOR)
        ) {

            ps.setInt(1, idPessoa);

            int linhasAfetadas = ps.executeUpdate();

            if (linhasAfetadas > 0) {
                return consultar(idPessoa);
            }

            return null;

        } catch (SQLException erro) {
            throw new RuntimeException(
                    "Erro ao habilitar pessoa como comprador.", erro
            );
        }
    }

    @Override
    public boolean excluir(Integer idPessoa) {
        try (Connection conexao = ConnectionFactory.getConnection();
             PreparedStatement ps = conexao.prepareStatement(SQL_EXCLUIR)) {

            ps.setInt(1, idPessoa);

            int linhasAfetadas = ps.executeUpdate();

            return linhasAfetadas > 0;

        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao excluir pessoa.", erro);
        }
    }


}

