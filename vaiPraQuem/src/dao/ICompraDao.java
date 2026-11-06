package dao;

import model.Compra;
import java.util.ArrayList;

public interface ICompraDao {

    // Registra a compra como CONCLUIDA e atualiza o estoque na mesma transacao.
    Compra registrar(Compra compra);

    // Historico de compras realizadas por um comprador.
    ArrayList<Compra> listarPorComprador(Integer idComprador);

    // Compras realizadas nos anuncios de um vendedor.
    ArrayList<Compra> listarPorVendedor(Integer idVendedor);
}
