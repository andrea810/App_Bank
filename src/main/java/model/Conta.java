package model;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

/**
 * Classe que representa uma conta bancária.
 * Contém informações do cliente, saldo e ID único.
 */
public class Conta {
    private final String id;
    private final Cliente cliente;
    private BigDecimal saldo;
    private static final Random random = new Random();
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    /**
     * Construtor da classe Conta.
     * Gera automaticamente um ID aleatório de 6 dígitos.
     * 
     * @param cliente Cliente proprietário da conta
     * @throws IllegalArgumentException se o cliente for nulo
     */
    public Conta(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não pode ser nulo");
        }
        
        this.id = gerarIdAleatorio();
        this.cliente = cliente;
        this.saldo = BigDecimal.ZERO;
    }

    /**
     * Gera um ID aleatório de 6 dígitos.
     * 
     * @return ID de 6 dígitos como String
     */
    private String gerarIdAleatorio() {
        int numero = 100000 + random.nextInt(900000); // Gera número entre 100000 e 999999
        return String.valueOf(numero);
    }

    /**
     * Retorna o ID da conta.
     * 
     * @return ID da conta
     */
    public String getId() {
        return id;
    }

    /**
     * Retorna o cliente proprietário da conta.
     * 
     * @return cliente da conta
     */
    public Cliente getCliente() {
        return cliente;
    }

    /**
     * Retorna o saldo atual da conta.
     * 
     * @return saldo da conta
     */
    public BigDecimal getSaldo() {
        return saldo;
    }

    /**
     * Retorna o saldo formatado em moeda brasileira.
     * 
     * @return saldo formatado (ex: R$ 1.234,56)
     */
    public String getSaldoFormatado() {
        return currencyFormat.format(saldo);
    }

    /**
     * Realiza um depósito na conta.
     * 
     * @param valor Valor a ser depositado
     * @throws IllegalArgumentException se o valor for inválido
     */
    public void depositar(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do depósito deve ser positivo");
        }
        
        this.saldo = this.saldo.add(valor);
    }

    /**
     * Realiza um saque na conta.
     * 
     * @param valor Valor a ser sacado
     * @throws IllegalArgumentException se o valor for inválido ou insuficiente
     */
    public void sacar(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do saque deve ser positivo");
        }
        
        if (valor.compareTo(this.saldo) > 0) {
            throw new IllegalArgumentException("Saldo insuficiente para saque");
        }
        
        this.saldo = this.saldo.subtract(valor);
    }

    /**
     * Verifica se a conta tem saldo suficiente para um valor.
     * 
     * @param valor Valor a ser verificado
     * @return true se o saldo for suficiente, false caso contrário
     */
    public boolean temSaldoSuficiente(BigDecimal valor) {
        return valor != null && this.saldo.compareTo(valor) >= 0;
    }

    /**
     * Formata um valor em moeda brasileira.
     * 
     * @param valor Valor a ser formatado
     * @return valor formatado (ex: R$ 1.234,56)
     */
    public static String formatarMoeda(BigDecimal valor) {
        return currencyFormat.format(valor != null ? valor : BigDecimal.ZERO);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Conta conta = (Conta) obj;
        return Objects.equals(id, conta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Conta{" +
                "id='" + id + '\'' +
                ", cliente=" + cliente.getNome() +
                ", saldo=" + getSaldoFormatado() +
                '}';
    }
}