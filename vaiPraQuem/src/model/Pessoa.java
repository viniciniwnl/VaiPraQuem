package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class Pessoa {
    private Integer idPessoa;
    private String tipoPessoa;
    private String documento;
    private String nome;
    private String email;
    private String telefone;
    private String senhaHash;
    private LocalDateTime dataCadastro;
    private ArrayList<Endereco> enderecos = new ArrayList<>();

    public Pessoa(){
    }

    public Pessoa(Integer idPessoa, String tipoPessoa, String documento, String nome, String email, String telefone, String senhaHash, LocalDateTime dataCadastro) {
        this.idPessoa = idPessoa;
        this.tipoPessoa = tipoPessoa;
        this.documento = documento;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.senhaHash = senhaHash;
        this.dataCadastro = dataCadastro;
    }

    public void adicionarEndereco(Endereco endereco) {
        enderecos.add(endereco);
    }

    public Integer getIdPessoa() {
        return idPessoa;
    }

    public void setIdPessoa(Integer idPessoa) {
        this.idPessoa = idPessoa;
    }

    public String getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(String tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public ArrayList<Endereco> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(ArrayList<Endereco> enderecos) {
        this.enderecos = enderecos;
    }

}
