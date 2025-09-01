package service;

import model.Cliente;
import model.Conta;
import repository.ContaRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável pela lógica de negócio das operações bancárias.
 * Implementa todas as funcionalidades do sistema bancário com validações robustas.
 */
public class ContaService {
    private final ContaRepository contaRepository;

    /**
     * Construtor do serviço.
     * 
     * @param contaRepository Repositório de contas
     */
    public ContaService(ContaRepository contaRepository) {
        if (contaRepository == null) {
            throw new IllegalArgumentException("ContaRepository não pode ser nulo");
        }
        this.contaRepository = contaRepository;
    }

    /**
     * Cria uma nova conta bancária.
     * 
     * @param nome Nome do cliente
     * @param cpf CPF do cliente
     * @return Conta criada
     * @throws IllegalArgumentException se os dados forem inválidos ou se já existir conta para o CPF
     */
    public Conta criarConta(String nome, String cpf) {
        // Validações básicas
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF é obrigatório");
        }

        // Verifica se já existe conta para este CPF
        if (contaRepository.existePorCpf(cpf)) {
            throw new IllegalArgumentException("Já existe uma conta cadastrada para este CPF");
        }

        try {
            // Cria o cliente (validação de CPF é feita na classe Cliente)
            Cliente cliente = new Cliente(nome, cpf);
            
            // Cria a conta
            Conta conta = new Conta(cliente);
            
            // Salva no repositório
            contaRepository.salvar(conta);
            
            return conta;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Erro ao criar conta: " + e.getMessage());
        }
    }

    /**
     * Realiza um depósito em uma conta.
     * 
     * @param idConta ID da conta
     * @param valor Valor a ser depositado
     * @throws IllegalArgumentException se os parâmetros forem inválidos ou conta não existir
     */
    public void depositar(String idConta, BigDecimal valor) {
        // Validações
        if (idConta == null || idConta.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da conta é obrigatório");
        }
        
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do depósito deve ser positivo");
        }

        // Busca a conta
        Optional<Conta> contaOpt = contaRepository.buscarPorId(idConta);
        if (contaOpt.isEmpty()) {
            throw new IllegalArgumentException("Conta não encontrada com ID: " + idConta);
        }

        // Realiza o depósito
        Conta conta = contaOpt.get();
        conta.depositar(valor);
    }

    /**
     * Realiza um saque de uma conta.
     * 
     * @param idConta ID da conta
     * @param valor Valor a ser sacado
     * @throws IllegalArgumentException se os parâmetros forem inválidos, conta não existir ou saldo insuficiente
     */
    public void sacar(String idConta, BigDecimal valor) {
        // Validações
        if (idConta == null || idConta.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da conta é obrigatório");
        }
        
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do saque deve ser positivo");
        }

        // Busca a conta
        Optional<Conta> contaOpt = contaRepository.buscarPorId(idConta);
        if (contaOpt.isEmpty()) {
            throw new IllegalArgumentException("Conta não encontrada com ID: " + idConta);
        }

        // Realiza o saque (validação de saldo é feita na classe Conta)
        Conta conta = contaOpt.get();
        conta.sacar(valor);
    }

    /**
     * Realiza uma transferência entre contas.
     * 
     * @param idContaOrigem ID da conta de origem
     * @param idContaDestino ID da conta de destino
     * @param valor Valor a ser transferido
     * @throws IllegalArgumentException se os parâmetros forem inválidos, contas não existirem ou saldo insuficiente
     */
    public void transferir(String idContaOrigem, String idContaDestino, BigDecimal valor) {
        // Validações básicas
        if (idContaOrigem == null || idContaOrigem.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da conta de origem é obrigatório");
        }
        
        if (idContaDestino == null || idContaDestino.trim().isEmpty()) {
            throw new IllegalArgumentException("ID da conta de destino é obrigatório");
        }
        
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor da transferência deve ser positivo");
        }

        // Verifica se as contas são diferentes
        if (idContaOrigem.trim().equals(idContaDestino.trim())) {
            throw new IllegalArgumentException("Conta de origem e destino devem ser diferentes");
        }

        // Busca as contas
        Optional<Conta> contaOrigemOpt = contaRepository.buscarPorId(idContaOrigem);
        if (contaOrigemOpt.isEmpty()) {
            throw new IllegalArgumentException("Conta de origem não encontrada com ID: " + idContaOrigem);
        }

        Optional<Conta> contaDestinoOpt = contaRepository.buscarPorId(idContaDestino);
        if (contaDestinoOpt.isEmpty()) {
            throw new IllegalArgumentException("Conta de destino não encontrada com ID: " + idContaDestino);
        }

        Conta contaOrigem = contaOrigemOpt.get();
        Conta contaDestino = contaDestinoOpt.get();

        // Verifica saldo suficiente
        if (!contaOrigem.temSaldoSuficiente(valor)) {
            throw new IllegalArgumentException("Saldo insuficiente na conta de origem");
        }

        // Realiza a transferência
        contaOrigem.sacar(valor);
        contaDestino.depositar(valor);
    }

    /**
     * Busca uma conta pelo ID.
     * 
     * @param id ID da conta
     * @return Optional contendo a conta se encontrada
     */
    public Optional<Conta> buscarContaPorId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        return contaRepository.buscarPorId(id);
    }

    /**
     * Busca contas pelo nome do cliente.
     * 
     * @param nome Nome ou parte do nome do cliente
     * @return Lista de contas encontradas
     */
    public List<Conta> buscarContasPorNome(String nome) {
        return contaRepository.buscarPorNome(nome);
    }

    /**
     * Lista todas as contas cadastradas ordenadas por nome do cliente.
     * 
     * @return Lista de todas as contas
     */
    public List<Conta> listarTodasContas() {
        return contaRepository.listarTodasOrdenadas();
    }

    /**
     * Verifica se existe uma conta com o ID especificado.
     * 
     * @param id ID a ser verificado
     * @return true se existe, false caso contrário
     */
    public boolean existeContaComId(String id) {
        return contaRepository.existePorId(id);
    }

    /**
     * Retorna o número total de contas cadastradas.
     * 
     * @return número de contas
     */
    public int getTotalContas() {
        return contaRepository.getTotalContas();
    }

    /**
     * Consulta o saldo de uma conta.
     * 
     * @param idConta ID da conta
     * @return Saldo da conta
     * @throws IllegalArgumentException se a conta não existir
     */
    public BigDecimal consultarSaldo(String idConta) {
        Optional<Conta> contaOpt = buscarContaPorId(idConta);
        if (contaOpt.isEmpty()) {
            throw new IllegalArgumentException("Conta não encontrada com ID: " + idConta);
        }
        return contaOpt.get().getSaldo();
    }
}