package gui;

import java.awt.*;
import javax.swing.*;

/**
 * Ícones desenhados com Java 2D (sem ficheiros de imagem), para a coluna "Opções".
 * Cada ícone é um quadrado de 16x16 pixéis.
 */
public final class Icones {

    private Icones() {
    }

    /** Lápis: editar. */
    public static Icon editar(Color cor) {
        return new IconeDesenhado(cor) {
            @Override
            void desenhar(Graphics2D g) {
                // corpo do lápis (uma diagonal grossa) e a ponta
                g.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
                g.drawLine(11, 3, 4, 10);
                g.setStroke(new BasicStroke(1.5f));
                g.fillPolygon(new int[]{3, 6, 4}, new int[]{11, 12, 14}, 3);
                g.drawLine(2, 15, 14, 15); // linha de "papel"
            }
        };
    }

    /** Visto: registar devolução. */
    public static Icon devolver(Color cor) {
        return new IconeDesenhado(cor) {
            @Override
            void desenhar(Graphics2D g) {
                g.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(2, 8, 6, 12);   // traço curto do visto
                g.drawLine(6, 12, 14, 3);  // traço comprido do visto
            }
        };
    }

    /** Caixote do lixo: remover. */
    public static Icone remover(Color cor) {
        return new Icone(cor);
    }

    /** Casa simples: menu Início. */
    public static Icon inicio(Color cor) {
        return new IconeDesenhado(cor) {
            @Override
            void desenhar(Graphics2D g) {
                g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(2, 8, 8, 2);      // telhado
                g.drawLine(8, 2, 14, 8);
                g.drawLine(4, 7, 4, 14);     // paredes
                g.drawLine(12, 7, 12, 14);
                g.drawLine(4, 14, 12, 14);
                g.drawLine(7, 14, 7, 10);    // porta
                g.drawLine(9, 14, 9, 10);
                g.drawLine(7, 10, 9, 10);
            }
        };
    }

    /** Três lombadas de livros: menu Livros. */
    public static Icon livros(Color cor) {
        return new IconeDesenhado(cor) {
            @Override
            void desenhar(Graphics2D g) {
                g.setStroke(new BasicStroke(1.6f));
                g.drawRoundRect(2, 3, 3, 11, 1, 1);
                g.drawRoundRect(6, 2, 3, 12, 1, 1);
                g.drawLine(11, 4, 14, 3);                 // livro inclinado
                g.drawLine(11, 4, 11, 14);
                g.drawLine(14, 3, 14, 13);
                g.drawLine(11, 14, 14, 13);
            }
        };
    }

    /** Cabeça e ombros: menu Utilizadores. */
    public static Icon utilizadores(Color cor) {
        return new IconeDesenhado(cor) {
            @Override
            void desenhar(Graphics2D g) {
                g.setStroke(new BasicStroke(1.6f));
                g.drawOval(5, 2, 6, 6);
                g.drawArc(2, 9, 12, 10, 0, 180);
            }
        };
    }

    /** Duas setas em sentidos opostos: menu Empréstimos. */
    public static Icon emprestimos(Color cor) {
        return new IconeDesenhado(cor) {
            @Override
            void desenhar(Graphics2D g) {
                g.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(2, 5, 13, 5);
                g.drawLine(10, 2, 13, 5);
                g.drawLine(10, 8, 13, 5);
                g.drawLine(14, 11, 3, 11);
                g.drawLine(6, 8, 3, 11);
                g.drawLine(6, 14, 3, 11);
            }
        };
    }

    /** Barras de um gráfico: menu Relatórios. */
    public static Icon relatorios(Color cor) {
        return new IconeDesenhado(cor) {
            @Override
            void desenhar(Graphics2D g) {
                g.setStroke(new BasicStroke(1.6f));
                g.drawLine(2, 14, 14, 14);
                g.fillRoundRect(3, 8, 3, 6, 1, 1);
                g.fillRoundRect(7, 3, 3, 11, 1, 1);
                g.fillRoundRect(11, 6, 3, 8, 1, 1);
            }
        };
    }

    // Classe auxiliar para o caixote (devolve Icon normal)
    public static class Icone extends IconeDesenhado {
        Icone(Color cor) {
            super(cor);
        }

        @Override
        void desenhar(Graphics2D g) {
            g.setStroke(new BasicStroke(1.6f));
            g.drawLine(2, 4, 14, 4);                 // tampa
            g.drawRect(6, 1, 4, 2);                  // pega
            g.drawRoundRect(4, 5, 8, 9, 2, 2);       // corpo
            g.drawLine(7, 7, 7, 12);                 // riscas
            g.drawLine(9, 7, 9, 12);
        }
    }

    /** Base comum: prepara o desenho suave e posiciona no sítio certo. */
    private abstract static class IconeDesenhado implements Icon {
        private final Color cor;

        IconeDesenhado(Color cor) {
            this.cor = cor;
        }

        abstract void desenhar(Graphics2D g);

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);
            g2.setColor(cor);
            desenhar(g2);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }
    }
}