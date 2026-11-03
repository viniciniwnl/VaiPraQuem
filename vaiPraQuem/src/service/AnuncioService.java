package service;

import dao.AnuncioDao;
import dao.IAnuncioDao;
import model.Anuncio;
import model.Endereco;

import java.util.ArrayList;

public class AnuncioService {

    private final IAnuncioDao anuncioDao;
    private final EnderecoService enderecoService;

    public AnuncioService() {
        this.anuncioDao = new AnuncioDao();
        this.enderecoService = new EnderecoService();
    }

    // Construtor alternativo para facilitar testes com DAOs simulados.
    public AnuncioService(IAnuncioDao anuncioDao, EnderecoService enderecoService) {
        this.anuncioDao = anuncioDao;
        this.enderecoService = enderecoService;
    }

    public Anuncio cadastrar(Anuncio anuncio) {
        validarDados(anuncio);
        validarEnderecoDoVendedor(anuncio.getIdVendedor(), anuncio.getIdEndereco());
        return anuncioDao.cadastrar(anuncio);
    }

    public boolean atualizar(Anuncio anuncio) {
        validarDados(anuncio);
        if (anuncio.getIdAnuncio() == null) {
            throw new IllegalArgumentException("Informe o ID do anúncio que deseja atualizar.");
        }
        validarEnderecoDoVendedor(anuncio.getIdVendedor(), anuncio.getIdEndereco());
        return anuncioDao.atualizar(anuncio);
    }

    public boolean excluir(Integer idAnuncio, Integer idVendedor) {
        if (idAnuncio == null || idVendedor == null) {
            throw new IllegalArgumentException("ID do anúncio e ID do vendedor são obrigatórios.");
        }
        return anuncioDao.excluir(idAnuncio, idVendedor);
    }

    public ArrayList<Anuncio> listarPorVendedor(Integer idVendedor) {
        if (idVendedor == null) {
            throw new IllegalArgumentException("Vendedor inválido.");
        }
        return anuncioDao.listarPorVendedor(idVendedor);
    }

    private void validarDados(Anuncio anuncio) {
        if (anuncio == null) {
            throw new IllegalArgumentException("Anúncio não informado.");
        }
        if (anuncio.getIdVendedor() == null || anuncio.getIdMaterial() == null
                || anuncio.getIdEndereco() == null) {
            throw new IllegalArgumentException("Vendedor, material e endereço são obrigatórios.");
        }
        obrigatorio(anuncio.getTitulo(), "título");
        obrigatorio(anuncio.getDescricao(), "descrição");
        obrigatorio(anuncio.getUnidade(), "unidade");
        if (anuncio.getQuantidade() <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        if (anuncio.getPreco() < 0) {
            throw new IllegalArgumentException("O preço não pode ser negativo.");
        }
        anuncio.setTitulo(anuncio.getTitulo().trim());
        anuncio.setDescricao(anuncio.getDescricao().trim());
        anuncio.setUnidade(anuncio.getUnidade().trim());
    }

    private void validarEnderecoDoVendedor(Integer idVendedor, Integer idEndereco) {
        ArrayList<Endereco> enderecos = enderecoService.listar(idVendedor);
        for (Endereco endereco : enderecos) {
            if (idEndereco.equals(endereco.getIdEndereco())) {
                return;
            }
        }
        throw new IllegalArgumentException("O endereço selecionado não pertence ao vendedor logado.");
    }

    private String obrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O campo " + campo + " é obrigatório.");
        }
        return valor;
    }
}
