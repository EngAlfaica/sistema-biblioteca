package estrutura;

import modelo.Utilizador;
import java.util.ArrayList;
import java.util.List;

/**
 * Lista Ligada simples que guarda os Utilizadores registados na biblioteca.
 * O volume de utilizadores tende a ser bem menor que o de livros, por isso
 * uma lista ligada (sem balanceamento) é suficiente aqui.
 */
public class ListaUtilizadores {

    private class No {
        Utilizador utilizador;
        No proximo;

        No(Utilizador utilizador) {
            this.utilizador = utilizador;
        }
    }

    private No cabeca;
    private int quantidade;

    public int getQuantidade() {
        return quantidade;
    }

    /** Adiciona um novo utilizador, se o número ainda não estiver em uso. */
    public boolean adicionar(Utilizador utilizador) {
        if (pesquisar(utilizador.getNumeroUtilizador()) != null) {
            return false; // já existe um utilizador com este número
        }
        No novo = new No(utilizador);
        novo.proximo = cabeca; // inserção no início, O(1)
        cabeca = novo;
        quantidade++;
        return true;
    }

    public Utilizador pesquisar(String numeroUtilizador) {
        No atual = cabeca;
        while (atual != null) {
            if (atual.utilizador.getNumeroUtilizador().equals(numeroUtilizador)) {
                return atual.utilizador;
            }
            atual = atual.proximo;
        }
        return null;
    }

    public boolean remover(String numeroUtilizador) {
        No anterior = null;
        No atual = cabeca;
        while (atual != null) {
            if (atual.utilizador.getNumeroUtilizador().equals(numeroUtilizador)) {
                if (anterior == null) {
                    cabeca = atual.proximo;
                } else {
                    anterior.proximo = atual.proximo;
                }
                quantidade--;
                return true;
            }
            anterior = atual;
            atual = atual.proximo;
        }
        return false;
    }

    public List<Utilizador> listarTodos() {
        List<Utilizador> resultado = new ArrayList<>();
        No atual = cabeca;
        while (atual != null) {
            resultado.add(atual.utilizador);
            atual = atual.proximo;
        }
        return resultado;
    }
}