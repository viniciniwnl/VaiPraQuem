package service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

import model.Anuncio;
import model.Compra;
import model.Comprador;
import model.Endereco;
import model.Pessoa;
import model.Vendedor;
import util.SenhaUtil;

public class Menu {

    private static final Scanner teclado = new Scanner(System.in);
    private static final PessoaService pessoaService = new PessoaService();
    private static final EnderecoService enderecoService = new EnderecoService();
    private static final AnuncioService anuncioService = new AnuncioService();
    private static final CompraService compraService = new CompraService();
    private static final MaterialService materialService = new MaterialService();

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
                    case 1 -> menuCadastro();
                    case 2 -> menuLogin();
                    case 0 -> System.out.println("Encerrando...");
                    default -> System.out.println("Opção inválida!");
                }
            } catch (RuntimeException erro) {
                mostrarErro(erro);
            }
        } while (opcao != 0);
    }

    // ---------- cadastro e login ----------

    private static void menuCadastro() {
        System.out.println("=== CADASTRO ===");
        System.out.println("1 - Cadastrar como VENDEDOR");
        System.out.println("2 - Cadastrar como COMPRADOR");
        int opcao = lerInteiro("Escolha: ");
        if (opcao != 1 && opcao != 2) {
            System.out.println("Opção inválida!");
            return;
        }

        System.out.print("Tipo de pessoa (PF ou PJ): ");
        String tipoPessoa = teclado.nextLine().trim().toUpperCase();
        System.out.print("Documento: ");
        String documento = teclado.nextLine().trim();
        System.out.print("Nome: ");
        String nome = teclado.nextLine().trim();
        System.out.print("E-mail: ");
        String email = teclado.nextLine().trim();
        System.out.print("Telefone: ");
        String telefone = teclado.nextLine().trim();
        System.out.print("Senha: ");
        String senhaHash = SenhaUtil.gerarHash(teclado.nextLine());

        Pessoa cadastrada = (opcao == 1)
                ? pessoaService.cadastrarVendedor(tipoPessoa, documento, nome, email, telefone, senhaHash)
                : pessoaService.cadastrarComprador(tipoPessoa, documento, nome, email, telefone, senhaHash);

        System.out.println("Cadastro realizado! ID: " + cadastrada.getIdPessoa());
        System.out.println("Nome: " + cadastrada.getNome());
        System.out.println("Perfil: " + cadastrada.getClass().getSimpleName());

        if (confirmar("Cadastrar um endereço para esta pessoa agora?")) {
            menuCadastrarEndereco(cadastrada.getIdPessoa());
        }
    }

    private static void menuLogin() {
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

        System.out.println("Login realizado com sucesso!");
        System.out.println("Bem-vindo(a), " + pessoa.getNome());
        System.out.println("ID: " + pessoa.getIdPessoa());
        System.out.println("Perfil: " + pessoa.getClass().getSimpleName());

        // A área de gerenciamento de anúncios é exclusiva do vendedor.
        if (pessoa instanceof Vendedor vendedor) {
            menuVendedor(vendedor);
        } else if (pessoa instanceof Comprador comprador) {
            menuComprador(comprador);
        }
    }

    // ---------- área exclusiva do comprador ----------

    private static void menuComprador(Comprador comprador) {
        int opcao;
        do {
            System.out.println();
            System.out.println("========== ÁREA DO COMPRADOR ==========");
            System.out.println("Olá, " + comprador.getNome());
            System.out.println("1 - Listar anúncios disponíveis");
            System.out.println("2 - Realizar uma compra");
            System.out.println("3 - Consultar histórico de compras");
            System.out.println("4 - Gerenciar meus endereços");
            System.out.println("0 - Sair da conta");
            opcao = lerInteiro("Escolha: ");

            try {
                switch (opcao) {
                    case 1 -> listarAnunciosDisponiveis();
                    case 2 -> menuRealizarCompra(comprador);
                    case 3 -> listarHistoricoCompras(comprador.getIdPessoa());
                    case 4 -> menuGerenciarEnderecos(comprador.getIdPessoa());
                    case 0 -> System.out.println("Você saiu da área do comprador.");
                    default -> System.out.println("Opção inválida!");
                }
            } catch (RuntimeException erro) {
                mostrarErro(erro);
            }
        } while (opcao != 0);
    }

    private static ArrayList<Anuncio> listarAnunciosDisponiveis() {
        ArrayList<Anuncio> anuncios = anuncioService.listarDisponiveis();
        if (anuncios.isEmpty()) {
            System.out.println("Não existem anúncios disponíveis no momento.");
            return anuncios;
        }

        System.out.println("========== ANÚNCIOS DISPONÍVEIS ==========");
        for (Anuncio anuncio : anuncios) {
            model.Material material = materialService.consultar(anuncio.getIdMaterial());
            System.out.println("ID do anúncio: " + anuncio.getIdAnuncio());
            System.out.println("Título: " + anuncio.getTitulo());
            System.out.println("Descrição: " + anuncio.getDescricao());
            System.out.println("Material: " + material.getNome());
            if (material.getCategoria() != null) {
                System.out.println("Categoria: " + material.getCategoria().getNome());
            }
            System.out.println("Estoque: " + anuncio.getQuantidade() + " " + anuncio.getUnidade());
            System.out.println("Preço por unidade: R$ " + anuncio.getPreco());
            System.out.println("Vendedor: " + anuncio.getNomeVendedor());
            System.out.println("------------------------------------------");
        }
        return anuncios;
    }

    private static void menuRealizarCompra(Comprador comprador) {
        ArrayList<Anuncio> anuncios = listarAnunciosDisponiveis();
        if (anuncios.isEmpty()) return;

        int idAnuncio = lerInteiro("Informe o ID do anúncio que deseja comprar: ");
        Anuncio selecionado = null;
        for (Anuncio anuncio : anuncios) {
            if (anuncio.getIdAnuncio().equals(idAnuncio)) {
                selecionado = anuncio;
                break;
            }
        }

        if (selecionado == null) {
            System.out.println("ID inválido. Escolha um anúncio da lista exibida.");
            return;
        }

        System.out.println("Anúncio selecionado: " + selecionado.getTitulo());
        System.out.println("Vendedor: " + selecionado.getNomeVendedor());
        System.out.println("Estoque disponível: " + selecionado.getQuantidade() + " " + selecionado.getUnidade());
        double quantidade = lerDouble("Informe a quantidade que deseja comprar: ");

        Compra compra = compraService.registrarCompra(
                comprador.getIdPessoa(), idAnuncio, quantidade);

        System.out.println("Compra realizada com sucesso!");
        System.out.println("Código da compra: " + compra.getIdCompra());
        System.out.println("Quantidade comprada: " + compra.getQuantidade() + " " + selecionado.getUnidade());
        System.out.println("Valor total: R$ " + compra.getPreco());
        System.out.println("Status: " + compra.getStatus());
    }

    private static void listarHistoricoCompras(Integer idComprador) {
        ArrayList<Compra> compras = compraService.listarPorComprador(idComprador);
        if (compras.isEmpty()) {
            System.out.println("Você ainda não realizou nenhuma compra.");
            return;
        }

        System.out.println("========== HISTÓRICO DE COMPRAS ==========");
        for (Compra compra : compras) {
            System.out.println("Código da compra: " + compra.getIdCompra());
            System.out.println("ID do anúncio: " + compra.getIdAnuncio());
            System.out.println("Quantidade: " + compra.getQuantidade());
            System.out.println("Valor total: R$ " + compra.getPreco());
            System.out.println("Data: " + compra.getDataCompra());
            System.out.println("Status: " + compra.getStatus());
            System.out.println("------------------------------------------");
        }
    }

    // ---------- área exclusiva do vendedor ----------

    private static void menuVendedor(Vendedor vendedor) {
        int opcao;
        do {
            System.out.println();
            System.out.println("========== ÁREA DO VENDEDOR ==========");
            System.out.println("Olá, " + vendedor.getNome());
            System.out.println("1 - Criar anúncio");
            System.out.println("2 - Listar meus anúncios");
            System.out.println("3 - Atualizar anúncio");
            System.out.println("4 - Excluir anúncio");
            System.out.println("5 - Gerenciar meus endereços");
            System.out.println("0 - Sair da conta");
            opcao = lerInteiro("Escolha: ");

            try {
                switch (opcao) {
                    case 1 -> menuCriarAnuncio(vendedor);
                    case 2 -> listarMeusAnuncios(vendedor.getIdPessoa());
                    case 3 -> menuAtualizarAnuncio(vendedor);
                    case 4 -> menuExcluirAnuncio(vendedor);
                    case 5 -> menuGerenciarEnderecos(vendedor.getIdPessoa());
                    case 0 -> System.out.println("Você saiu da área do vendedor.");
                    default -> System.out.println("Opção inválida!");
                }
            } catch (RuntimeException erro) {
                mostrarErro(erro);
            }
        } while (opcao != 0);
    }

    private static void menuCriarAnuncio(Vendedor vendedor) {
        ArrayList<Endereco> enderecos = enderecoService.listar(vendedor.getIdPessoa());
        if (enderecos.isEmpty()) {
            System.out.println("Você precisa cadastrar um endereço antes de criar um anúncio.");
            if (confirmar("Deseja cadastrar um endereço agora?")) {
                menuCadastrarEndereco(vendedor.getIdPessoa());
            }
            return;
        }

        Anuncio anuncio = lerDadosAnuncio(vendedor, null, enderecos);
        anuncioService.cadastrar(anuncio);
        System.out.println("Anúncio cadastrado com sucesso! ID: " + anuncio.getIdAnuncio());
    }

    private static void menuAtualizarAnuncio(Vendedor vendedor) {
        ArrayList<Anuncio> anuncios = listarMeusAnuncios(vendedor.getIdPessoa());
        if (anuncios.isEmpty()) return;

        int id = lerInteiro("Informe o ID do anúncio que deseja atualizar: ");
        Anuncio escolhido = null;
        for (Anuncio anuncio : anuncios) {
            if (anuncio.getIdAnuncio() == id) {
                escolhido = anuncio;
                break;
            }
        }
        if (escolhido == null) {
            System.out.println("Anúncio não encontrado entre os seus anúncios.");
            return;
        }

        ArrayList<Endereco> enderecos = enderecoService.listar(vendedor.getIdPessoa());
        Anuncio atualizado = lerDadosAnuncio(vendedor, escolhido.getIdAnuncio(), enderecos);
        if (anuncioService.atualizar(atualizado)) {
            System.out.println("Anúncio atualizado com sucesso!");
        } else {
            System.out.println("Não foi possível atualizar o anúncio.");
        }
    }

    private static void menuExcluirAnuncio(Vendedor vendedor) {
        ArrayList<Anuncio> anuncios = listarMeusAnuncios(vendedor.getIdPessoa());
        if (anuncios.isEmpty()) return;

        int id = lerInteiro("Informe o ID do anúncio que deseja excluir: ");
        if (!confirmar("Deseja realmente excluir o anúncio " + id + "?")) return;

        if (anuncioService.excluir(id, vendedor.getIdPessoa())) {
            System.out.println("Anúncio excluído com sucesso!");
        } else {
            System.out.println("Anúncio não encontrado ou não pertence a você.");
        }
    }

    private static ArrayList<Anuncio> listarMeusAnuncios(Integer idVendedor) {
        ArrayList<Anuncio> anuncios = anuncioService.listarPorVendedor(idVendedor);
        if (anuncios.isEmpty()) {
            System.out.println("Você ainda não possui anúncios.");
            return anuncios;
        }
        System.out.println("========== MEUS ANÚNCIOS ==========");
        for (Anuncio anuncio : anuncios) {
            System.out.println("ID: " + anuncio.getIdAnuncio());
            System.out.println("Título: " + anuncio.getTitulo());
            System.out.println("Descrição: " + anuncio.getDescricao());
            model.Material material = materialService.consultar(anuncio.getIdMaterial());
            System.out.println("Material: " + material.getNome()
                    + " - Categoria: " + material.getCategoria().getNome());
            System.out.println("Endereço (ID): " + anuncio.getIdEndereco());
            System.out.println("Quantidade: " + anuncio.getQuantidade() + " " + anuncio.getUnidade());
            System.out.println("Preço: R$ " + anuncio.getPreco());
            System.out.println("Status: " + anuncio.getStatusAnuncio());
            System.out.println("Publicado em: " + anuncio.getDataPublicacao());
            System.out.println("------------------------------------");
        }
        return anuncios;
    }

    private static Anuncio lerDadosAnuncio(Vendedor vendedor, Integer idAnuncio,
                                           ArrayList<Endereco> enderecos) {
        Anuncio anuncio = new Anuncio();
        anuncio.setIdAnuncio(idAnuncio);
        anuncio.setIdVendedor(vendedor.getIdPessoa());

        ArrayList<model.Categoria> categorias = materialService.listarCategorias();
        if (categorias.isEmpty()) {
            throw new IllegalStateException("Não existem categorias cadastradas no catálogo.");
        }

        System.out.println("=== CATEGORIAS DISPONÍVEIS ===");
        for (model.Categoria categoria : categorias) {
            System.out.println("[" + categoria.getIdCategoria() + "] " + categoria.getNome());
        }

        int idCategoria = lerInteiro("Escolha uma categoria: ");
        model.Categoria categoriaSelecionada = materialService.consultarCategoria(idCategoria);

        ArrayList<model.Material> materiais = materialService.listarPorCategoria(idCategoria);
        if (materiais.isEmpty()) {
            throw new IllegalStateException(
                    "Não existem materiais cadastrados na categoria "
                            + categoriaSelecionada.getNome() + ".");
        }

        System.out.println("=== MATERIAIS DE " + categoriaSelecionada.getNome().toUpperCase() + " ===");
        for (model.Material material : materiais) {
            System.out.println("[" + material.getIdMaterial() + "] " + material.getNome());
        }

        int idMaterial = lerInteiro("ID do material desejado: ");
        model.Material materialSelecionado = materialService.consultar(idMaterial);
        if (!materialSelecionado.getIdCategoria().equals(idCategoria)) {
            throw new IllegalArgumentException(
                    "O material selecionado não pertence à categoria escolhida.");
        }
        anuncio.setIdMaterial(materialSelecionado.getIdMaterial());
        System.out.println("=== SELECIONE O ENDEREÇO DE RETIRADA ===");
        for (Endereco endereco : enderecos) {
            System.out.println(descrever(endereco));
        }
        int idEndereco = lerInteiro("ID do endereço: ");
        anuncio.setIdEndereco(idEndereco);

        System.out.print("Título: ");
        anuncio.setTitulo(teclado.nextLine());
        System.out.print("Descrição: ");
        anuncio.setDescricao(teclado.nextLine());
        anuncio.setQuantidade(lerDouble("Quantidade: "));
        anuncio.setUnidade(lerUnidade());
        anuncio.setPreco(lerDouble("Preço (use ponto ou vírgula): R$ "));
        return anuncio;
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

        Endereco endereco = enderecoService.cadastrar(idPessoa, logradouro, numero,
                complemento, bairro, cep, cidade, uf);
        System.out.println("Endereço cadastrado! ID: " + endereco.getIdEndereco());
    }

    private static void menuGerenciarEnderecos(Integer idPessoa) {
        int opcao;
        do {
            ArrayList<Endereco> enderecos = enderecoService.listar(idPessoa);

            System.out.println();
            System.out.println("========== MEUS ENDEREÇOS ==========");
            if (enderecos.isEmpty()) {
                System.out.println("Você ainda não possui endereços cadastrados.");
            } else {
                for (Endereco endereco : enderecos) {
                    System.out.println(descrever(endereco));
                }
            }

            System.out.println("1 - Adicionar novo endereço");
            System.out.println("2 - Excluir endereço");
            System.out.println("0 - Voltar");
            opcao = lerInteiro("Escolha: ");

            switch (opcao) {
                case 1 -> menuCadastrarEndereco(idPessoa);
                case 2 -> menuExcluirEndereco(idPessoa, enderecos);
                case 0 -> System.out.println("Voltando ao menu anterior...");
                default -> System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private static void menuExcluirEndereco(Integer idPessoa, ArrayList<Endereco> enderecos) {
        if (enderecos.isEmpty()) {
            System.out.println("Não há endereços para excluir.");
            return;
        }

        int idEndereco = lerInteiro("Informe o ID do endereço que deseja excluir: ");
        if (!confirmar("Tem certeza de que deseja excluir o endereço " + idEndereco + "?")) {
            System.out.println("Exclusão cancelada.");
            return;
        }

        try {
            boolean excluido = enderecoService.excluir(idPessoa, idEndereco);
            if (excluido) {
                System.out.println("Endereço excluído com sucesso!");
            } else {
                System.out.println("Nenhum endereço foi excluído.");
            }
        } catch (RuntimeException erro) {
            System.out.println("Não foi possível excluir o endereço: " + erro.getMessage());
        }
    }

    private static String descrever(Endereco endereco) {
        String complemento = endereco.getComplemento() == null ? "" : " (" + endereco.getComplemento() + ")";
        return "[" + endereco.getIdEndereco() + "] " + endereco.getLogradouro() + ", "
                + endereco.getNumero() + complemento + " - " + endereco.getBairro()
                + ", " + endereco.getCidade() + "/" + endereco.getUf() + " - CEP " + endereco.getCep();
    }

    // ---------- métodos auxiliares ----------

    private static int lerInteiro(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                return Integer.parseInt(teclado.nextLine().trim());
            } catch (NumberFormatException erro) {
                System.out.println("Digite um número inteiro válido.");
            }
        }
    }

    private static String lerUnidade() {
        while (true) {
            System.out.println("=== UNIDADES DISPONÍVEIS ===");
            System.out.println("1 - Quilograma (kg)");
            System.out.println("2 - Grama (g)");
            System.out.println("3 - Tonelada (t)");
            System.out.println("4 - Unidade (un)");
            System.out.println("5 - Litro (L)");
            System.out.println("6 - Metro cúbico (m³)");
            System.out.println("7 - Metro (m)");
            System.out.println("8 - Pacote (pct)");

            int opcao = lerInteiro("Escolha a unidade: ");

            switch (opcao) {
                case 1:
                    return "kg";
                case 2:
                    return "g";
                case 3:
                    return "t";
                case 4:
                    return "un";
                case 5:
                    return "L";
                case 6:
                    return "m³";
                case 7:
                    return "m";
                case 8:
                    return "pct";
                default:
                    System.out.println("Opção inválida! Escolha uma unidade da lista.");
            }
        }
    }

    private static double lerDouble(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            try {
                String valor = teclado.nextLine().trim().replace(',', '.');
                return Double.parseDouble(valor);
            } catch (NumberFormatException erro) {
                System.out.println("Digite um número válido.");
            }
        }
    }

    private static boolean confirmar(String mensagem) {
        while (true) {
            System.out.print(mensagem + " (S/N): ");
            String resposta = teclado.nextLine().trim();
            if (resposta.equalsIgnoreCase("s")) return true;
            if (resposta.equalsIgnoreCase("n")) return false;
            System.out.println("Responda com S ou N.");
        }
    }

    private static void mostrarErro(RuntimeException erro) {
        System.out.println("ERRO: " + erro.getMessage());
        Throwable causa = erro.getCause();
        while (causa != null) {
            System.out.println("      causa: " + causa.getMessage());
            causa = causa.getCause();
        }
    }
}
