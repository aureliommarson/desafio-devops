package br.edu.cidadesesg.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "iniciativas")
public class Iniciativa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o título")
    @Size(max = 120, message = "O título deve ter no máximo 120 caracteres")
    @Column(nullable = false, length = 120)
    private String titulo;

    @NotBlank(message = "Informe a descrição")
    @Size(max = 1000, message = "A descrição deve ter no máximo 1000 caracteres")
    @Column(nullable = false, length = 1000)
    private String descricao;

    @NotBlank(message = "Informe a cidade")
    @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String cidade;

    @NotNull(message = "Selecione a categoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Categoria categoria;

    @NotNull(message = "Selecione o status")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusIniciativa status;

    @NotNull(message = "Informe a pontuação de impacto")
    @Min(value = 1, message = "A pontuação deve ser no mínimo 1")
    @Max(value = 10, message = "A pontuação deve ser no máximo 10")
    @Column(nullable = false)
    private Integer pontuacaoImpacto;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    public Iniciativa() {
    }

    public Iniciativa(String titulo, String descricao, String cidade, Categoria categoria,
                      StatusIniciativa status, Integer pontuacaoImpacto) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.cidade = cidade;
        this.categoria = categoria;
        this.status = status;
        this.pontuacaoImpacto = pontuacaoImpacto;
    }

    @PrePersist
    public void preencherDataCriacao() {
        if (dataCriacao == null) {
            dataCriacao = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
    public StatusIniciativa getStatus() { return status; }
    public void setStatus(StatusIniciativa status) { this.status = status; }
    public Integer getPontuacaoImpacto() { return pontuacaoImpacto; }
    public void setPontuacaoImpacto(Integer pontuacaoImpacto) { this.pontuacaoImpacto = pontuacaoImpacto; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
