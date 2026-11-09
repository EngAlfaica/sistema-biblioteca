package estrutura;

import modelo.Livro;
import java.util.ArrayList;
import java.util.List;

/**
 * Árvore AVL (Binária de Pesquisa auto-balanceada) que indexa os Livros
 * pela chave ISBN. Garante O(log n) em inserção, pesquisa e remoção.
 */
public class ArvoreAVL {

    /** Nó interno da árvore. Não é exposto fora desta classe. */
    private class No {
        Livro livro;
        No esquerda, direita;
        int altura;

        No(Livro livro) {
            this.livro = livro;
            this.altura = 1; // folha nova tem altura 1
        }
    }

    private No raiz;
    private int quantidadeLivros;

    public int getQuantidadeLivros() {
        return quantidadeLivros;
    }

    /** Altura actual da árvore (usada para confirmar visualmente o balanceamento nos testes). */
    public int getAltura() {
        return altura(raiz);
    }

    // ---------- Funções auxiliares de balanceamento ----------

    private int altura(No no) {
        return (no == null) ? 0 : no.altura;
    }

    private int fatorBalanceamento(No no) {
        return (no == null) ? 0 : altura(no.esquerda) - altura(no.direita);
    }

    private void atualizarAltura(No no) {
        no.altura = 1 + Math.max(altura(no.esquerda), altura(no.direita));
    }

    /** Rotação simples à direita — usada quando a subárvore esquerda está pesada. */
    private No rotacaoDireita(No y) {
        No x = y.esquerda;
        No t2 = x.direita;

        x.direita = y;
        y.esquerda = t2;

        atualizarAltura(y);
        atualizarAltura(x);

        return x; // nova raiz desta subárvore
    }

    /** Rotação simples à esquerda — usada quando a subárvore direita está pesada. */
    private No rotacaoEsquerda(No x) {
        No y = x.direita;
        No t2 = y.esquerda;

        y.esquerda = x;
        x.direita = t2;

        atualizarAltura(x);
        atualizarAltura(y);

        return y; // nova raiz desta subárvore
    }

    /** Reequilibra o nó, aplicando rotação simples ou dupla consoante o caso. */
    private No reequilibrar(No no) {
        atualizarAltura(no);
        int fb = fatorBalanceamento(no);

        // Caso Esquerda-Esquerda
        if (fb > 1 && fatorBalanceamento(no.esquerda) >= 0) {
            return rotacaoDireita(no);
        }
        // Caso Esquerda-Direita (rotação dupla)
        if (fb > 1 && fatorBalanceamento(no.esquerda) < 0) {
            no.esquerda = rotacaoEsquerda(no.esquerda);
            return rotacaoDireita(no);
        }
        // Caso Direita-Direita
        if (fb < -1 && fatorBalanceamento(no.direita) <= 0) {
            return rotacaoEsquerda(no);
        }
        // Caso Direita-Esquerda (rotação dupla)
        if (fb < -1 && fatorBalanceamento(no.direita) > 0) {
            no.direita = rotacaoDireita(no.direita);
            return rotacaoEsquerda(no);
        }

        return no; // já estava balanceado
    }

    // ---------- Inserção ----------

    public void inserir(Livro livro) {
        raiz = inserir(raiz, livro);
    }

    private No inserir(No no, Livro livro) {
        if (no == null) {
            quantidadeLivros++;
            return new No(livro);
        }

        int cmp = livro.getIsbn().compareTo(no.livro.getIsbn());
        if (cmp < 0) {
            no.esquerda = inserir(no.esquerda, livro);
        } else if (cmp > 0) {
            no.direita = inserir(no.direita, livro);
        } else {
            // ISBN já existe: actualiza o livro em vez de duplicar
            no.livro = livro;
            return no;
        }

        return reequilibrar(no);
    }

    // ---------- Pesquisa ----------

    public Livro pesquisar(String isbn) {
        No atual = raiz;
        while (atual != null) {
            int cmp = isbn.compareTo(atual.livro.getIsbn());
            if (cmp == 0) return atual.livro;
            atual = (cmp < 0) ? atual.esquerda : atual.direita;
        }
        return null; // não encontrado
    }

    // ---------- Remoção ----------

    public boolean remover(String isbn) {
        if (pesquisar(isbn) == null) {
            return false;
        }
        raiz = remover(raiz, isbn);
        quantidadeLivros--;
        return true;
    }

    private No remover(No no, String isbn) {
        if (no == null) return null;

        int cmp = isbn.compareTo(no.livro.getIsbn());
        if (cmp < 0) {
            no.esquerda = remover(no.esquerda, isbn);
        } else if (cmp > 0) {
            no.direita = remover(no.direita, isbn);
        } else {
            // Nó encontrado: três casos clássicos de remoção em BST
            if (no.esquerda == null) {
                return no.direita;
            } else if (no.direita == null) {
                return no.esquerda;
            } else {
                // Dois filhos: substitui pelo sucessor (menor da subárvore direita)
                No sucessor = menorNo(no.direita);
                no.livro = sucessor.livro;
                no.direita = remover(no.direita, sucessor.livro.getIsbn());
            }
        }

        return reequilibrar(no);
    }

    private No menorNo(No no) {
        while (no.esquerda != null) {
            no = no.esquerda;
        }
        return no;
    }

    // ---------- Percurso (para listar todos os livros) ----------

    /** Devolve todos os livros ordenados por ISBN (percurso em-ordem). */
    public List<Livro> listarTodos() {
        List<Livro> resultado = new ArrayList<>();
        emOrdem(raiz, resultado);
        return resultado;
    }

    private void emOrdem(No no, List<Livro> resultado) {
        if (no == null) return;
        emOrdem(no.esquerda, resultado);
        resultado.add(no.livro);
        emOrdem(no.direita, resultado);
    }

    /** Pesquisa livros cujo título contenha o texto dado (case-insensitive). Percorre toda a árvore. */
    public List<Livro> pesquisarPorTitulo(String textoParcial) {
        List<Livro> resultado = new ArrayList<>();
        String alvo = textoParcial.toLowerCase();
        pesquisarPorTitulo(raiz, alvo, resultado);
        return resultado;
    }

    private void pesquisarPorTitulo(No no, String alvo, List<Livro> resultado) {
        if (no == null) return;
        pesquisarPorTitulo(no.esquerda, alvo, resultado);
        if (no.livro.getTitulo().toLowerCase().contains(alvo)) {
            resultado.add(no.livro);
        }
        pesquisarPorTitulo(no.direita, alvo, resultado);
    }
}