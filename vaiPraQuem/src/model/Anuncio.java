package model;

import java.time.LocalDateTime;

public class Anuncio {
    private Integer idAnuncio;
    private Integer idVendedor;
    private Integer idMaterial;
    private Integer idEndereco;
    private String titulo;
    private String descricao;
    private double quantidade;
    private String unidade;
    private double preco;
    private String statusAnuncio;
    private LocalDateTime dataPublicacao;

    public Anuncio(){
    }

    public Anuncio(Integer idAnuncio, Integer idVendedor, Integer idMaterial, Integer idEndereco, String titulo, String descricao, double quantidade, String unidade, double preco, String statusAnuncio, LocalDateTime dataPublicacao) {
        this.idAnuncio = idAnuncio;
        this.idVendedor = idVendedor;
        this.idMaterial = idMaterial;
        this.idEndereco = idEndereco;
        this.titulo = titulo;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.unidade = unidade;
        this.preco = preco;
        this.statusAnuncio = statusAnuncio;
        this.dataPublicacao = dataPublicacao;
    }

    public Integer getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(Integer idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public Integer getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(Integer idVendedor) {
        this.idVendedor = idVendedor;
    }

    public Integer getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Integer idMaterial) {
        this.idMaterial = idMaterial;
    }

    public Integer getIdEndereco() {
        return idEndereco;
    }

    public void setIdEndereco(Integer idEndereco) {
        this.idEndereco = idEndereco;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getStatusAnuncio() {
        return statusAnuncio;
    }

    public void setStatusAnuncio(String statusAnuncio) {
        this.statusAnuncio = statusAnuncio;
    }

    public LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(LocalDateTime dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }
}
