package dao;

import model.Endereco;
import java.util.ArrayList;

public interface IEnderecoDao {

    public Endereco cadastrar(Endereco endereco);

    //Devolve o endereço ou null se não existir
    public Endereco consultar(Integer idEndereco);

    public boolean excluir(Integer idEndereco);

    // Todos os endereços de uma pessoa
    ArrayList<Endereco> enderecos(Integer idPessoa);

    // Apaga TODOS os endereços de uma pessoa e devolve quantos foram apagados
    public int excluirDaPessoa(Integer idPessoa);
}
