package modelo;

/**
 * Representa um livro na biblioteca.
 * O ISBN é usado como chave de pesquisa na Árvore AVL.
 */
public class Livro {

    private String isbn;
    private String titulo;
    private String autor;
    private int anoPublicacao;
    private int exemplaresTotal;
    private int exemplaresDisponiveis;
    private String genero = "";   // NOVO (etapa 3): vazio quando o livro não tem género

    public Livro(String isbn, String titulo, String autor, int anoPublicacao, int exemplaresTotal) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.anoPublicacao = anoPublicacao;
        this.exemplaresTotal = exemplaresTotal;
        this.exemplaresDisponiveis = exemplaresTotal;
    }

    // --- Getters e Setters ---

    public String getIsbn() {
        return isbn;
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

    public int getAnoPublicacao() {
        return anoPublicacao;
    }

    public void setAnoPublicacao(int anoPublicacao) {
        this.anoPublicacao = anoPublicacao;
    }

    public int getExemplaresTotal() {
        return exemplaresTotal;
    }

    public void setExemplaresTotal(int exemplaresTotal) {
        this.exemplaresTotal = exemplaresTotal;
    }

    public int getExemplaresDisponiveis() {
        return exemplaresDisponiveis;
    }

    /** Usado pela persistência para restaurar o número exacto de exemplares disponíveis ao carregar do ficheiro. */
    public void setExemplaresDisponiveis(int exemplaresDisponiveis) {
        this.exemplaresDisponiveis = exemplaresDisponiveis;
    }

    // NOVO (etapa 3)
    public String getGenero() {
        return genero;
    }

    // NOVO (etapa 3)
    public void setGenero(String genero) {
        this.genero = (genero == null) ? "" : genero.trim();
    }

    /**
     * Reduz em 1 os exemplares disponíveis (chamado ao emprestar).
     * @return true se havia exemplar disponível e a operação foi feita.
     */
    public boolean reservarExemplar() {
        if (exemplaresDisponiveis > 0) {
            exemplaresDisponiveis--;
            return true;
        }
        return false;
    }

    /**
     * Aumenta em 1 os exemplares disponíveis (chamado ao devolver).
     */
    public void libertarExemplar() {
        if (exemplaresDisponiveis < exemplaresTotal) {
            exemplaresDisponiveis++;
        }
    }

    public boolean temExemplarDisponivel() {
        return exemplaresDisponiveis > 0;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s — %s (%d) | Disponíveis: %d/%d",
                isbn, titulo, autor, anoPublicacao, exemplaresDisponiveis, exemplaresTotal);
    }
}