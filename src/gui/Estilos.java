package gui;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Ponto único de configuração visual da aplicação: cores, fontes, e o
 * Look and Feel (Nimbus, com uma paleta azul personalizada). Aplicar o
 * tema aqui evita cores/fontes "mágicas" espalhadas pelos painéis.
 */
public final class Estilos {

    // ---------- Paleta de cores ----------
    public static final Color AZUL_PRIMARIO = new Color(0x2E, 0x53, 0x95);
    public static final Color AZUL_ESCURO = new Color(0x1E, 0x38, 0x66);
    public static final Color AZUL_CLARO = new Color(0xE8, 0xEE, 0xF7);
    public static final Color CINZA_TEXTO = new Color(0x33, 0x33, 0x33);
    public static final Color BRANCO = Color.WHITE;
    public static final Color LINHA_ALTERNADA = new Color(0xF5, 0xF7, 0xFB);
    public static final Color VERMELHO_ATRASO = new Color(0xC0, 0x39, 0x2B);
    public static final Color VERDE_OK = new Color(0x1E, 0x8A, 0x4C);

    // Neutros da nova casca (a cor da marca fica só nas acções e na selecção)
    public static final Color TINTA = new Color(0x1F, 0x29, 0x37);          // texto principal
    public static final Color TEXTO_SUAVE = new Color(0x6B, 0x72, 0x80);    // texto secundário
    public static final Color FUNDO_MENU = new Color(0xF9, 0xFA, 0xFB);     // fundo do menu lateral
    public static final Color LINHA_FINA = new Color(0xE5, 0xE7, 0xEB);     // traços de separação
    public static final Color ITEM_ACTIVO = new Color(0xE8, 0xEE, 0xF7);    // fundo do item de menu activo
    public static final Color ITEM_HOVER = new Color(0xEE, 0xF0, 0xF3);     // fundo ao passar o rato

    // ---------- Fontes ----------
    public static final Font FONTE_TITULO = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONTE_BOTAO = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONTE_TABELA_CABECALHO = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONTE_TABELA_CORPO = new Font("Segoe UI", Font.PLAIN, 13);

    private Estilos() {
        // classe utilitária: não instanciar
    }

