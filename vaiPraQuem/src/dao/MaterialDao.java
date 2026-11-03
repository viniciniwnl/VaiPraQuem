package dao;

import factory.ConnectionFactory;
import model.Categoria;
import model.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MaterialDao implements IMaterialDao {

    private static final String SQL_LISTAR_MATERIAIS =
            "SELECT m.id_material, m.nome AS nome_material, " +
                    "c.id_categoria, c.nome AS nome_categoria " +
                    "FROM material m " +
                    "INNER JOIN categoria c ON c.id_categoria = m.id_categoria " +
                    "ORDER BY FIELD(c.nome, 'Construção Civil', 'Madeira e Marcenaria', " +
                    "'Recicláveis e Sucata', 'Óleo de cozinha usado'), m.nome";

    private static final String SQL_LISTAR_POR_CATEGORIA =
            "SELECT m.id_material, m.nome AS nome_material, " +
                    "c.id_categoria, c.nome AS nome_categoria " +
                    "FROM material m " +
                    "INNER JOIN categoria c ON c.id_categoria = m.id_categoria " +
                    "WHERE c.id_categoria = ? " +
                    "ORDER BY m.nome";

    private static final String SQL_CONSULTAR_MATERIAL =
            "SELECT m.id_material, m.nome AS nome_material, " +
                    "c.id_categoria, c.nome AS nome_categoria " +
                    "FROM material m " +
                    "INNER JOIN categoria c ON c.id_categoria = m.id_categoria " +
                    "WHERE m.id_material = ?";

    private static final String SQL_LISTAR_CATEGORIAS =
            "SELECT id_categoria, nome FROM categoria " +
                    "ORDER BY FIELD(nome, 'Construção Civil', 'Madeira e Marcenaria', " +
                    "'Recicláveis e Sucata', 'Óleo de cozinha usado')";

    private static final String SQL_CONSULTAR_CATEGORIA =
            "SELECT id_categoria, nome FROM categoria WHERE id_categoria = ?";

    @Override
    public ArrayList<Material> listarTodos() {
        ArrayList<Material> materiais = new ArrayList<>();

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_MATERIAIS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                materiais.add(mapearMaterial(rs));
            }
            return materiais;

        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao listar os materiais.", erro);
        }
    }

    @Override
    public ArrayList<Material> listarPorCategoria(Integer idCategoria) {
        ArrayList<Material> materiais = new ArrayList<>();

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_CATEGORIA)) {

            ps.setInt(1, idCategoria);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    materiais.add(mapearMaterial(rs));
                }
            }
            return materiais;

        } catch (SQLException erro) {
            throw new RuntimeException(
                    "Erro ao listar os materiais da categoria " + idCategoria + ".", erro);
        }
    }

    @Override
    public Material consultar(Integer idMaterial) {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_CONSULTAR_MATERIAL)) {

            ps.setInt(1, idMaterial);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearMaterial(rs);
                }
            }
            return null;

        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao consultar o material " + idMaterial + ".", erro);
        }
    }

    @Override
    public ArrayList<Categoria> listarCategorias() {
        ArrayList<Categoria> categorias = new ArrayList<>();

        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_CATEGORIAS);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                categorias.add(mapearCategoria(rs));
            }
            return categorias;

        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao listar as categorias.", erro);
        }
    }

    @Override
    public Categoria consultarCategoria(Integer idCategoria) {
        try (Connection con = ConnectionFactory.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_CONSULTAR_CATEGORIA)) {

            ps.setInt(1, idCategoria);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCategoria(rs);
                }
            }
            return null;

        } catch (SQLException erro) {
            throw new RuntimeException("Erro ao consultar a categoria " + idCategoria + ".", erro);
        }
    }

    private Material mapearMaterial(ResultSet rs) throws SQLException {
        Categoria categoria = new Categoria(
                rs.getInt("id_categoria"),
                rs.getString("nome_categoria")
        );

        Material material = new Material();
        material.setIdMaterial(rs.getInt("id_material"));
        material.setNome(rs.getString("nome_material"));
        material.setCategoria(categoria);
        return material;
    }

    private Categoria mapearCategoria(ResultSet rs) throws SQLException {
        return new Categoria(
                rs.getInt("id_categoria"),
                rs.getString("nome")
        );
    }
}
