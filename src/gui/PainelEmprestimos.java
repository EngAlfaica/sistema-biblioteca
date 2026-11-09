package gui;

import gestao.GestorBiblioteca;
import modelo.Emprestimo;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Página de Empréstimos: botão "Novo empréstimo" em cima e tabela dos
 * empréstimos activos, com um ícone de visto (registar devolução) em cada linha.
 */
public class PainelEmprestimos extends JPanel {

    private final GestorBiblioteca gestor;
    private TabelaComIcones<Emprestimo> tabela;

    public PainelEmprestimos(GestorBiblioteca gestor) {
        this.gestor = gestor;
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(Estilos.BRANCO);

        add(criarBarraSuperior(), BorderLayout.NORTH);
        add(criarTabela(), BorderLayout.CENTER);

        atualizarTabela();
    }

    private JPanel criarBarraSuperior() {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Estilos.BRANCO);

        JLabel titulo = new JLabel("Empréstimos activos");
        titulo.setFont(Estilos.FONTE_SUBTITULO);

        JButton botaoActualizar = new JButton("Actualizar lista");
        JButton botaoNovo = new JButton("+ Novo empréstimo");
        Estilos.estilizarBotaoPrimario(botaoNovo);
        botaoActualizar.addActionListener(e -> atualizarTabela());
        botaoNovo.addActionListener(e -> novoEmprestimo());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botoes.setBackground(Estilos.BRANCO);
        botoes.add(botaoActualizar);
        botoes.add(botaoNovo);

        barra.add(titulo, BorderLayout.WEST);
        barra.add(botoes, BorderLayout.EAST);
        return barra;
    }

    private JScrollPane criarTabela() {
        tabela = new TabelaComIcones<>(
                new String[]{"ISBN", "Utilizador", "Data empréstimo", "Prazo devolução", "Estado"},
                new int[]{110, 110, 150, 150, 110},
                new Icon[]{Icones.devolver(Estilos.VERDE_OK)});

        // Coluna "Estado" (índice 4): chip verde (em curso) ou vermelho (em atraso)
        tabela.getColumnModel().getColumn(4).setCellRenderer(Estilos.chipEstado());

        // único ícone (0) = devolver
        tabela.aoClicar((emprestimo, icone) -> devolver(emprestimo));

        JScrollPane scroll = new JScrollPane(tabela);
        scroll.getViewport().setBackground(Estilos.BRANCO);
        return scroll;
    }

    private void novoEmprestimo() {
        Dialogo.Formulario f = new Dialogo.Formulario("Novo empréstimo", "Emprestar");
        JTextField isbn = f.campo("ISBN do livro", "", true);
        JTextField numero = f.campo("Número do utilizador", "", true);
        JTextField dias = f.campo("Prazo (dias)", "7", true); // prazo por defeito: 7 dias
        if (!f.mostrar(this)) return;

        if (isbn.getText().trim().isEmpty() || numero.getText().trim().isEmpty()) {
            aviso("Indica o ISBN e o número do utilizador.");
            return;
        }

        int diasPrazo;
        try {
            diasPrazo = Integer.parseInt(dias.getText().trim());
        } catch (NumberFormatException ex) {
            diasPrazo = 0;
        }
        if (diasPrazo < 1 || diasPrazo > 60) {
            aviso("O prazo deve ser um número de dias entre 1 e 60.");
            return;
        }

        Emprestimo e = gestor.emprestarLivro(isbn.getText().trim(), numero.getText().trim(), diasPrazo);
        if (e != null) {
            atualizarTabela();
            Dialogo.info(this, "Empréstimo registado. Prazo de devolução: " + e.getDataPrevistaDevolucao());
        } else {
            aviso("Não foi possível emprestar: livro/utilizador não existe, ou não há exemplar disponível.");
        }
    }

    private void devolver(Emprestimo emprestimo) {
        if (!Dialogo.confirmar(this, "Registar devolução",
                "Registar a devolução do livro " + emprestimo.getIsbnLivro()
                        + " (utilizador " + emprestimo.getNumeroUtilizador() + ")?",
                "Registar devolução", false)) return;

        if (gestor.devolverLivro(emprestimo.getIsbnLivro())) {
            atualizarTabela();
        } else {
            aviso("Não há empréstimo activo para esse livro.");
        }
    }

    private void aviso(String texto) {
        Dialogo.aviso(this, texto);
    }

    public void atualizarTabela() {
        tabela.carregar(gestor.listarEmprestimosActivos(), e -> new Object[]{
                e.getIsbnLivro(), e.getNumeroUtilizador(),
                e.getDataEmprestimo(), e.getDataPrevistaDevolucao(),
                e.isAtrasado() ? "Em atraso" : "Em curso"});
    }
}