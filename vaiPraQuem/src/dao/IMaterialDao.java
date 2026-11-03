package dao;

import java.util.ArrayList;
import model.Categoria;
import model.Material;

public interface IMaterialDao {

    // Lista todos os materiais junto com suas respectivas categorias.
    ArrayList<Material> listarTodos();

    // Lista os materiais pertencentes a uma categoria específica.
    ArrayList<Material> listarPorCategoria(Integer idCategoria);

    // Consulta um material pelo ID; retorna null se não existir.
    Material consultar(Integer idMaterial);

    // Lista todas as categorias cadastradas.
    ArrayList<Categoria> listarCategorias();

    // Consulta uma categoria pelo ID; retorna null se não existir.
    Categoria consultarCategoria(Integer idCategoria);
}
