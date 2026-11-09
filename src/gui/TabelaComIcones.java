package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Tabela reutilizável com uma última coluna "Opções" cheia de ícones clicáveis.
 * É genérica (T): guarda o objecto de cada linha (um Utilizador, um Emprestimo...),
 * para saber sobre qual deles o ícone foi clicado.
 */
public class TabelaComIcones<T> extends JTable {

    /** O que fazer quando se clica num ícone: recebe o objecto da linha e o nº do ícone (0, 1...). */
    public interface Acao<T> {
        void executar(T item, int icone);
    }

    private static final int LADO = 16;
    private static final int ESPACO = 14;

    private final DefaultTableModel modelo;
    private final List<T> itens = new ArrayList<>();
    private final Icon[] icones;
    private final int colunaOpcoes;
    private Acao<T> acao;

    public TabelaComIcones(String[] colunas, int[] larguras, Icon[] icones) {
        this.icones = icones;
        this.colunaOpcoes = colunas.length; // a coluna Opções vem logo a seguir às de dados

        String[] todas = Arrays.copyOf(colunas, colunas.length + 1);
        todas[colunas.length] = "Opções";
        modelo = new DefaultTableModel(todas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }
        };
        setModel(modelo);
        Estilos.estilizarTabela(this);

        for (int i = 0; i < larguras.length && i < colunas.length; i++) {
            getColumnModel().getColumn(i).setPreferredWidth(larguras[i]);
        }
        int larguraOpcoes = icones.length * (LADO + ESPACO) + 40;
        getColumnModel().getColumn(colunaOpcoes).setPreferredWidth(larguraOpcoes);
        getColumnModel().getColumn(colunaOpcoes).setMaxWidth(larguraOpcoes);
        getColumnModel().getColumn(colunaOpcoes).setCellRenderer(new RendererOpcoes());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int linha = rowAtPoint(e.getPoint());
                int icone = iconeEm(e.getPoint());
                if (linha >= 0 && icone >= 0 && acao != null) {
                    acao.executar(itens.get(linha), icone);
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                setCursor(iconeEm(e.getPoint()) >= 0
                        ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                        : Cursor.getDefaultCursor());
            }
        });
    }

    /** Define o que acontece ao clicar num ícone. */
    public void aoClicar(Acao<T> acao) {
        this.acao = acao;
    }

    /** Substitui o conteúdo. O conversor transforma cada objecto nos valores das colunas de dados. */
    public void carregar(List<T> lista, Function<T, Object[]> conversor) {
        itens.clear();
        modelo.setRowCount(0);
        for (T item : lista) {
            itens.add(item);
            Object[] dados = conversor.apply(item);
            Object[] linha = Arrays.copyOf(dados, dados.length + 1);
            linha[dados.length] = "";
            modelo.addRow(linha);
        }
    }

    private int xIcone(int i, int larguraCelula) {
        int total = icones.length * LADO + (icones.length - 1) * ESPACO;
        return (larguraCelula - total) / 2 + i * (LADO + ESPACO);
    }

    /** Índice do ícone sob o rato, ou -1. */
    private int iconeEm(Point p) {
        int linha = rowAtPoint(p);
        int coluna = columnAtPoint(p);
        if (linha < 0 || coluna != colunaOpcoes) return -1;
        Rectangle celula = getCellRect(linha, coluna, false);
        int relativo = p.x - celula.x;
        for (int i = 0; i < icones.length; i++) {
            int x = xIcone(i, celula.width);
            if (relativo >= x - 4 && relativo < x + LADO + 4) return i;
        }
        return -1;
    }

    private class RendererOpcoes extends DefaultTableCellRenderer {
        private int linha;
        private boolean seleccionada;

        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                         boolean foco, int row, int col) {
            linha = row;
            seleccionada = sel;
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(Estilos.fundoLinha(TabelaComIcones.this, linha, seleccionada));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Estilos.LINHA_FINA);
            g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            int y = (getHeight() - LADO) / 2;
            for (int i = 0; i < icones.length; i++) {
                icones[i].paintIcon(this, g, xIcone(i, getWidth()), y);
            }
        }
    }
}