package model;

public class Material {
    private Integer idMaterial;
    private Integer idCategoria;
    private String nome;

    public Material(){
    }

    public Material(Integer idMaterial, Integer idCategoria, String nome) {
        this.idMaterial = idMaterial;
        this.idCategoria = idCategoria;
        this.nome = nome;
    }

    public Integer getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Integer idMaterial) {
        this.idMaterial = idMaterial;
    }

    public Integer getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
