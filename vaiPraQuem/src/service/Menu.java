package service;

import java.util.ArrayList;
import java.util.Scanner;

import model.Endereco;
import model.Pessoa;
import util.SenhaUtil;

public class Menu {

    private static final Scanner teclado = new Scanner(System.in);
    private static final PessoaService pessoaService =
            new PessoaService();
    private static final EnderecoService enderecoService =
            new EnderecoService();


    public static void menuPrincipal() {

        int opcao;

        do {
            System.out.println();
            System.out.println("========== VAI PRA QUEM ==========");
            System.out.println("1 - Cadastrar-se");
            System.out.println("2 - Login");
            System.out.println("0 - Sair");

            opcao = lerInteiro("Escolha: ");

            try {
                switch (opcao) {

                    case 1:
                        menuCadastro();
                        break;

                    case 2:
                        menuLogin();
                        break;

                    case 0:
                        System.out.println("Encerrando...");
                        break;

                    default:
                        System.out.println("Opção inválida!");
                }

            } catch (RuntimeException erro) {
                mostrarErro(erro);
            }

        } while (opcao != 0);
    }


    // ---------- pessoa ----------

    public static void menuCadastro() {

        System.out.println("=== CADASTRO ===");
        System.out.println("1 - Cadastrar como VENDEDOR");
        System.out.println("2 - Cadastrar como COMPRADOR");

        int opcao = lerInteiro("Escolha: ");

        System.out.print("Tipo de pessoa (PF ou PJ): ");
        String tipoPessoa = teclado.nextLine().trim().toUpperCase();

        System.out.print("Documento: ");
        String documento = teclado.nextLine();

        System.out.print("Nome: ");
        String nome = teclado.nextLine();

        System.out.print("E-mail: ");
        String email = teclado.nextLine();

        System.out.print("Telefone: ");
        String telefone = teclado.nextLine();

        System.out.print("Senha: ");
        String senha = teclado.nextLine();

        String senhaHash = gerarHash(senha);

        Pessoa cadastrada;

        switch (opcao) {

            case 1:
                cadastrada = pessoaService.cadastrarVendedor(
                        tipoPessoa, documento, nome,
                        email, telefone, senhaHash
                );
                break;

            case 2:
                cadastrada = pessoaService.cadastrarComprador(
                        tipoPessoa, documento, nome,
                        email, telefone, senhaHash
                );
                break;

            default:
                System.out.println("Opção inválida!");
                return;
        }

        System.out.println("Cadastro realizado!");
        System.out.println(
                "ID da pessoa: " + cadastrada.getIdPessoa()
        );
        System.out.println(
                "Nome: " + cadastrada.getNome()
        );
        System.out.println(
                "Perfil: " + cadastrada.getClass().getSimpleName()
        );

        if (confirmar("Cadastrar um endereço para esta pessoa agora?")) {
            menuCadastrarEndereco(cadastrada.getIdPessoa());
        }
    }

    private static void menuLogin() {

        System.out.println();
        System.out.println("========== LOGIN ==========");

        System.out.print("E-mail: ");
        String email = teclado.nextLine().trim();

        System.out.print("Senha: ");
        String senha = teclado.nextLine();

        Pessoa pessoa = pessoaService.login(email, senha);

        if (pessoa == null) {
            System.out.println("E-mail ou senha inválidos.");
            return;
        }

        System.out.println();
        System.out.println("Login realizado com sucesso!");
        System.out.println("Bem-vindo(a), " + pessoa.getNome());
        System.out.println("ID: " + pessoa.getIdPessoa());
        System.out.println(
                "Perfil: " + pessoa.getClass().getSimpleName()
        );
    }


    private static void menuHabilitarVendedor() {

        Integer idPessoa = lerInteiro("Código da pessoa (id_pessoa): ");

        Pessoa pessoa = pessoaService.habilitarComoVendedor(idPessoa);

        System.out.println(
                "Pessoa " + pessoa.getIdPessoa() + " (" + pessoa.getNome() + ") agora também é VENDEDOR."
        );
    }

    private static void menuHabilitarComprador() {

        Integer idPessoa = lerInteiro("Código da pessoa (id_pessoa): ");

        Pessoa pessoa = pessoaService.habilitarComoComprador(idPessoa);

        System.out.println(
                "Pessoa " + pessoa.getIdPessoa() + " (" + pessoa.getNome() + ") agora também é COMPRADOR."
        );
    }

