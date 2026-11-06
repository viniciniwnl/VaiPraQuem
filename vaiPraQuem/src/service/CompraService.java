package service;

import dao.CompraDao;
import dao.ICompraDao;
import model.Compra;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class CompraService {

    private final ICompraDao compraDao;

    public CompraService() {
        this.compraDao = new CompraDao();
    }

    // Construtor alternativo para facilitar testes do Service.
    public CompraService(ICompraDao compraDao) {
        if (compraDao == null) {
            throw new IllegalArgumentException("O DAO de compra nao foi informado.");
        }
        this.compraDao = compraDao;
    }

    // O DAO calcula o valor total e atualiza o estoque atomicamente.
    public Compra registrarCompra(Integer idComprador, Integer idAnuncio, double quantidade) {
        validarId(idComprador, "comprador");
        validarId(idAnuncio, "anuncio");
        validarQuantidade(quantidade);

        Compra compra = new Compra();
        compra.setIdComprador(idComprador);
        compra.setIdAnuncio(idAnuncio);
        compra.setQuantidade(quantidade);
        return compraDao.registrar(compra);
    }

    public ArrayList<Compra> listarPorComprador(Integer idComprador) {
        validarId(idComprador, "comprador");
        return compraDao.listarPorComprador(idComprador);
    }

    public ArrayList<Compra> listarPorVendedor(Integer idVendedor) {
        validarId(idVendedor, "vendedor");
        return compraDao.listarPorVendedor(idVendedor);
    }

    private void validarId(Integer id, String entidade) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Informe um ID valido para " + entidade + ".");
        }
    }

    private void validarQuantidade(double quantidade) {
        if (!Double.isFinite(quantidade) || quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        try {
            BigDecimal.valueOf(quantidade).setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException erro) {
            throw new IllegalArgumentException(
                    "A quantidade deve possuir no maximo duas casas decimais.", erro);
        }
    }
}
