package gui;

import gestao.GestorBiblioteca;
import modelo.Emprestimo;
import modelo.Livro;
import modelo.Utilizador;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Página inicial. Responde a uma pergunta só: "o que precisa da minha atenção hoje?"
 *  - um número grande: empréstimos em atraso;
 *  - uma linha discreta com os totais (livros, utilizadores, empréstimos activos);
 *  - a lista dos atrasos, cada um com a acção de registar a devolução;
 *  - se não houver atrasos, uma mensagem positiva e o botão de novo empréstimo.
 */
public class PainelInicio extends JPanel {

    private final GestorBiblioteca gestor;
    private final Consumer<String> irPara; // pede à janela principal para mudar de página
    private final JPanel conteudo = new JPanel();

    public PainelInicio(GestorBiblioteca gestor, Consumer<String> irPara) {
        this.gestor = gestor;
        this.irPara = irPara;
        setLayout(new BorderLayout());
        setBackground(Estilos.BRANCO);
        setBorder(BorderFactory.createEmptyBorder(28, 32, 20, 32));

        conteudo.setLayout(new BoxLayout(conteudo, BoxLayout.Y_AXIS));
        conteudo.setBackground(Estilos.BRANCO);
        add(conteudo, BorderLayout.NORTH);
        atualizar();
    }

    /** Reconstrói a página com os dados de agora. */
    public void atualizar() {
        conteudo.removeAll();

        List<Emprestimo> atrasados = new ArrayList<>();
        for (Emprestimo e : gestor.listarEmprestimosActivos()) {
            if (e.isAtrasado()) atrasados.add(e);
        }
        int activos = gestor.listarEmprestimosActivos().size();

        conteudo.add(criarDestaque(atrasados.size()));
        conteudo.add(Box.createVerticalStrut(18));
        conteudo.add(criarTotais(activos));
        conteudo.add(Box.createVerticalStrut(28));

        if (atrasados.isEmpty()) {
            conteudo.add(criarEstadoVazio());
        } else {
            conteudo.add(criarListaAtrasos(atrasados));
        }
        conteudo.revalidate();
        conteudo.repaint();
    }

    // ---------- Número principal ----------

    private JComponent criarDestaque(int atrasos) {
        JPanel p = alinhado(new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)));

        JLabel numero = new JLabel(String.valueOf(atrasos));
        numero.setFont(Estilos.FONTE_TITULO.deriveFont(Font.BOLD, 54f));
        numero.setForeground(atrasos > 0 ? Estilos.VERMELHO_ATRASO : Estilos.TINTA);

        JLabel texto = new JLabel("<html>" + (atrasos == 1 ? "empréstimo em atraso" : "empréstimos em atraso")
                + "<br><span style='color:#6B7280;font-size:11px'>Passaram o prazo de devolução</span></html>");
        texto.setFont(Estilos.FONTE_SUBTITULO);
        texto.setForeground(Estilos.TINTA);
        texto.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 0));

        p.add(numero);
        p.add(texto);
        return p;
    }

    // ---------- Totais discretos ----------

    private JComponent criarTotais(int activos) {
        JPanel p = alinhado(new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)));
        p.add(total(gestor.getQuantidadeLivros(), "livros"));
        p.add(separador());
        p.add(total(gestor.listarTodosOsUtilizadores().size(), "utilizadores"));
        p.add(separador());
        p.add(total(activos, "empréstimos activos"));
        return p;
    }

    private JLabel total(int n, String legenda) {
        JLabel l = new JLabel("<html><b>" + n + "</b> " + legenda + "</html>");
        l.setFont(Estilos.FONTE_NORMAL);
        l.setForeground(Estilos.TEXTO_SUAVE);
        return l;
    }

    private JComponent separador() {
        JLabel l = new JLabel("   ·   ");
        l.setForeground(Estilos.LINHA_FINA.darker());
        return l;
    }

    // ---------- Lista de atrasos ----------

    private JComponent criarListaAtrasos(List<Emprestimo> atrasados) {
        JPanel bloco = alinhado(new JPanel());
        bloco.setLayout(new BoxLayout(bloco, BoxLayout.Y_AXIS));

        JLabel titulo = new JLabel("Precisa de atenção");
        titulo.setFont(Estilos.FONTE_SUBTITULO);
        titulo.setForeground(Estilos.TINTA);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        bloco.add(titulo);

        // linha de topo + uma linha por atraso, separadas por traços finos (sem "cartões")
        for (Emprestimo e : atrasados) {
            bloco.add(criarLinhaAtraso(e));
        }
        return bloco;
    }

    private JComponent criarLinhaAtraso(Emprestimo e) {
        Livro livro = gestor.pesquisarLivroPorIsbn(e.getIsbnLivro());
        Utilizador u = gestor.pesquisarUtilizador(e.getNumeroUtilizador());
        String nomeLivro = livro != null ? livro.getTitulo() : "ISBN " + e.getIsbnLivro();
        String nomeUtil = u != null ? u.getNome() : "Utilizador " + e.getNumeroUtilizador();
        long dias = ChronoUnit.DAYS.between(e.getDataPrevistaDevolucao(), LocalDate.now());

        JPanel linha = new JPanel(new BorderLayout(12, 0));
        linha.setBackground(Estilos.BRANCO);
        linha.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Estilos.LINHA_FINA),
                BorderFactory.createEmptyBorder(10, 0, 10, 0)));
        linha.setMaximumSize(new Dimension(760, 60));
        linha.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descricao = new JLabel("<html><b>" + escapar(nomeLivro) + "</b><br>"
                + "<span style='color:#6B7280'>" + escapar(nomeUtil) + "  ·  devia ter voltado a "
                + e.getDataPrevistaDevolucao() + "</span></html>");
        descricao.setFont(Estilos.FONTE_NORMAL);
        descricao.setForeground(Estilos.TINTA);

        JLabel atraso = new JLabel(dias + (dias == 1 ? " dia" : " dias") + " de atraso");
        atraso.setFont(Estilos.FONTE_NORMAL.deriveFont(Font.BOLD));
        atraso.setForeground(Estilos.VERMELHO_ATRASO);

        JButton devolver = new JButton("Registar devolução");
        devolver.addActionListener(ev -> {
            if (gestor.devolverLivro(e.getIsbnLivro())) {
                atualizar();
            }
        });

        JPanel direita = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        direita.setOpaque(false);
        direita.add(atraso);
        direita.add(devolver);

        linha.add(descricao, BorderLayout.CENTER);
        linha.add(direita, BorderLayout.EAST);
        return linha;
    }

    // ---------- Quando não há atrasos ----------

    private JComponent criarEstadoVazio() {
        JPanel p = alinhado(new JPanel());
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel msg = new JLabel("Tudo em dia. Nenhum empréstimo passou o prazo.");
        msg.setFont(Estilos.FONTE_SUBTITULO);
        msg.setForeground(Estilos.TINTA);
        msg.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton novo = new JButton("+ Novo empréstimo");
        Estilos.estilizarBotaoPrimario(novo);
        novo.addActionListener(e -> irPara.accept("emprestimos"));
        novo.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(msg);
        p.add(Box.createVerticalStrut(12));
        p.add(novo);
        return p;
    }

    // ---------- Auxiliares ----------

    private static <T extends JPanel> T alinhado(T p) {
        p.setBackground(Estilos.BRANCO);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        return p;
    }

    /** Evita que um título com "<" parta o HTML dos rótulos. */
    private static String escapar(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}