package gui;

import gestao.GestorBiblioteca;
import modelo.Emprestimo;
import modelo.Livro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.List;

/**
 * Página de Relatórios (análise, não acção): uma linha de totais no topo e, por baixo,
 * os livros mais emprestados e um gráfico de empréstimos por mês.
 * A lista de atrasos vive na página Início (é lá que se actua sobre ela);
 * aqui o número de atrasos aparece apenas como um dos totais.
 */
public class PainelRelatorios extends JPanel {

    private final GestorBiblioteca gestor;

    private final JLabel totalLivros = new JLabel("0");
    private final JLabel totalExemplares = new JLabel("0");
    private final JLabel totalUtilizadores = new JLabel("0");
    private final JLabel totalActivos = new JLabel("0");
    private final JLabel totalAtrasados = new JLabel("0");

    private final DefaultTableModel modeloMaisEmprestados = criarModelo("Pos.", "ISBN", "Título", "Total");
    private final GraficoMeses grafico = new GraficoMeses();

    public PainelRelatorios(GestorBiblioteca gestor) {
        this.gestor = gestor;
        setLayout(new BorderLayout(0, 24));
        setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        setBackground(Estilos.BRANCO);

        add(criarTotais(), BorderLayout.NORTH);

        JPanel blocos = new JPanel(new GridLayout(1, 2, 32, 0));
        blocos.setBackground(Estilos.BRANCO);
        blocos.add(criarBlocoTabela("Livros mais emprestados", modeloMaisEmprestados, new int[]{50, 100, 200, 60}));
        blocos.add(criarBlocoGrafico("Empréstimos por mês"));
        add(blocos, BorderLayout.CENTER);

        atualizar();
    }

    private static DefaultTableModel criarModelo(String... colunas) {
        return new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
    }

    // ---------- Totais (texto simples separado por traços finos, sem cartões) ----------

    private JPanel criarTotais() {
        JPanel linha = new JPanel(new GridLayout(1, 5, 0, 0));
        linha.setBackground(Estilos.BRANCO);
        linha.add(criarTotal("Títulos", totalLivros, Estilos.TINTA, false));
        linha.add(criarTotal("Disponíveis", totalExemplares, Estilos.TINTA, true));
        linha.add(criarTotal("Utilizadores", totalUtilizadores, Estilos.TINTA, true));
        linha.add(criarTotal("Empréstimos activos", totalActivos, Estilos.TINTA, true));
        linha.add(criarTotal("Em atraso", totalAtrasados, Estilos.VERMELHO_ATRASO, true));
        return linha;
    }