    /** Aplica o Look and Feel Nimbus com uma paleta azul personalizada. Chamar uma vez, no arranque. */
    public static void aplicarTema() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());

            UIManager.put("nimbusBase", AZUL_ESCURO);
            UIManager.put("nimbusBlueGrey", new Color(0xDE, 0xE3, 0xEC));
            UIManager.put("control", new Color(0xF4, 0xF6, 0xFA));
            UIManager.put("nimbusLightBackground", BRANCO);
            UIManager.put("text", CINZA_TEXTO);
            UIManager.put("nimbusSelectionBackground", AZUL_PRIMARIO);
            UIManager.put("nimbusSelectedText", BRANCO);
            UIManager.put("nimbusFocus", AZUL_PRIMARIO);

            UIManager.put("Table.font", FONTE_TABELA_CORPO);
            UIManager.put("TableHeader.font", FONTE_TABELA_CABECALHO);
            UIManager.put("Label.font", FONTE_NORMAL);
            UIManager.put("Button.font", FONTE_BOTAO);
            UIManager.put("TextField.font", FONTE_NORMAL);
            UIManager.put("PasswordField.font", FONTE_NORMAL);
            UIManager.put("TabbedPane.font", FONTE_SUBTITULO);
            UIManager.put("TitledBorder.font", FONTE_SUBTITULO);
            UIManager.put("TitledBorder.titleColor", AZUL_PRIMARIO);

            // Botões das caixas de diálogo (JOptionPane) em português
            UIManager.put("OptionPane.okButtonText", "OK");
            UIManager.put("OptionPane.cancelButtonText", "Cancelar");
            UIManager.put("OptionPane.yesButtonText", "Sim");
            UIManager.put("OptionPane.noButtonText", "Não");

            UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(LINHA_FINA));

            aplicarBotoesEcamposModernos();

        } catch (Exception ex) {
            // Se o Nimbus não estiver disponível, segue com o Look and Feel por omissão.
            System.err.println("Não foi possível aplicar o tema Nimbus: " + ex.getMessage());
        }
    }


    // ====================================================================
    //  Botões e campos de texto modernos (cantos arredondados, hover, foco)
    // ====================================================================

    private static final Color BORDA = new Color(0xC9, 0xD2, 0xE3);
    private static final Color AZUL_HOVER = new Color(0x3C, 0x68, 0xB4);
    private static final int ARREDONDAMENTO = 12;

    /**
     * O Nimbus pinta cada componente através de "painters" (objectos que desenham o fundo
     * e a borda). Aqui trocamos os painters dos botões e dos campos por versões nossas,
     * com cantos arredondados. Como é no tema, todos os botões e campos da aplicação mudam
     * de uma só vez, sem tocar nos painéis.
     */
    private static void aplicarBotoesEcamposModernos() {
        Painter<JComponent> fundoBotao = Estilos::pintarFundoBotao;
        String[] estadosBotao = {"Enabled", "MouseOver", "Pressed", "Disabled", "Focused", "Default",
                "Default+Focused", "Default+MouseOver", "Default+Focused+MouseOver", "Default+Pressed",
                "Default+Focused+Pressed", "Focused+MouseOver", "Focused+Pressed"};
        for (String estado : estadosBotao) {
            UIManager.put("Button[" + estado + "].backgroundPainter", fundoBotao);
        }
        UIManager.put("Button.contentMargins", new InsetsUIResource(7, 18, 7, 18));

        Painter<JComponent> fundoCampo = Estilos::pintarFundoCampo;
        Painter<JComponent> bordaCampo = Estilos::pintarBordaCampo;
        String[] tipos = {"TextField", "PasswordField", "FormattedTextField"};
        String[] estadosCampo = {"Enabled", "Focused", "Disabled", "Enabled+NotInScrollPane",
                "Focused+NotInScrollPane"};
        for (String tipo : tipos) {
            for (String estado : estadosCampo) {
                UIManager.put(tipo + "[" + estado + "].backgroundPainter", fundoCampo);
                UIManager.put(tipo + "[" + estado + "].borderPainter", bordaCampo);
            }
            UIManager.put(tipo + ".contentMargins", new InsetsUIResource(7, 11, 7, 11));
            UIManager.put(tipo + ".opaque", Boolean.FALSE); // deixa ver os cantos arredondados
        }
    }

    private static Graphics2D suave(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return g;
    }

    private static void pintarFundoBotao(Graphics2D g0, JComponent c, int w, int h) {
        Graphics2D g = suave((Graphics2D) g0.create());
        ButtonModel m = (c instanceof AbstractButton) ? ((AbstractButton) c).getModel() : null;
        boolean activo = c.isEnabled();
        boolean carregado = m != null && m.isPressed();
        boolean rato = m != null && m.isRollover();
        boolean primario = Boolean.TRUE.equals(c.getClientProperty("primario"));

        Color fundo;
        Color borda;
        if (!activo) {
            fundo = new Color(0xEE, 0xF0, 0xF4);
            borda = new Color(0xDD, 0xE1, 0xE8);
        } else if (primario) {
            fundo = carregado ? AZUL_ESCURO : (rato ? AZUL_HOVER : AZUL_PRIMARIO);
            borda = fundo;
        } else {
            fundo = carregado ? new Color(0xD3, 0xDE, 0xF1) : (rato ? AZUL_CLARO : BRANCO);
            borda = rato || carregado ? AZUL_PRIMARIO : BORDA;
        }

        RoundRectangle2D forma = new RoundRectangle2D.Float(1, 1, w - 3, h - 3, ARREDONDAMENTO, ARREDONDAMENTO);
        g.setColor(fundo);
        g.fill(forma);
        g.setColor(borda);
        g.setStroke(new BasicStroke(1.2f));
        g.draw(forma);
        g.dispose();
    }

    private static void pintarFundoCampo(Graphics2D g0, JComponent c, int w, int h) {
        Graphics2D g = suave((Graphics2D) g0.create());
        g.setColor(c.isEnabled() ? BRANCO : new Color(0xF1, 0xF3, 0xF7));
        g.fill(new RoundRectangle2D.Float(1, 1, w - 3, h - 3, 10, 10));
        g.dispose();
    }

    private static void pintarBordaCampo(Graphics2D g0, JComponent c, int w, int h) {
        Graphics2D g = suave((Graphics2D) g0.create());
        boolean foco = c.hasFocus();
        g.setColor(foco ? AZUL_PRIMARIO : BORDA);
        g.setStroke(new BasicStroke(foco ? 2f : 1.2f));
        g.draw(new RoundRectangle2D.Float(1, 1, w - 3, h - 3, 10, 10));
        g.dispose();
    }

    /**
     * Cor de fundo de uma linha da tabela: branca, um cinzento muito leve quando o rato passa
     * por cima, e azul-claro quando está seleccionada. Já não há linhas zebradas.
     */
    public static Color fundoLinha(JTable tabela, int linha, boolean seleccionada) {
        if (seleccionada) return ITEM_ACTIVO;
        Object sobre = tabela.getClientProperty("linhaSobRato");
        if (sobre instanceof Integer && (Integer) sobre == linha) return new Color(0xF4, 0xF6, 0xFA);
        return BRANCO;
    }

    /** Traço fino por baixo de cada linha + margem interior (usado nas células). */
    public static javax.swing.border.Border bordaCelula() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, LINHA_FINA),
                BorderFactory.createEmptyBorder(0, 8, 0, 8));
    }

    /**
     * Renderer de "chip" de estado: uma pílula arredondada com cor suave e texto na cor forte.
     * Verde = tudo bem, vermelho = problema, âmbar = aviso, cinzento = neutro.
     * A cor escolhe-se pelo texto da célula (ver corDoChip).
     */
    public static javax.swing.table.TableCellRenderer chipEstado() {
        // Componente que pinta tudo à mão: fundo da linha, traço fino e a pílula centrada na vertical.
        class Chip extends JComponent implements javax.swing.table.TableCellRenderer {
            String texto = "";
            Color fundoChip, corTexto, fundoLinhaActual = BRANCO;

            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel,
                                                             boolean foco, int row, int col) {
                texto = String.valueOf(v);
                Color[] c = coresDoChip(texto);
                fundoChip = c[0];
                corTexto = c[1];
                fundoLinhaActual = fundoLinha(t, row, sel);
                return this;
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(fundoLinhaActual);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(LINHA_FINA);
                g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

                Font fonte = FONTE_TABELA_CORPO.deriveFont(Font.BOLD, 11.5f);
                g2.setFont(fonte);
                FontMetrics fm = g2.getFontMetrics();
                int largura = fm.stringWidth(texto) + 20;
                int altura = fm.getHeight() + 4;
                int x = 8;
                int y = (getHeight() - 1 - altura) / 2;
                g2.setColor(fundoChip);
                g2.fillRoundRect(x, y, largura, altura, altura, altura);
                g2.setColor(corTexto);
                g2.drawString(texto, x + 10, y + 2 + fm.getAscent());
                g2.dispose();
            }
        }
        return new Chip();
    }

    /** {fundo suave, texto forte} para cada estado conhecido. */
    private static Color[] coresDoChip(String texto) {
        switch (texto) {
            case "Disponível": case "Em curso":
                return new Color[]{new Color(0xE3, 0xF4, 0xE8), new Color(0x1E, 0x7B, 0x3A)};
            case "Esgotado": case "Em atraso":
                return new Color[]{new Color(0xFC, 0xE8, 0xE6), new Color(0xB3, 0x26, 0x1E)};
            default:
                return new Color[]{new Color(0xEE, 0xF0, 0xF4), TEXTO_SUAVE};
        }
    }

    /** Estilo leve de tabela: cabeçalho discreto, traços finos entre linhas e realce ao passar o rato. */
    public static void estilizarTabela(JTable tabela) {
        tabela.setRowHeight(36);
        tabela.setShowGrid(false);
        tabela.setIntercellSpacing(new Dimension(0, 0));
        tabela.setSelectionBackground(ITEM_ACTIVO);
        tabela.setSelectionForeground(TINTA);
        tabela.setFont(FONTE_TABELA_CORPO);
        tabela.setForeground(TINTA);
        tabela.setFillsViewportHeight(true);
        tabela.setBackground(BRANCO);

        // CABEÇALHO: fundo branco, texto pequeno e cinzento, traço por baixo.
        // (O Nimbus ignora setBackground() no cabeçalho, por isso pintamos nós com um renderer próprio.)
        DefaultTableCellRenderer cabecalho = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                             boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, false, false, row, column);
                setOpaque(true);
                setBackground(BRANCO);
                setForeground(TEXTO_SUAVE);
                setFont(FONTE_TABELA_CABECALHO.deriveFont(Font.BOLD, 12f));
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, LINHA_FINA),
                        BorderFactory.createEmptyBorder(0, 8, 0, 8)));
                return this;
            }
        };
        tabela.getTableHeader().setDefaultRenderer(cabecalho);
        tabela.getTableHeader().setPreferredSize(new Dimension(0, 36));
        tabela.getTableHeader().setReorderingAllowed(false);

        // CORPO
        tabela.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                             boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, false, row, column);
                c.setBackground(fundoLinha(table, row, isSelected));
                c.setForeground(TINTA);
                setBorder(bordaCelula());
                return c;
            }
        });

        // Realce da linha sob o rato: guardamos o índice e pedimos para repintar
        java.awt.event.MouseAdapter rato = new java.awt.event.MouseAdapter() {
            private void actualizar(int linha) {
                Object anterior = tabela.getClientProperty("linhaSobRato");
                if (anterior instanceof Integer && (Integer) anterior == linha) return;
                tabela.putClientProperty("linhaSobRato", linha < 0 ? null : linha);
                tabela.repaint();
            }
            @Override public void mouseMoved(java.awt.event.MouseEvent e) { actualizar(tabela.rowAtPoint(e.getPoint())); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { actualizar(-1); }
        };
        tabela.addMouseMotionListener(rato);
        tabela.addMouseListener(rato);
    }

    /** Dá destaque visual (fundo azul) ao botão de acção principal de um formulário. */
    public static void estilizarBotaoPrimario(JButton botao) {
        botao.putClientProperty("primario", Boolean.TRUE); // o painter lê isto para pintar a azul
        botao.setBackground(AZUL_PRIMARIO);
        botao.setForeground(BRANCO);
        botao.setFocusPainted(false);
        botao.setFont(FONTE_BOTAO);
    }
}