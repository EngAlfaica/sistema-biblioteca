package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Ecrã de login: painel azul com o nome do sistema à esquerda e o
 * formulário à direita. Só depois de autenticar é que a JanelaPrincipal abre.
 */
public class JanelaLogin extends JFrame {

    // Credenciais fixas. Para mudar, altera estas duas linhas.
    private static final String UTILIZADOR_CORRECTO = "FENG";
    private static final String PASSWORD_CORRECTA = "Feng2026";

    private JTextField campoUtilizador;
    private JPasswordField campoPassword;
    private JLabel mensagemErro;

    public JanelaLogin() {
        super("Sistema de Gestão de Biblioteca — Login");

        // Dois painéis lado a lado, com a mesma largura
        setLayout(new GridLayout(1, 2));
        add(criarPainelMarca());
        add(criarPainelFormulario());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 420);
        setResizable(false);
        setLocationRelativeTo(null);
    }

    // ---------- Lado esquerdo: a marca ----------

    private JPanel criarPainelMarca() {
        JPanel painel = new PainelEstantes(); // fundo: prateleiras com livros
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(BorderFactory.createEmptyBorder(0, 36, 0, 36));

        JLabel nome = new JLabel("BIBLIOTECA");
        nome.setFont(Estilos.FONTE_TITULO.deriveFont(30f));
        nome.setForeground(Estilos.BRANCO);

        JLabel descricao = new JLabel("<html>Sistema de Gestão de Biblioteca<br>"
                + "Livros, utilizadores e empréstimos<br>num só lugar.</html>");
        descricao.setFont(Estilos.FONTE_NORMAL.deriveFont(14f));
        descricao.setForeground(new Color(0xC9, 0xD6, 0xEE));

        painel.add(Box.createVerticalGlue());
        painel.add(nome);
        painel.add(Box.createVerticalStrut(12));
        painel.add(descricao);
        painel.add(Box.createVerticalGlue());
        return painel;
    }

    // ---------- Lado direito: o formulário ----------

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new GridBagLayout());
        painel.setBackground(Estilos.BRANCO);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Estilos.BRANCO);

        JLabel titulo = new JLabel("Entrar");
        titulo.setFont(Estilos.FONTE_TITULO.deriveFont(24f));
        titulo.setForeground(Estilos.AZUL_PRIMARIO);

        JLabel subtitulo = new JLabel("Acesso do bibliotecário");
        subtitulo.setFont(Estilos.FONTE_NORMAL);
        subtitulo.setForeground(Estilos.CINZA_TEXTO);

        campoUtilizador = new JTextField(18);
        campoPassword = new JPasswordField(18);
        campoUtilizador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        campoPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        mensagemErro = new JLabel(" "); // espaço para não "saltar" o layout quando aparece o erro
        mensagemErro.setFont(Estilos.FONTE_NORMAL);
        mensagemErro.setForeground(Estilos.VERMELHO_ATRASO);

        JButton botaoEntrar = new JButton("Entrar");
        Estilos.estilizarBotaoPrimario(botaoEntrar);
        botaoEntrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        botaoEntrar.addActionListener(e -> tentarLogin());
        campoUtilizador.addActionListener(e -> campoPassword.requestFocusInWindow());
        campoPassword.addActionListener(e -> tentarLogin()); // Enter na palavra-passe também entra

        form.add(alinhar(titulo));
        form.add(Box.createVerticalStrut(4));
        form.add(alinhar(subtitulo));
        form.add(Box.createVerticalStrut(24));
        form.add(alinhar(new JLabel("Utilizador")));
        form.add(Box.createVerticalStrut(4));
        form.add(alinhar(campoUtilizador));
        form.add(Box.createVerticalStrut(12));
        form.add(alinhar(new JLabel("Palavra-passe")));
        form.add(Box.createVerticalStrut(4));
        form.add(alinhar(campoPassword));
        form.add(Box.createVerticalStrut(8));
        form.add(alinhar(mensagemErro));
        form.add(Box.createVerticalStrut(8));
        form.add(alinhar(botaoEntrar));

        painel.add(form);
        return painel;
    }

    /** Alinha o componente à esquerda dentro do BoxLayout vertical. */
    private JComponent alinhar(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        return c;
    }

    private void tentarLogin() {
        String utilizador = campoUtilizador.getText().trim();
        String password = new String(campoPassword.getPassword());

        if (utilizador.equals(UTILIZADOR_CORRECTO) && password.equals(PASSWORD_CORRECTA)) {
            dispose(); // fecha o login
            new JanelaPrincipal().setVisible(true);
        } else {
            mensagemErro.setText("Utilizador ou palavra-passe incorrectos.");
            campoPassword.setText("");
            campoPassword.requestFocusInWindow();
        }
    }

    // ---------- Fundo: prateleiras com livros ----------

    /**
     * Painel que pinta o seu próprio fundo. Se existir o ficheiro imagens/estante.jpg
     * (ou .png) usa essa fotografia; senão desenha prateleiras e livros com Java 2D.
     * Por cima põe uma camada azul-escura para o texto ficar legível.
     */
    private static class PainelEstantes extends JPanel {

        private static final Color[] CORES_LIVROS = {
                new Color(0x8E, 0x3B, 0x46), new Color(0x2F, 0x5D, 0x8A), new Color(0xC2, 0x8E, 0x3A),
                new Color(0x3E, 0x7C, 0x59), new Color(0x6B, 0x4E, 0x8C), new Color(0xB5, 0x5A, 0x30),
                new Color(0x4A, 0x6F, 0x7A), new Color(0xA8, 0x9F, 0x6C)
        };

        private final BufferedImage foto = carregarFoto();

        /**
         * Procura uma imagem (png/jpg/jpeg) na pasta "imagens". Tenta primeiro a pasta
         * onde o programa foi iniciado e depois a pasta acima (caso se corra de dentro de src ou out).
         * Aceita qualquer nome de ficheiro, por isso "estante.png.png" também funciona.
         */
        private static BufferedImage carregarFoto() {
            String[] pastas = {"imagens", "../imagens"};
            for (String nomePasta : pastas) {
                File pasta = new File(nomePasta);
                File[] ficheiros = pasta.listFiles();
                if (ficheiros == null) continue;
                java.util.Arrays.sort(ficheiros); // ordem previsível
                for (File f : ficheiros) {
                    String n = f.getName().toLowerCase();
                    if (n.endsWith(".png") || n.endsWith(".jpg") || n.endsWith(".jpeg")) {
                        try {
                            BufferedImage img = ImageIO.read(f);
                            if (img != null) {
                                System.out.println("Imagem de fundo carregada: " + f.getAbsolutePath());
                                return img;
                            }
                            // read() devolve null quando o formato não é suportado (ex.: WebP)
                            System.out.println("O Java não consegue ler este formato: " + f.getAbsolutePath()
                                    + " -> abre a imagem no Paint e guarda como PNG.");
                        } catch (IOException ex) {
                            System.out.println("Erro ao ler " + f.getAbsolutePath() + ": " + ex.getMessage());
                        }
                    }
                }
            }
            System.out.println("Imagem de fundo NÃO encontrada. Procurei em: "
                    + new File("imagens").getAbsolutePath());
            return null;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            if (foto != null) {
                // "cover": enche o painel sem deformar a imagem
                double escala = Math.max((double) w / foto.getWidth(), (double) h / foto.getHeight());
                int iw = (int) (foto.getWidth() * escala);
                int ih = (int) (foto.getHeight() * escala);
                g2.drawImage(foto, (w - iw) / 2, (h - ih) / 2, iw, ih, null);
            } else {
                desenharEstantes(g2, w, h);
            }

            // camada azul-escura semi-transparente por cima (alpha 0-255)
            g2.setColor(new Color(0x1E, 0x38, 0x66, 175));
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }

        private void desenharEstantes(Graphics2D g2, int w, int h) {
            g2.setColor(new Color(0x2B, 0x1D, 0x14)); // madeira escura ao fundo
            g2.fillRect(0, 0, w, h);

            int numPrateleiras = 5;
            int alturaPrateleira = h / numPrateleiras;
            Random sorteio = new Random(7); // semente fixa: os livros ficam sempre iguais

            for (int i = 0; i < numPrateleiras; i++) {
                int chao = (i + 1) * alturaPrateleira; // y da tábua desta prateleira
                int x = 4;
                while (x < w - 4) {
                    int largura = 12 + sorteio.nextInt(16);
                    int altura = (int) (alturaPrateleira * (0.55 + sorteio.nextDouble() * 0.35));
                    Color cor = CORES_LIVROS[sorteio.nextInt(CORES_LIVROS.length)];
                    g2.setColor(cor);
                    g2.fillRect(x, chao - altura, largura, altura);
                    g2.setColor(cor.darker());
                    g2.drawRect(x, chao - altura, largura, altura);
                    // duas faixas claras na lombada
                    g2.setColor(new Color(255, 255, 255, 90));
                    g2.fillRect(x + 2, chao - altura + 8, largura - 4, 3);
                    g2.fillRect(x + 2, chao - 14, largura - 4, 3);
                    x += largura + 1;
                }
                g2.setColor(new Color(0x6B, 0x45, 0x25)); // tábua da prateleira
                g2.fillRect(0, chao - 2, w, 8);
            }
        }
    }
}