    private JPanel criarTotal(String rotulo, JLabel valor, Color cor, boolean comTraco) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Estilos.BRANCO);
        p.setBorder(BorderFactory.createCompoundBorder(
                comTraco ? BorderFactory.createMatteBorder(0, 1, 0, 0, Estilos.LINHA_FINA)
                         : BorderFactory.createEmptyBorder(),
                BorderFactory.createEmptyBorder(0, comTraco ? 20 : 0, 0, 8)));

        valor.setFont(Estilos.FONTE_TITULO.deriveFont(Font.BOLD, 30f));
        valor.setForeground(cor);
        valor.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel texto = new JLabel(rotulo);
        texto.setFont(Estilos.FONTE_NORMAL);
        texto.setForeground(Estilos.TEXTO_SUAVE);
        texto.setAlignmentX(Component.LEFT_ALIGNMENT);

        p.add(valor);
        p.add(texto);
        return p;
    }

    // ---------- Blocos ----------

    private JPanel criarBlocoTabela(String titulo, DefaultTableModel modelo, int[] larguras) {
        JPanel bloco = new JPanel(new BorderLayout(0, 10));
        bloco.setBackground(Estilos.BRANCO);
        bloco.add(etiqueta(titulo), BorderLayout.NORTH);

        JTable tabela = new JTable(modelo);
        Estilos.estilizarTabela(tabela);
        for (int i = 0; i < larguras.length; i++) {
            tabela.getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }
        JScrollPane scroll = new JScrollPane(tabela);
        scroll.getViewport().setBackground(Estilos.BRANCO);
        bloco.add(scroll, BorderLayout.CENTER);
        return bloco;
    }

    private JPanel criarBlocoGrafico(String titulo) {
        JPanel bloco = new JPanel(new BorderLayout(0, 10));
        bloco.setBackground(Estilos.BRANCO);
        JPanel cima = new JPanel(new BorderLayout(0, 10));
        cima.setBackground(Estilos.BRANCO);
        cima.add(etiqueta(titulo), BorderLayout.NORTH);
        grafico.setPreferredSize(new Dimension(300, 260)); // altura fixa: o gráfico não estica com a janela
        cima.add(grafico, BorderLayout.CENTER);
        bloco.add(cima, BorderLayout.NORTH);
        return bloco;
    }

    private JLabel etiqueta(String titulo) {
        JLabel l = new JLabel(titulo);
        l.setFont(Estilos.FONTE_SUBTITULO);
        l.setForeground(Estilos.TINTA);
        return l;
    }

    // ---------- Cálculo dos números ----------

    /** Recalcula tudo a partir do gestor. É chamado sempre que a página é mostrada. */
    public void atualizar() {
        List<Livro> livros = gestor.listarTodosOsLivros();
        int exemplaresDisponiveis = 0;
        for (Livro l : livros) {
            exemplaresDisponiveis += l.getExemplaresDisponiveis();
        }
        List<Emprestimo> activos = gestor.listarEmprestimosActivos();
        int atrasados = 0;
        for (Emprestimo e : activos) {
            if (e.isAtrasado()) atrasados++;
        }

        totalLivros.setText(String.valueOf(livros.size()));
        totalExemplares.setText(String.valueOf(exemplaresDisponiveis));
        totalUtilizadores.setText(String.valueOf(gestor.listarTodosOsUtilizadores().size()));
        totalActivos.setText(String.valueOf(activos.size()));
        totalAtrasados.setText(String.valueOf(atrasados));

        preencherMaisEmprestados();
        preencherGrafico();
    }

    /** Conta quantas vezes cada ISBN aparece no histórico e mostra os 5 maiores. */
    private void preencherMaisEmprestados() {
        Map<String, Integer> contagem = new HashMap<>();
        for (Emprestimo e : gestor.listarTodoHistorico()) {
            contagem.merge(e.getIsbnLivro(), 1, Integer::sum);
        }

        List<Map.Entry<String, Integer>> ordenados = new ArrayList<>(contagem.entrySet());
        ordenados.sort((a, b) -> b.getValue() - a.getValue());

        modeloMaisEmprestados.setRowCount(0);
        int posicao = 1;
        for (Map.Entry<String, Integer> entrada : ordenados) {
            if (posicao > 5) break;
            Livro livro = gestor.pesquisarLivroPorIsbn(entrada.getKey()); // busca O(log n) na AVL
            String titulo = (livro == null) ? "(livro removido)" : livro.getTitulo();
            modeloMaisEmprestados.addRow(new Object[]{posicao, entrada.getKey(), titulo, entrada.getValue()});
            posicao++;
        }
    }

    /** Conta os empréstimos feitos em cada um dos últimos 6 meses (incluindo o actual). */
    private void preencherGrafico() {
        final int meses = 6;
        YearMonth actual = YearMonth.now();
        int[] valores = new int[meses];
        String[] nomes = new String[meses];
        String[] abreviaturas = {"Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez"};

        for (int i = 0; i < meses; i++) {
            nomes[i] = abreviaturas[actual.minusMonths(meses - 1 - i).getMonthValue() - 1];
        }
        for (Emprestimo e : gestor.listarTodoHistorico()) {
            LocalDate d = e.getDataEmprestimo();
            long diferenca = java.time.temporal.ChronoUnit.MONTHS.between(YearMonth.from(d), actual);
            if (diferenca >= 0 && diferenca < meses) {
                valores[meses - 1 - (int) diferenca]++;
            }
        }
        grafico.definir(nomes, valores);
    }

    // ---------- Gráfico de barras desenhado à mão ----------

    /** Barras simples: cor da marca (é dado), valor por cima, linha de base fina. */
    private static class GraficoMeses extends JComponent {
        private String[] nomes = new String[0];
        private int[] valores = new int[0];

        void definir(String[] nomes, int[] valores) {
            this.nomes = nomes;
            this.valores = valores;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int max = 0;
            for (int v : valores) max = Math.max(max, v);
            boolean vazio = max == 0;
            max = Math.max(max, 4); // escala mínima: uma barra de 1 não ocupa a altura toda

            if (vazio) { // estado vazio: nada para desenhar
                g2.setColor(Estilos.TEXTO_SUAVE);
                g2.setFont(Estilos.FONTE_NORMAL);
                g2.drawString("Ainda não há empréstimos registados.", 0, 24);
                g2.dispose();
                return;
            }

            int largura = getWidth();
            int baseY = getHeight() - 28;   // espaço em baixo para os nomes dos meses
            int topo = 24;                  // espaço em cima para o valor
            int n = valores.length;
            int passo = largura / n;
            int barra = Math.min(44, passo - 16);

            g2.setColor(Estilos.LINHA_FINA);
            g2.drawLine(0, baseY, largura, baseY);

            for (int i = 0; i < n; i++) {
                int altura = (int) Math.round((baseY - topo) * (valores[i] / (double) max));
                int x = i * passo + (passo - barra) / 2;

                if (valores[i] > 0) {
                    g2.setColor(Estilos.AZUL_PRIMARIO);
                    g2.fillRoundRect(x, baseY - altura, barra, altura, 6, 6);          // topo arredondado
                    int rect = Math.min(altura, 6);
                    g2.fillRect(x, baseY - rect, barra, rect);                         // base recta
                }

                g2.setFont(Estilos.FONTE_NORMAL.deriveFont(Font.BOLD, 12f));
                g2.setColor(Estilos.TINTA);
                String v = String.valueOf(valores[i]);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(v, x + (barra - fm.stringWidth(v)) / 2, baseY - altura - 6);

                g2.setFont(Estilos.FONTE_NORMAL.deriveFont(12f));
                g2.setColor(Estilos.TEXTO_SUAVE);
                fm = g2.getFontMetrics();
                g2.drawString(nomes[i], x + (barra - fm.stringWidth(nomes[i])) / 2, baseY + 18);
            }
            g2.dispose();
        }
    }
}