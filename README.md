# Sistema de Gestão de Biblioteca

Aplicação de ambiente de trabalho em **Java Swing** para gerir livros, utilizadores e empréstimos de uma biblioteca.
Projecto académico (UEM) com foco em estruturas de dados.

![Página Início](docs/ecra-inicio.png)

## Estruturas de dados
- **Árvore AVL** — os livros, com o ISBN como chave (pesquisa, inserção e remoção em O(log n)).
- **Listas ligadas** — empréstimos e utilizadores.
- **Persistência** em ficheiros de texto (`dados/`), carregados ao abrir e guardados ao fechar.

## Funcionalidades
- Login (utilizador `bibliotecario`, palavra-passe `biblioteca123`)
- Livros: cadastrar, editar, remover, pesquisar; estado Disponível/Esgotado
- Utilizadores: registar, editar, remover
- Empréstimos: prazo escolhido em cada empréstimo (por defeito 7 dias), devolução, estado Em curso/Em atraso
- Início com os empréstimos em atraso e acção de devolução
- Relatórios: totais, livros mais emprestados, empréstimos por mês
- Pesquisa global com **Ctrl+K**

## Como compilar e correr
Requer o JDK 11 ou superior. A partir da pasta do projecto:

Windows (PowerShell):
```
javac -d out -encoding UTF-8 src\Main.java src\modelo\*.java src\estrutura\*.java src\gestao\*.java src\gui\*.java src\persistencia\*.java
java -cp out Main
```

Linux/macOS:
```
javac -d out -encoding UTF-8 $(find src -name '*.java')
java -cp out Main
```

Corre sempre a partir da pasta onde estão `dados/` e `imagens/`. A fotografia do login é `imagens/estante.png` (ou `.jpg`/`.jpeg`); se não existir, o ecrã desenha prateleiras.

## Estrutura
```
src/        código Java (modelo, estrutura, gestao, gui, persistencia)
dados/      dados de exemplo
imagens/    imagem do ecrã de login
docs/       relatório (Word) e capturas de ecrã
```

## Limitações
Sem multas nem renovações, um único utilizador de login, dados em ficheiros de texto e sem exportação para impressão.