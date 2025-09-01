package repository;

import model.Conta;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repositório responsável pelo armazenamento e recuperação de contas em memória.
 * Implementa operações CRUD básicas para contas bancárias.
 */
public class ContaRepository {
    private final Map<String, Conta> contas;

    /**
     * Construtor do repositório.
     * Inicializa a estrutura de dados para armazenamento das contas.
     */
    public ContaRepository() {
        this.contas = new HashMap<>();
    }

    /**
     * Salva uma conta no repositório.
     * 
     * @param conta Conta a ser salva
     * @throws IllegalArgumentException se a conta for nula ou se já existir uma conta com o mesmo ID
     */
    public void salvar(Conta conta) {
        if (conta == null) {
            throw new IllegalArgumentException("Conta não pode ser nula");
        }
        
        if (contas.containsKey(conta.getId())) {
            throw new IllegalArgumentException("Já existe uma conta com o ID: " + conta.getId());
        }
        
        contas.put(conta.getId(), conta);
    }

    /**
     * Busca uma conta pelo ID.
     * 
     * @param id ID da conta a ser buscada
     * @return Optional contendo a conta se encontrada, Optional.empty() caso contrário
     */
    public Optional<Conta> buscarPorId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return Optional.ofNullable(contas.get(id.trim()));
    }

    /**
     * Busca contas pelo nome do cliente (busca parcial, case-insensitive).
     * 
     * @param nome Nome ou parte do nome do cliente
     * @return Lista de contas que correspondem ao critério de busca
     */
    public List<Conta> buscarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String nomeBusca = nome.trim().toLowerCase();
        
        return contas.values().stream()
                .filter(conta -> conta.getCliente().getNome().toLowerCase().contains(nomeBusca))
                .collect(Collectors.toList());
    }

    /**
     * Busca uma conta pelo CPF do cliente.
     * 
     * @param cpf CPF do cliente
     * @return Optional contendo a conta se encontrada, Optional.empty() caso contrário
     */
    public Optional<Conta> buscarPorCpf(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        
        return contas.values().stream()
                .filter(conta -> conta.getCliente().getCpf().equals(cpfLimpo))
                .findFirst();
    }

    /**
     * Lista todas as contas cadastradas.
     * 
     * @return Lista com todas as contas
     */
    public List<Conta> listarTodas() {
        return new ArrayList<>(contas.values());
    }

    /**
     * Lista todas as contas ordenadas por nome do cliente.
     * 
     * @return Lista de contas ordenadas por nome
     */
    public List<Conta> listarTodasOrdenadas() {
        return contas.values().stream()
                .sorted(Comparator.comparing(conta -> conta.getCliente().getNome()))
                .collect(Collectors.toList());
    }

    /**
     * Remove uma conta do repositório.
     * 
     * @param id ID da conta a ser removida
     * @return true se a conta foi removida, false se não foi encontrada
     */
    public boolean remover(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        
        return contas.remove(id.trim()) != null;
    }

    /**
     * Verifica se existe uma conta com o ID especificado.
     * 
     * @param id ID a ser verificado
     * @return true se existe, false caso contrário
     */
    public boolean existePorId(String id) {
        return id != null && contas.containsKey(id.trim());
    }

    /**
     * Verifica se existe uma conta para o CPF especificado.
     * 
     * @param cpf CPF a ser verificado
     * @return true se existe, false caso contrário
     */
    public boolean existePorCpf(String cpf) {
        return buscarPorCpf(cpf).isPresent();
    }

    /**
     * Retorna o número total de contas cadastradas.
     * 
     * @return número de contas
     */
    public int getTotalContas() {
        return contas.size();
    }

    /**
     * Limpa todas as contas do repositório.
     * Método útil para testes.
     */
    public void limparTodas() {
        contas.clear();
    }
}