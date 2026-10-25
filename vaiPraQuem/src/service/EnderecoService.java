package service;

import dao.EnderecoDao;
import dao.IEnderecoDao;
import dao.IPessoaDao;
import dao.PessoaDao;
import model.Endereco;

import java.util.ArrayList;

public class EnderecoService {

    private final IEnderecoDao enderecoDao;
    private final IPessoaDao pessoaDao;

    public EnderecoService() {
        this.enderecoDao = new EnderecoDao();
        this.pessoaDao = new PessoaDao();
    }

    // Permite trocar os DAOs (útil para testar o service sem o banco).
    public EnderecoService(IEnderecoDao enderecoDao, IPessoaDao pessoaDao) {
        this.enderecoDao = enderecoDao;
        this.pessoaDao = pessoaDao;
    }

    // Atrela um novo endereço a uma pessoa que já existe.
    public Endereco cadastrar(
            Integer idPessoa,
            String logradouro,
            String numero,
            String complemento,
            String bairro,
            String cep,
            String cidade,
            String uf) {

        garantirPessoa(idPessoa);

        Endereco endereco = new Endereco();

        endereco.setIdPessoa(idPessoa);
        endereco.setLogradouro(obrigatorio(logradouro, "logradouro"));
        endereco.setNumero(obrigatorio(numero, "número"));
        endereco.setComplemento(opcional(complemento));
        endereco.setBairro(obrigatorio(bairro, "bairro"));
        endereco.setCep(normalizarCep(cep));
        endereco.setCidade(obrigatorio(cidade, "cidade"));
        endereco.setUf(normalizarUf(uf));

        return enderecoDao.cadastrar(endereco);
    }

    // Todos os endereços de uma pessoa.
    public ArrayList<Endereco> listar(Integer idPessoa) {
        garantirPessoa(idPessoa);
        return enderecoDao.enderecos(idPessoa);
    }

    // Exclui UM endereço, mas só se ele pertencer mesmo à pessoa informada.
    public boolean excluir(Integer idPessoa, Integer idEndereco) {
        garantirPessoa(idPessoa);

        if (idEndereco == null) {
            throw new IllegalArgumentException("Informe o código do endereço.");
        }

        Endereco endereco = enderecoDao.consultar(idEndereco);

        if (endereco == null) {
            throw new IllegalArgumentException("Endereço " + idEndereco + " não encontrado.");
        }
        if (!idPessoa.equals(endereco.getIdPessoa())) {
            throw new IllegalArgumentException(
                    "O endereço " + idEndereco + " não pertence à pessoa " + idPessoa + ".");
        }

        return enderecoDao.excluir(idEndereco);
    }

    // Exclui TODOS os endereços da pessoa e devolve quantos foram apagados.
    public int excluirTodos(Integer idPessoa) {
        garantirPessoa(idPessoa);
        return enderecoDao.excluirDaPessoa(idPessoa);
    }

    // ---------- validações ----------

    private void garantirPessoa(Integer idPessoa) {
        if (idPessoa == null) {
            throw new IllegalArgumentException("Informe o código da pessoa.");
        }
        if (pessoaDao.consultar(idPessoa) == null) {
            throw new IllegalArgumentException("Pessoa " + idPessoa + " não encontrada.");
        }
    }

    private String obrigatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O campo " + campo + " é obrigatório.");
        }
        return valor.trim();
    }

    // Campo opcional: vazio vira null (NULL no banco).
    private String opcional(String valor) {
        return (valor == null || valor.isBlank()) ? null : valor.trim();
    }

    // "18010-000" vira "18010000".
    private String normalizarCep(String cep) {
        String digitos = (cep == null) ? "" : cep.replaceAll("\\D", "");

        if (digitos.length() != 8) {
            throw new IllegalArgumentException("CEP inválido: informe 8 dígitos.");
        }
        return digitos;
    }

    // "sp" vira "SP".
    private String normalizarUf(String uf) {
        String sigla = (uf == null) ? "" : uf.trim().toUpperCase();

        if (!sigla.matches("[A-Z]{2}")) {
            throw new IllegalArgumentException("UF inválida: informe 2 letras (ex.: SP).");
        }
        return sigla;
    }
}
