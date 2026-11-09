package estrutura;

import modelo.Emprestimo;
import java.util.ArrayList;
import java.util.List;

/**
 * Lista Ligada simples que guarda o histórico de Empréstimos.
 * Novos empréstimos são adicionados ao fim (mantém ordem cronológica).
 * Não precisa de balanceamento: o uso típico é inserir no fim e percorrer,
 * ao contrário dos Livros, que exigem pesquisa rápida por ISBN.
 */
public class ListaLigada {

    /** Nó interno da lista. Não é exposto fora desta classe. */
    private class No {
        Emprestimo emprestimo;
        No proximo;

        No(Emprestimo emprestimo) {
            this.emprestimo = emprestimo;
        }
    }

    private No cabeca; // primeiro nó
    private No cauda;  // último nó (permite inserir no fim em O(1))
    private int quantidade;

    public int getQuantidade() {
        return quantidade;
    }

    public boolean estaVazia() {
        return cabeca == null;
    }

    // ---------- Inserção ----------

    /** Adiciona um novo empréstimo ao fim da lista. O(1) graças ao ponteiro 'cauda'. */
    public void adicionar(Emprestimo emprestimo) {
        No novo = new No(emprestimo);
        if (estaVazia()) {
            cabeca = novo;
            cauda = novo;
        } else {
            cauda.proximo = novo;
            cauda = novo;
        }
        quantidade++;
    }

    // ---------- Remoção ----------

    /**
     * Remove o primeiro empréstimo em curso (não devolvido) que corresponda
     * ao ISBN e ao utilizador dados. Usado, por exemplo, se for preciso
     * cancelar um empréstimo por engano.
     */
    public boolean remover(String isbnLivro, String numeroUtilizador) {
        No anterior = null;
        No atual = cabeca;

        while (atual != null) {
            Emprestimo e = atual.emprestimo;
            if (e.getIsbnLivro().equals(isbnLivro)
                    && e.getNumeroUtilizador().equals(numeroUtilizador)
                    && !e.isDevolvido()) {

                if (anterior == null) {
                    cabeca = atual.proximo;
                } else {
                    anterior.proximo = atual.proximo;
                }
                if (atual == cauda) {
                    cauda = anterior;
                }
                quantidade--;
                return true;
            }
            anterior = atual;
            atual = atual.proximo;
        }
        return false;
    }

    // ---------- Pesquisa ----------

    /** Devolve o empréstimo em curso (ainda não devolvido) de um dado livro, ou null se não existir. */
    public Emprestimo pesquisarEmprestimoActivo(String isbnLivro) {
        No atual = cabeca;
        while (atual != null) {
            if (atual.emprestimo.getIsbnLivro().equals(isbnLivro) && !atual.emprestimo.isDevolvido()) {
                return atual.emprestimo;
            }
            atual = atual.proximo;
        }
        return null;
    }

    /** Devolve todos os empréstimos (activos e já devolvidos) de um utilizador. */
    public List<Emprestimo> listarPorUtilizador(String numeroUtilizador) {
        List<Emprestimo> resultado = new ArrayList<>();
        No atual = cabeca;
        while (atual != null) {
            if (atual.emprestimo.getNumeroUtilizador().equals(numeroUtilizador)) {
                resultado.add(atual.emprestimo);
            }
            atual = atual.proximo;
        }
        return resultado;
    }

    /** Devolve todos os empréstimos actualmente em curso (não devolvidos). */
    public List<Emprestimo> listarActivos() {
        List<Emprestimo> resultado = new ArrayList<>();
        No atual = cabeca;
        while (atual != null) {
            if (!atual.emprestimo.isDevolvido()) {
                resultado.add(atual.emprestimo);
            }
            atual = atual.proximo;
        }
        return resultado;
    }

    /** Devolve todo o histórico, na ordem em que os empréstimos foram feitos. */
    public List<Emprestimo> listarTodos() {
        List<Emprestimo> resultado = new ArrayList<>();
        No atual = cabeca;
        while (atual != null) {
            resultado.add(atual.emprestimo);
            atual = atual.proximo;
        }
        return resultado;
    }
}