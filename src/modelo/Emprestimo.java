package modelo;

import java.time.LocalDate;

/**
 * Representa um empréstimo de um livro a um utilizador.
 * Cada nó da Lista Ligada de empréstimos guarda um objecto deste tipo.
 */
public class Emprestimo {

    private String isbnLivro;
    private String numeroUtilizador;
    private LocalDate dataEmprestimo;
    private LocalDate dataPrevistaDevolucao;
    private LocalDate dataDevolucaoReal; // null enquanto não devolvido

    public Emprestimo(String isbnLivro, String numeroUtilizador, LocalDate dataEmprestimo, int diasPrazo) {
        this.isbnLivro = isbnLivro;
        this.numeroUtilizador = numeroUtilizador;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevistaDevolucao = dataEmprestimo.plusDays(diasPrazo);
        this.dataDevolucaoReal = null;
    }

    /**
     * Construtor usado pela persistência: reconstrói um empréstimo já existente,
     * com a data prevista de devolução guardada tal como estava no ficheiro
     * (em vez de recalculada a partir de um prazo em dias).
     */
    public Emprestimo(String isbnLivro, String numeroUtilizador, LocalDate dataEmprestimo, LocalDate dataPrevistaDevolucao) {
        this.isbnLivro = isbnLivro;
        this.numeroUtilizador = numeroUtilizador;
        this.dataEmprestimo = dataEmprestimo;
        this.dataPrevistaDevolucao = dataPrevistaDevolucao;
        this.dataDevolucaoReal = null;
    }

    public String getIsbnLivro() {
        return isbnLivro;
    }

    public String getNumeroUtilizador() {
        return numeroUtilizador;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate getDataPrevistaDevolucao() {
        return dataPrevistaDevolucao;
    }

    public LocalDate getDataDevolucaoReal() {
        return dataDevolucaoReal;
    }

    public boolean isDevolvido() {
        return dataDevolucaoReal != null;
    }

    public void registarDevolucao(LocalDate data) {
        this.dataDevolucaoReal = data;
    }

    public boolean isAtrasado() {
        if (isDevolvido()) {
            return dataDevolucaoReal.isAfter(dataPrevistaDevolucao);
        }
        return LocalDate.now().isAfter(dataPrevistaDevolucao);
    }

    @Override
    public String toString() {
        String estado = isDevolvido()
                ? "Devolvido em " + dataDevolucaoReal
                : (isAtrasado() ? "EM ATRASO" : "Em curso");
        return String.format("Livro %s -> Utilizador %s | Emprestado: %s | Prazo: %s | %s",
                isbnLivro, numeroUtilizador, dataEmprestimo, dataPrevistaDevolucao, estado);
    }
}