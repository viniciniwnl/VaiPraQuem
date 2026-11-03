package model;

public class Material {
    private Integer idMaterial;
    private Integer idCategoria;
    private String nome;
    private Categoria categoria;

    public Material() {
    }

    // Construtor mantido para compatibilidade com o código anterior.
    public Material(Integer idMaterial, Integer idCategoria, String nome) {
        this.idMaterial = idMaterial;
        this.idCategoria = idCategoria;
        this.nome = nome;
    }

    public Material(Integer idMaterial, String nome, Categoria categoria) {
        this.idMaterial = idMaterial;
        this.nome = nome;
        setCategoria(categoria);
    }

    public Integer getIdMaterial() {
        return idMaterial;
    }

    public void setIdMaterial(Integer idMaterial) {
        this.idMaterial = idMaterial;
    }

    public Integer getIdCategoria() {
        if (categoria != null) {
            return categoria.getIdCategoria();
        }
        return idCategoria;
    }

    public void setIdCategoria(Integer idCategoria) {
        this.idCategoria = idCategoria;
        if (categoria != null && !idCategoria.equals(categoria.getIdCategoria())) {
            categoria = null;
        }
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
        this.idCategoria = categoria == null ? null : categoria.getIdCategoria();
    }

    @Override
    public String toString() {
        String nomeCategoria = categoria == null ? "Sem categoria" : categoria.getNome();
        return nome + " (" + nomeCategoria + ")";
    }
}
