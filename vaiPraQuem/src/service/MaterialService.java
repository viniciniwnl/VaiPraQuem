package service;

import dao.IMaterialDao;
import dao.MaterialDao;
import model.Categoria;
import model.Material;

import java.util.ArrayList;

public class MaterialService {

    private final IMaterialDao materialDao;

    public MaterialService() {
        this.materialDao = new MaterialDao();
    }

    // Construtor alternativo para facilitar testes com DAO simulado.
    public MaterialService(IMaterialDao materialDao) {
        this.materialDao = materialDao;
    }

    public ArrayList<Material> listarTodos() {
        return materialDao.listarTodos();
    }

    public ArrayList<Material> listarPorCategoria(Integer idCategoria) {
        if (idCategoria == null || idCategoria <= 0) {
            throw new IllegalArgumentException("Informe um ID de categoria válido.");
        }

        // Confirma que a categoria existe antes de consultar seus materiais.
        consultarCategoria(idCategoria);
        return materialDao.listarPorCategoria(idCategoria);
    }

    public Material consultar(Integer idMaterial) {
        if (idMaterial == null || idMaterial <= 0) {
            throw new IllegalArgumentException("Informe um ID de material válido.");
        }

        Material material = materialDao.consultar(idMaterial);
        if (material == null) {
            throw new IllegalArgumentException("Material " + idMaterial + " não encontrado.");
        }
        return material;
    }

    public ArrayList<Categoria> listarCategorias() {
        return materialDao.listarCategorias();
    }

    public Categoria consultarCategoria(Integer idCategoria) {
        if (idCategoria == null || idCategoria <= 0) {
            throw new IllegalArgumentException("Informe um ID de categoria válido.");
        }

        Categoria categoria = materialDao.consultarCategoria(idCategoria);
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria " + idCategoria + " não encontrada.");
        }
        return categoria;
    }
}
