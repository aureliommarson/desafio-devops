package br.edu.cidadesesg.model;

public enum StatusIniciativa {
    PLANEJADA("Planejada"),
    EM_ANDAMENTO("Em andamento"),
    CONCLUIDA("Concluída");

    private final String descricao;

    StatusIniciativa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
