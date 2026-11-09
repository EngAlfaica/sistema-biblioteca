package persistencia;

import gestao.GestorBiblioteca;
import modelo.Emprestimo;
import modelo.Livro;
import modelo.Utilizador;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Grava e carrega os dados da biblioteca (livros, utilizadores, empréstimos)
 * em ficheiros de texto simples, um registo por linha, campos separados por "|".
 * Não usa serialização binária nem bibliotecas externas, para o formato
 * ficar legível e fácil de inspeccionar/depurar num editor de texto.
 */
public class PersistenciaDados {

    private static final String PASTA = "dados";
    private static final String FICHEIRO_LIVROS = PASTA + File.separator + "livros.txt";
    private static final String FICHEIRO_UTILIZADORES = PASTA + File.separator + "utilizadores.txt";
    private static final String FICHEIRO_EMPRESTIMOS = PASTA + File.separator + "emprestimos.txt";

    private static final String SEPARADOR = "\\|";
    private static final String MARCA_VAZIO = "-"; // usado para datas nulas (empréstimo ainda não devolvido)

    // ==================== GUARDAR ====================

    public static void guardarTudo(GestorBiblioteca gestor) throws IOException {
        Files.createDirectories(Paths.get(PASTA));
        guardarLivros(gestor.listarTodosOsLivros());
        guardarUtilizadores(gestor.listarTodosOsUtilizadores());
        guardarEmprestimos(gestor.listarTodoHistorico());
    }

    private static void guardarLivros(List<Livro> livros) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FICHEIRO_LIVROS), StandardCharsets.UTF_8)) {
            for (Livro l : livros) {
                writer.write(String.join("|",
                        l.getIsbn(), l.getTitulo(), l.getAutor(),
                        String.valueOf(l.getAnoPublicacao()),
                        String.valueOf(l.getExemplaresTotal()),
                        String.valueOf(l.getExemplaresDisponiveis())));
                writer.newLine();
            }
        }
    }

    private static void guardarUtilizadores(List<Utilizador> utilizadores) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FICHEIRO_UTILIZADORES), StandardCharsets.UTF_8)) {
            for (Utilizador u : utilizadores) {
                writer.write(String.join("|",
                        u.getNumeroUtilizador(), u.getNome(), u.getEmail(), u.getTelefone()));
                writer.newLine();
            }
        }
    }

    private static void guardarEmprestimos(List<Emprestimo> emprestimos) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FICHEIRO_EMPRESTIMOS), StandardCharsets.UTF_8)) {
            for (Emprestimo e : emprestimos) {
                String dataDevolucaoReal = (e.getDataDevolucaoReal() == null)
                        ? MARCA_VAZIO
                        : e.getDataDevolucaoReal().toString();

                writer.write(String.join("|",
                        e.getIsbnLivro(), e.getNumeroUtilizador(),
                        e.getDataEmprestimo().toString(),
                        e.getDataPrevistaDevolucao().toString(),
                        dataDevolucaoReal));
                writer.newLine();
            }
        }
    }

    // ==================== CARREGAR ====================

    public static void carregarTudo(GestorBiblioteca gestor) throws IOException {
        carregarLivros(gestor);
        carregarUtilizadores(gestor);
        carregarEmprestimos(gestor);
    }

    private static void carregarLivros(GestorBiblioteca gestor) throws IOException {
        Path caminho = Paths.get(FICHEIRO_LIVROS);
        if (!Files.exists(caminho)) return; // primeira vez a correr: ainda não há ficheiro

        for (String linha : Files.readAllLines(caminho, StandardCharsets.UTF_8)) {
            if (linha.isBlank()) continue;
            String[] campos = linha.split(SEPARADOR, -1);

            Livro livro = new Livro(
                    campos[0], campos[1], campos[2],
                    Integer.parseInt(campos[3]), Integer.parseInt(campos[4]));
            livro.setExemplaresDisponiveis(Integer.parseInt(campos[5]));

            gestor.inserirLivro(livro);
        }
    }

    private static void carregarUtilizadores(GestorBiblioteca gestor) throws IOException {
        Path caminho = Paths.get(FICHEIRO_UTILIZADORES);
        if (!Files.exists(caminho)) return;

        for (String linha : Files.readAllLines(caminho, StandardCharsets.UTF_8)) {
            if (linha.isBlank()) continue;
            String[] campos = linha.split(SEPARADOR, -1);

            Utilizador utilizador = new Utilizador(campos[0], campos[1], campos[2], campos[3]);
            gestor.registarUtilizador(utilizador);
        }
    }

    private static void carregarEmprestimos(GestorBiblioteca gestor) throws IOException {
        Path caminho = Paths.get(FICHEIRO_EMPRESTIMOS);
        if (!Files.exists(caminho)) return;

        for (String linha : Files.readAllLines(caminho, StandardCharsets.UTF_8)) {
            if (linha.isBlank()) continue;
            String[] campos = linha.split(SEPARADOR, -1);

            LocalDate dataEmprestimo = LocalDate.parse(campos[2]);
            LocalDate dataPrevistaDevolucao = LocalDate.parse(campos[3]);

            Emprestimo emprestimo = new Emprestimo(campos[0], campos[1], dataEmprestimo, dataPrevistaDevolucao);

            if (!campos[4].equals(MARCA_VAZIO)) {
                emprestimo.registarDevolucao(LocalDate.parse(campos[4]));
            }

            gestor.carregarEmprestimoBruto(emprestimo);
        }
    }
}