package dao;

import factory.ConnectionFactory;
import model.Endereco;
import dao.IEnderecoDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;

public class EnderecoDao implements IEnderecoDao {

    private static final String COLUNAS =
            "id_endereco, id_pessoa, logradouro, numero, complemento, bairro, cep, cidade, uf";

    private static final String SQL_CADASTRAR =
            "INSERT INTO endereco (id_pessoa, logradouro, numero, complemento, bairro, cep, cidade, uf) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_CONSULTAR =
            "SELECT " + COLUNAS + " FROM endereco WHERE id_endereco = ?";
    private static final String SQL_EXCLUIR =
            "DELETE FROM endereco WHERE id_endereco = ?";
    private static final String SQL_EXCLUIR_DA_PESSOA =
            "DELETE FROM endereco WHERE id_pessoa = ?";
    private static final String SQL_ENDERECOS =
            "SELECT " + COLUNAS + " FROM endereco WHERE id_pessoa = ? ORDER BY id_endereco";

    @Override
    public Endereco cadastrar(Endereco endereco) {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_CADASTRAR, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, endereco.getIdPessoa());
            ps.setString(2, endereco.getLogradouro());
            ps.setString(3, endereco.getNumero());
            ps.setString(4, endereco.getComplemento());     // null vira NULL no banco
            ps.setString(5, endereco.getBairro());
            ps.setString(6, endereco.getCep());
            ps.setString(7, endereco.getCidade());
            ps.setString(8, endereco.getUf());
            ps.executeUpdate();

            try (ResultSet chaves = ps.getGeneratedKeys()) {
                if (chaves.next()) {
                    endereco.setIdEndereco(chaves.getInt(1));
                }
            }
            return endereco;

        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao cadastrar o endereço.", erro);
        }
    }

    @Override
    public Endereco consultar(Integer idEndereco) {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_CONSULTAR)) {

            ps.setInt(1, idEndereco);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao consultar o endereço " + idEndereco + ".", erro);
        }
    }

    @Override
    public boolean excluir(Integer idEndereco) {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_EXCLUIR)) {

            ps.setInt(1, idEndereco);
            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException erro) {
            throw new RuntimeException("O endereço é local de retirada de algum anúncio e não pode ser excluído.", erro);
        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao excluir o endereço.", erro);
        }
    }

    @Override
    public int excluirDaPessoa(Integer idPessoa) {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_EXCLUIR_DA_PESSOA)) {

            ps.setInt(1, idPessoa);
            return ps.executeUpdate();     // quantidade de endereços apagados

        } catch (SQLIntegrityConstraintViolationException erro) {
            throw new RuntimeException("Algum endereço da pessoa é local de retirada de anúncio e não pode ser excluído.", erro);
        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao excluir os endereços da pessoa " + idPessoa + ".", erro);
        }
    }

    @Override
    public ArrayList<Endereco> enderecos(Integer idPessoa) {
        ArrayList<Endereco> lista = new ArrayList<>();
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_ENDERECOS)) {

            ps.setInt(1, idPessoa);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao listar os endereços da pessoa " + idPessoa + ".", erro);
        }
        return lista;
    }

    private Endereco mapear(ResultSet rs) throws SQLException {
        Endereco endereco = new Endereco();
        endereco.setIdEndereco(rs.getInt("id_endereco"));
        endereco.setIdPessoa(rs.getInt("id_pessoa"));
        endereco.setLogradouro(rs.getString("logradouro"));
        endereco.setNumero(rs.getString("numero"));
        endereco.setComplemento(rs.getString("complemento"));
        endereco.setBairro(rs.getString("bairro"));
        endereco.setCep(rs.getString("cep"));
        endereco.setCidade(rs.getString("cidade"));
        endereco.setUf(rs.getString("uf"));
        return endereco;
    }
}
