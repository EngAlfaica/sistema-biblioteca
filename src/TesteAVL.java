import estrutura.ArvoreAVL;
import gestao.GestorBiblioteca;
import modelo.Emprestimo;
import modelo.Livro;
import modelo.Utilizador;

/**
 * Testes manuais (sem framework) para correr no terminal e confirmar,
 * de forma visual, que a AVL se mantém balanceada e que as regras de
 * negócio do GestorBiblioteca funcionam como esperado.
 *
 * Para correr: executa esta classe directamente (tem o seu próprio main).
 */
public class TesteAVL {

    public static void main(String[] args) {
        testarBalanceamentoDaAVL();
        System.out.println();
        testarRegrasDoGestor();
    }

    // ---------- Teste 1: balanceamento da AVL ----------

    private static void testarBalanceamentoDaAVL() {
        System.out.println("===== TESTE 1: Balanceamento da AVL =====");
        ArvoreAVL arvore = new ArvoreAVL();

        // Pior caso para uma BST normal: inserir em ordem crescente de ISBN.
        // Numa BST simples isto criava uma "lista" (altura = n).
        // Numa AVL, a altura deve manter-se próxima de log2(n).
        int totalLivros = 1000;
        for (int i = 1; i <= totalLivros; i++) {
            String isbnGerado = String.format("ISBN-%05d", i); // ISBN-00001, ISBN-00002, ...
            arvore.inserir(new Livro(isbnGerado, "Livro " + i, "Autor " + i, 2000 + (i % 25), 1));
        }

        int alturaObtida = arvore.getAltura();
        double limiteTeorico = Math.log(totalLivros + 1) / Math.log(2); // log2(n+1)

        System.out.println("Livros inseridos em ordem crescente: " + totalLivros);
        System.out.println("Altura da árvore: " + alturaObtida);
        System.out.printf("Limite teórico (log2 de %d): %.2f%n", totalLivros, limiteTeorico);
        System.out.println(alturaObtida <= limiteTeorico * 1.5
                ? ">> OK: a árvore manteve-se balanceada."
                : ">> FALHA: a árvore não está balanceada!");

        // Confirma que a pesquisa continua a encontrar tudo correctamente
        Livro encontrado = arvore.pesquisar("ISBN-00500");
        System.out.println("Pesquisa por ISBN-00500: " + (encontrado != null ? "encontrado -> " + encontrado : "NÃO encontrado (falha)"));

        // Testa remoção e confirma que a árvore continua balanceada depois
        for (int i = 1; i <= 500; i++) {
            arvore.remover(String.format("ISBN-%05d", i));
        }
        System.out.println("Depois de remover 500 livros -> quantidade: " + arvore.getQuantidadeLivros()
                + " | altura: " + arvore.getAltura());
    }

    // ---------- Teste 2: regras de negócio do GestorBiblioteca ----------

    private static void testarRegrasDoGestor() {
        System.out.println("===== TESTE 2: Regras de negócio do GestorBiblioteca =====");
        GestorBiblioteca gestor = new GestorBiblioteca();

        // Cenário base
        gestor.inserirLivro(new Livro("111", "Dom Casmurro", "Machado de Assis", 1899, 1));
        gestor.registarUtilizador(new Utilizador("U1", "Keen", "keen@email.com", "84xxxxxxx"));

        // Caso 1: empréstimo normal deve funcionar
        Emprestimo emp1 = gestor.emprestarLivro("111", "U1");
        System.out.println("Emprestar livro com exemplar disponível: " + (emp1 != null ? "OK" : "FALHA"));

        // Caso 2: emprestar o mesmo livro outra vez (sem exemplares) deve falhar
        Emprestimo emp2 = gestor.emprestarLivro("111", "U1");
        System.out.println("Emprestar livro sem exemplar disponível (deve falhar): " + (emp2 == null ? "OK" : "FALHA"));

        // Caso 3: remover livro com empréstimo activo deve falhar
        boolean removeuComActivo = gestor.removerLivro("111");
        System.out.println("Remover livro com empréstimo activo (deve falhar): " + (!removeuComActivo ? "OK" : "FALHA"));

        // Caso 4: devolver e voltar a emprestar deve funcionar
        boolean devolveu = gestor.devolverLivro("111");
        Emprestimo emp3 = gestor.emprestarLivro("111", "U1");
        System.out.println("Devolver e voltar a emprestar: " + (devolveu && emp3 != null ? "OK" : "FALHA"));

        // Caso 5: emprestar a um utilizador inexistente deve falhar
        Emprestimo emp4 = gestor.emprestarLivro("111", "UTILIZADOR_INEXISTENTE");
        System.out.println("Emprestar a utilizador inexistente (deve falhar): " + (emp4 == null ? "OK" : "FALHA"));
    }
}