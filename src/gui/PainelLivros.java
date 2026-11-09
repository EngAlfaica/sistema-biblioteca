package gui;

import gestao.GestorBiblioteca;
import modelo.Livro;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Página de Livros: pesquisa + botão "Cadastrar" em cima, tabela com ícones
 * de editar e remover em cada linha. O formulário abre numa janelinha.
 */
public class PainelLivros extends JPanel {

    private final GestorBiblioteca gestor;
    private final JTextField campoPesquisa = new JTextField();
    private TabelaComIcones<Livro> tabela;

    public PainelLivros(GestorBiblioteca gestor) {
        this.gestor = gestor;
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(Estilos.BRANCO);

        add(criarBarraSuperior(), BorderLayout.NORTH);
        add(criarTabela(), BorderLayout.CENTER);

        atualizarTabela(gestor.listarTodosOsLivros());
    }

    private JPanel criarBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout(8, 0));
        barra.setBackground(Estilos.BRANCO);

        JButton botaoPesquisar = new JButton("Pesquisar por título");
        JButton botaoTodos = new JButton("Mostrar todos");
        JButton botaoCadastrar = new JButton("+ Cadastrar livro");
        Estilos.estilizarBotaoPrimario(botaoCadastrar);

        botaoPesquisar.addActionListener(e ->
                atualizarTabela(gestor.pesquisarLivrosPorTitulo(campoPesquisa.getText().trim())));
        campoPesquisa.addActionListener(e -> botaoPesquisar.doClick()); // Enter também pesquisa
        botaoTodos.addActionListener(e -> {
            campoPesquisa.setText("");
            atualizarTabela(gestor.listarTodosOsLivros());
        });
        botaoCadastrar.addActionListener(e -> abrirFormulario(null));

        JPanel esquerda = new JPanel(new BorderLayout(6, 0));
        esquerda.setBackground(Estilos.BRANCO);
        esquerda.add(new JLabel("Pesquisar:"), BorderLayout.WEST);
        esquerda.add(campoPesquisa, BorderLayout.CENTER);

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botoes.setBackground(Estilos.BRANCO);
        botoes.add(botaoPesquisar);
        botoes.add(botaoTodos);
        botoes.add(botaoCadastrar);

        barra.add(esquerda, BorderLayout.CENTER);
        barra.add(botoes, BorderLayout.EAST);
        return barra;
    }

    private JScrollPane criarTabela() {
        tabela = new TabelaComIcones<>(
                new String[]{"ISBN", "Título", "Autor", "Ano", "Disponíveis", "Total", "Estado"},
                new int[]{90, 210, 150, 55, 100, 60, 110},
                new Icon[]{Icones.editar(Estilos.AZUL_PRIMARIO), Icones.remover(Estilos.VERMELHO_ATRASO)});

        tabela.getColumnModel().getColumn(6).setCellRenderer(Estilos.chipEstado());

        // icone 0 = editar, 1 = remover
        tabela.aoClicar((livro, icone) -> {
            if (icone == 0) abrirFormulario(livro);
            else removerLivro(livro);
        });

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.getViewport().setBackground(Estilos.BRANCO);
        return scroll;
    }

    /** livro == null: cadastro novo; senão: edição desse livro. */
    private void abrirFormulario(Livro livro) {
        boolean edicao = livro != null;

        Dialogo.Formulario f = new Dialogo.Formulario(edicao ? "Editar livro" : "Cadastrar livro", "Guardar");
        JTextField isbn = f.campo("ISBN", edicao ? livro.getIsbn() : "", !edicao);               // o ISBN é a chave da árvore: não se muda
        JTextField titulo = f.campo("Título", edicao ? livro.getTitulo() : "", true);
        JTextField autor = f.campo("Autor", edicao ? livro.getAutor() : "", true);
        JTextField ano = f.campo("Ano de publicação", edicao ? String.valueOf(livro.getAnoPublicacao()) : "", true);
        JTextField exemplares = f.campo("Nº de exemplares", edicao ? String.valueOf(livro.getExemplaresTotal()) : "", !edicao);
        if (!f.mostrar(this)) return;

        try {
            String t = titulo.getText().trim();
            String a = autor.getText().trim();
            int anoNum = Integer.parseInt(ano.getText().trim());

            if (isbn.getText().trim().isEmpty() || t.isEmpty()) {
                aviso("ISBN e Título são obrigatórios.");
                return;
            }

            if (edicao) {
                gestor.actualizarLivro(livro.getIsbn(), t, a, anoNum);
            } else {
                int n = Integer.parseInt(exemplares.getText().trim());
                gestor.inserirLivro(new Livro(isbn.getText().trim(), t, a, anoNum, n));
            }
            atualizarTabela(gestor.listarTodosOsLivros());
        } catch (NumberFormatException ex) {
            aviso("Ano e Nº de exemplares devem ser números.");
        }
    }

    private void removerLivro(Livro livro) {
        if (!Dialogo.confirmar(this, "Remover livro",
                "Remover o livro \"" + livro.getTitulo() + "\"? Esta acção não pode ser desfeita.",
                "Remover", true)) return;

        if (gestor.removerLivro(livro.getIsbn())) {
            atualizarTabela(gestor.listarTodosOsLivros());
        } else {
            aviso("Não foi possível remover: o livro tem um empréstimo activo.");
        }
    }

    private void aviso(String texto) {
        Dialogo.aviso(this, texto);
    }

    /** Usado pela pesquisa global: escreve o texto no campo e mostra só os livros que coincidem. */
    public void mostrarPesquisa(String texto) {
        campoPesquisa.setText(texto);
        atualizarTabela(gestor.pesquisar(texto, ""));
    }

    /** Recarrega a tabela com a lista de livros dada. */
    public void atualizarTabela(List<Livro> livros) {
        tabela.carregar(livros, l -> new Object[]{
                l.getIsbn(), l.getTitulo(), l.getAutor(),
                l.getAnoPublicacao(), l.getExemplaresDisponiveis(), l.getExemplaresTotal(),
                l.getExemplaresDisponiveis() > 0 ? "Disponível" : "Esgotado"});
    }
}