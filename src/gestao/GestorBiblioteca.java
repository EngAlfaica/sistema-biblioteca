package gestao;

import estrutura.ArvoreAVL;
import estrutura.ListaLigada;
import estrutura.ListaUtilizadores;
import modelo.Emprestimo;
import modelo.Livro;
import modelo.Utilizador;

import java.time.LocalDate;
import java.util.ArrayList;   // NOVO (etapa 3)
import java.util.List;
import java.util.Set;         // NOVO (etapa 3)
import java.util.TreeSet;     // NOVO (etapa 3)

/**
 * Camada de gestão que junta a Árvore AVL (livros), a Lista Ligada de
 * Utilizadores e a Lista Ligada de Empréstimos, aplicando as regras de
 * negócio da biblioteca. É esta classe que a GUI vai chamar.
 */
public class GestorBiblioteca {

    private static final int PRAZO_EMPRESTIMO_DIAS = 7; // prazo por defeito, em dias

    private final ArvoreAVL livros = new ArvoreAVL();
    private final ListaUtilizadores utilizadores = new ListaUtilizadores();
    private final ListaLigada emprestimos = new ListaLigada();

    // ---------- Livros ----------

    public void inserirLivro(Livro livro) {
        livros.inserir(livro);
    }

    public Livro pesquisarLivroPorIsbn(String isbn) {
        return livros.pesquisar(isbn);
    }

    public List<Livro> pesquisarLivrosPorTitulo(String titulo) {
        return livros.pesquisarPorTitulo(titulo);
    }

    public List<Livro> listarTodosOsLivros() {
        return livros.listarTodos();
    }

    /**
     * NOVO (etapa 3): pesquisa livros. O texto procura no título, autor e ISBN;
     * o género é um filtro exacto ("" = todos os géneros).
     */
    public List<Livro> pesquisar(String texto, String genero) {
        String alvo = (texto == null) ? "" : texto.trim().toLowerCase();
        List<Livro> resultado = new ArrayList<>();

        for (Livro l : livros.listarTodos()) {
            boolean generoOk = genero == null || genero.isEmpty() || l.getGenero().equalsIgnoreCase(genero);
            boolean textoOk = alvo.isEmpty()
                    || l.getTitulo().toLowerCase().contains(alvo)
                    || l.getAutor().toLowerCase().contains(alvo)
                    || l.getIsbn().toLowerCase().contains(alvo);

            if (generoOk && textoOk) {
                resultado.add(l);
            }
        }
        return resultado;
    }

    /** NOVO (etapa 3): géneros que existem nos livros, sem repetições e por ordem alfabética. */
    public List<String> listarGeneros() {
        Set<String> generos = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        for (Livro l : livros.listarTodos()) {
            if (!l.getGenero().isEmpty()) {
                generos.add(l.getGenero());
            }
        }
        return new ArrayList<>(generos);
    }

    public boolean actualizarLivro(String isbn, String novoTitulo, String novoAutor, int novoAno) {
        Livro existente = livros.pesquisar(isbn);
        if (existente == null) {
            return false;
        }
        existente.setTitulo(novoTitulo);
        existente.setAutor(novoAutor);
        existente.setAnoPublicacao(novoAno);
        return true;
    }

    public boolean removerLivro(String isbn) {
        // Regra de negócio: não permitir remover um livro com empréstimo activo
        if (emprestimos.pesquisarEmprestimoActivo(isbn) != null) {
            return false;
        }
        return livros.remover(isbn);
    }

    public int getQuantidadeLivros() {
        return livros.getQuantidadeLivros();
    }

    // ---------- Utilizadores ----------

    public boolean registarUtilizador(Utilizador utilizador) {
        return utilizadores.adicionar(utilizador);
    }

    public Utilizador pesquisarUtilizador(String numero) {
        return utilizadores.pesquisar(numero);
    }

    public List<Utilizador> listarTodosOsUtilizadores() {
        return utilizadores.listarTodos();
    }

    public boolean removerUtilizador(String numero) {
        return utilizadores.remover(numero);
    }

    // ---------- Empréstimos ----------

    /**
     * Regista um novo empréstimo, validando:
     * - se o livro existe e tem exemplar disponível
     * - se o utilizador está registado
     * @return o Emprestimo criado, ou null se alguma validação falhar
     */
    public Emprestimo emprestarLivro(String isbn, String numeroUtilizador) {
        return emprestarLivro(isbn, numeroUtilizador, PRAZO_EMPRESTIMO_DIAS);
    }

    /** Igual ao anterior, mas o bibliotecário escolhe o prazo (em dias) deste empréstimo. */
    public Emprestimo emprestarLivro(String isbn, String numeroUtilizador, int diasPrazo) {
        if (diasPrazo < 1) {
            return null;
        }
        Livro livro = livros.pesquisar(isbn);
        if (livro == null || !livro.temExemplarDisponivel()) {
            return null;
        }
        Utilizador utilizador = utilizadores.pesquisar(numeroUtilizador);
        if (utilizador == null) {
            return null;
        }

        livro.reservarExemplar();
        Emprestimo emprestimo = new Emprestimo(isbn, numeroUtilizador, LocalDate.now(), diasPrazo);
        emprestimos.adicionar(emprestimo);
        return emprestimo;
    }

    /**
     * Regista a devolução de um livro. Liberta o exemplar na AVL e marca
     * o empréstimo correspondente como devolvido.
     */
    public boolean devolverLivro(String isbn) {
        Emprestimo emprestimo = emprestimos.pesquisarEmprestimoActivo(isbn);
        if (emprestimo == null) {
            return false;
        }
        emprestimo.registarDevolucao(LocalDate.now());

        Livro livro = livros.pesquisar(isbn);
        if (livro != null) {
            livro.libertarExemplar();
        }
        return true;
    }

    /**
     * Adiciona um empréstimo já existente directamente à lista, sem passar
     * pelas validações de emprestarLivro() nem alterar exemplares do livro.
     * Usado apenas pela persistência, ao recarregar dados do ficheiro.
     */
    public void carregarEmprestimoBruto(Emprestimo emprestimo) {
        emprestimos.adicionar(emprestimo);
    }

    public List<Emprestimo> listarEmprestimosActivos() {
        return emprestimos.listarActivos();
    }

    public List<Emprestimo> listarHistoricoDoUtilizador(String numeroUtilizador) {
        return emprestimos.listarPorUtilizador(numeroUtilizador);
    }

    public List<Emprestimo> listarTodoHistorico() {
        return emprestimos.listarTodos();
    }
}