    private static void menuExcluirPessoa() {

        Integer idPessoa = lerInteiro("Código da pessoa (id_pessoa): ");

        if (!confirmar("Excluir a pessoa " + idPessoa + "?")) {
            return;
        }

        if (pessoaService.excluir(idPessoa)) {
            System.out.println("Pessoa excluída.");
        } else {
            System.out.println("Nenhuma pessoa encontrada com o código " + idPessoa + ".");
        }
    }

    // ---------- endereço ----------

    private static void menuCadastrarEndereco(Integer idPessoa) {

        System.out.print("Logradouro: ");
        String logradouro = teclado.nextLine();

        System.out.print("Número: ");
        String numero = teclado.nextLine();

        System.out.print("Complemento (opcional): ");
        String complemento = teclado.nextLine();

        System.out.print("Bairro: ");
        String bairro = teclado.nextLine();

        System.out.print("CEP: ");
        String cep = teclado.nextLine();

        System.out.print("Cidade: ");
        String cidade = teclado.nextLine();

        System.out.print("UF: ");
        String uf = teclado.nextLine();

        Endereco endereco = enderecoService.cadastrar(
                idPessoa, logradouro, numero, complemento,
                bairro, cep, cidade, uf
        );

        System.out.println(
                "Endereço cadastrado! ID do endereço: " + endereco.getIdEndereco()
        );
    }

    private static void menuListarEnderecos() {

        Integer idPessoa = lerInteiro("Código da pessoa (id_pessoa): ");

        ArrayList<Endereco> enderecos = enderecoService.listar(idPessoa);

        if (enderecos.isEmpty()) {
            System.out.println("Esta pessoa não tem endereços.");
            return;
        }

        for (Endereco endereco : enderecos) {
            System.out.println(descrever(endereco));
        }
    }

    private static void menuExcluirEndereco() {

        Integer idPessoa = lerInteiro("Código da pessoa (id_pessoa): ");
        Integer idEndereco = lerInteiro("Código do endereço (id_endereco): ");

        if (enderecoService.excluir(idPessoa, idEndereco)) {
            System.out.println("Endereço excluído.");
        } else {
            System.out.println("Nenhum endereço foi excluído.");
        }
    }

    private static void menuExcluirTodosEnderecos() {

        Integer idPessoa = lerInteiro("Código da pessoa (id_pessoa): ");

        if (!confirmar("Excluir TODOS os endereços da pessoa " + idPessoa + "?")) {
            return;
        }

        int apagados = enderecoService.excluirTodos(idPessoa);

        System.out.println(apagados + " endereço(s) excluído(s).");
    }

    // ---------- apoio ----------

    private static String descrever(Endereco endereco) {

        String complemento = (endereco.getComplemento() == null)
                ? ""
                : " (" + endereco.getComplemento() + ")";

        return "[" + endereco.getIdEndereco() + "] "
                + endereco.getLogradouro() + ", " + endereco.getNumero() + complemento
                + " - " + endereco.getBairro()
                + ", " + endereco.getCidade() + "/" + endereco.getUf()
                + " - CEP " + endereco.getCep();
    }

    // Lê a linha inteira e converte: evita o bug do nextInt() seguido de nextLine().
    private static int lerInteiro(String mensagem) {

        while (true) {
            System.out.print(mensagem);

            try {
                return Integer.parseInt(teclado.nextLine().trim());
            } catch (NumberFormatException erro) {
                System.out.println("Digite um número válido.");
            }
        }
    }

    private static boolean confirmar(String mensagem) {

        while (true) {
            System.out.print(mensagem + " (S/N): ");
            String resposta = teclado.nextLine().trim();

            if (resposta.equalsIgnoreCase("s")) {
                return true;
            }
            if (resposta.equalsIgnoreCase("n")) {
                return false;
            }
            System.out.println("Responda com S ou N.");
        }
    }

    // Mostra a mensagem e também a causa (por exemplo, o erro original do MySQL).
    private static void mostrarErro(RuntimeException erro) {

        System.out.println("ERRO: " + erro.getMessage());

        Throwable causa = erro.getCause();

        while (causa != null) {
            System.out.println("      causa: " + causa.getMessage());
            causa = causa.getCause();
        }
    }

    private static String gerarHash(String senha) {
        return SenhaUtil.gerarHash(senha);
    }
}
