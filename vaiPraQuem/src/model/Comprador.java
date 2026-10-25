package model;

import java.time.LocalDateTime;

public class Comprador extends Pessoa{

    public Comprador() {
        super();
    }

    public Comprador(Integer idPessoa, String tipoPessoa, String documento, String nome, String email, String telefone, String senhaHash, LocalDateTime dataCadastro) {
        super(idPessoa, tipoPessoa, documento, nome, email, telefone, senhaHash, dataCadastro);
    }
}
