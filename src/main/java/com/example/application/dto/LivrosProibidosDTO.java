package com.example.application.dto;

import java.time.LocalDate;
import java.util.Objects;

public class LivrosProibidosDTO {
    private Integer id;
    private String titulo;
    private String autor;
    private String descricao;
    private LocalDate dataPublicacao;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(LocalDate dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LivrosProibidosDTO that = (LivrosProibidosDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(titulo, that.titulo) && Objects.equals(autor, that.autor) && Objects.equals(descricao, that.descricao) && Objects.equals(dataPublicacao, that.dataPublicacao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, titulo, autor, descricao, dataPublicacao);
    }
}
