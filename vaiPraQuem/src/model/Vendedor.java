package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Vendedor extends Pessoa{
    private ArrayList<Anuncio> anuncios;

    public Vendedor() {
        super();
        this.anuncios = new ArrayList<>();
    }

    public Vendedor(Integer idPessoa, String tipoPessoa, String documento, String nome, String email, String telefone, String senhaHash, LocalDateTime dataCadastro, ArrayList<Anuncio> anuncios) {
        super(idPessoa, tipoPessoa, documento, nome, email, telefone, senhaHash, dataCadastro);
        this.anuncios = anuncios;
    }

    public ArrayList<Anuncio> getAnuncios() {
        return anuncios;
    }

    public void setAnuncios(ArrayList<Anuncio> anuncios) {
        this.anuncios = anuncios;
    }
}
