package dao;

import model.Anuncio;
import java.util.ArrayList;

public interface IAnuncioDao {

    // Cadastra e devolve o anúncio com o ID gerado pelo MySQL.
    Anuncio cadastrar(Anuncio anuncio);

    // Atualiza somente se o anúncio pertencer ao vendedor informado.
    boolean atualizar(Anuncio anuncio);

    // Exclui somente se o anúncio pertencer ao vendedor informado.
    boolean excluir(Integer idAnuncio, Integer idVendedor);

    // Lista somente anúncios disponíveis e com estoque maior que zero.
    ArrayList<Anuncio> listarDisponiveis();

    // Lista os anúncios de um vendedor.
    ArrayList<Anuncio> listarPorVendedor(Integer idVendedor);
}
