package model;

import java.time.LocalDateTime;

public class Compra {
    private Integer idCompra;
    private Integer idAnuncio;
    private Integer idComprador;
    private double quantidade;
    private double preco;
    private LocalDateTime dataCompra;
    private String status;

    public Compra(){
    }

    public Compra(Integer idCompra, Integer idAnuncio, Integer idComprador, double quantidade, double preco, LocalDateTime dataCompra, String status) {
        this.idCompra = idCompra;
        this.idAnuncio = idAnuncio;
        this.idComprador = idComprador;
        this.quantidade = quantidade;
        this.preco = preco;
        this.dataCompra = dataCompra;
        this.status = status;
    }

    public Integer getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(Integer idCompra) {
        this.idCompra = idCompra;
    }

    public Integer getIdAnuncio() {
        return idAnuncio;
    }

    public void setIdAnuncio(Integer idAnuncio) {
        this.idAnuncio = idAnuncio;
    }

    public Integer getIdComprador() {
        return idComprador;
    }

    public void setIdComprador(Integer idComprador) {
        this.idComprador = idComprador;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(double quantidade) {
        this.quantidade = quantidade;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public LocalDateTime getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(LocalDateTime dataCompra) {
        this.dataCompra = dataCompra;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
