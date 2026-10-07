package br.edu.cidadesesg.model;

public enum Categoria {
    AMBIENTAL("Ambiental"),
    SOCIAL("Social"),
    GOVERNANCA("Governança");

    private final String descricao;

    Categoria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
