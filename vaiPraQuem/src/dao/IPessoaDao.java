package dao;

import model.Comprador;
import model.Pessoa;
import model.Vendedor;

import java.util.ArrayList;

public interface IPessoaDao {
    public Pessoa cadastrar(Pessoa pessoa);

    public Pessoa consultar(Integer idPessoa);

    public boolean excluir(Integer idPessoa);

    public Pessoa habilitarComoVendedor(Integer idPessoa);

    public Pessoa habilitarComoComprador(Integer idPessoa);

    Pessoa consultarPorEmail(String email);

    //ArrayList<Vendedor> vendedores();

    //ArrayList<Comprador> compradores();
}
