
package service;

import dao.IPessoaDao;
import dao.PessoaDao;
import model.Pessoa;
import model.Vendedor;
import model.Comprador;
import util.SenhaUtil;

public class PessoaService {

    private final IPessoaDao pessoaDao;

    public PessoaService() {
        this.pessoaDao = new PessoaDao();
    }

    // Permite trocar o DAO (útil para testar o service sem o banco).
    public PessoaService(IPessoaDao pessoaDao) {
        this.pessoaDao = pessoaDao;
    }

    public Pessoa cadastrarVendedor(
            String tipoPessoa,
            String documento,
            String nome,
            String email,
            String telefone,
            String senhaHash) {

        Vendedor vendedor = new Vendedor();

        vendedor.setTipoPessoa(tipoPessoa);
        vendedor.setDocumento(documento);
        vendedor.setNome(nome);
        vendedor.setEmail(email);
        vendedor.setTelefone(telefone);
        vendedor.setSenhaHash(senhaHash);

        Pessoa cadastrada = pessoaDao.cadastrar(vendedor);

        return pessoaDao.habilitarComoVendedor(
                cadastrada.getIdPessoa()
        );
    }

    public Pessoa cadastrarComprador(
            String tipoPessoa,
            String documento,
            String nome,
            String email,
            String telefone,
            String senhaHash) {

        Comprador comprador = new Comprador();

        comprador.setTipoPessoa(tipoPessoa);
        comprador.setDocumento(documento);
        comprador.setNome(nome);
        comprador.setEmail(email);
        comprador.setTelefone(telefone);
        comprador.setSenhaHash(senhaHash);

        Pessoa cadastrada = pessoaDao.cadastrar(comprador);

        return pessoaDao.habilitarComoComprador(
                cadastrada.getIdPessoa()
        );
    }

    // Devolve a pessoa ou null se não existir.
    public Pessoa consultar(Integer idPessoa) {
        return pessoaDao.consultar(idPessoa);
    }

    // Para uma pessoa que já existe passar a ter também o papel de vendedor.
    public Pessoa habilitarComoVendedor(Integer idPessoa) {

        Pessoa habilitada = pessoaDao.habilitarComoVendedor(idPessoa);

        if (habilitada == null) {
            throw new IllegalStateException(
                    "Não foi possível habilitar a pessoa " + idPessoa + " como vendedor.");
        }
        return habilitada;
    }

    // Para uma pessoa que já existe passar a ter também o papel de comprador.
    public Pessoa habilitarComoComprador(Integer idPessoa) {

        Pessoa habilitada = pessoaDao.habilitarComoComprador(idPessoa);

        if (habilitada == null) {
            throw new IllegalStateException(
                    "Não foi possível habilitar a pessoa " + idPessoa + " como comprador.");
        }
        return habilitada;
    }

    public boolean excluir(Integer idPessoa) {
        return pessoaDao.excluir(idPessoa);
    }

    public Pessoa login(String email, String senha) {

        if (email == null || email.isBlank()
                || senha == null || senha.isEmpty()) {
            return null;
        }

        Pessoa pessoa = pessoaDao.consultarPorEmail(email.trim());

        if (pessoa == null) {
            return null;
        }

        boolean senhaCorreta = SenhaUtil.verificar(
                senha,
                pessoa.getSenhaHash()
        );

        if (!senhaCorreta) {
            return null;
        }

        return pessoa;
    }
}




