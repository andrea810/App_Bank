package model;

import java.util.Objects;

/**
 * Classe que representa um cliente do banco.
 * Contém informações básicas como nome e CPF.
 */
public class Cliente {
    private final String nome;
    private final String cpf;

    /**
     * Construtor da classe Cliente.
     * 
     * @param nome Nome completo do cliente
     * @param cpf CPF do cliente (deve ser válido)
     * @throws IllegalArgumentException se o nome for inválido ou CPF for inválido
     */
    public Cliente(String nome, String cpf) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        if (!isValidCPF(cpf)) {
            throw new IllegalArgumentException("CPF inválido");
        }
        
        this.nome = nome.trim();
        this.cpf = cpf.replaceAll("[^0-9]", ""); // Remove formatação
    }

    /**
     * Retorna o nome do cliente.
     * 
     * @return nome do cliente
     */
    public String getNome() {
        return nome;
    }

    /**
     * Retorna o CPF do cliente.
     * 
     * @return CPF do cliente
     */
    public String getCpf() {
        return cpf;
    }

    /**
     * Retorna o CPF formatado (XXX.XXX.XXX-XX).
     * 
     * @return CPF formatado
     */
    public String getCpfFormatado() {
        return cpf.substring(0, 3) + "." + 
               cpf.substring(3, 6) + "." + 
               cpf.substring(6, 9) + "-" + 
               cpf.substring(9, 11);
    }

    /**
     * Valida se um CPF é válido usando o algoritmo oficial.
     * 
     * @param cpf CPF a ser validado
     * @return true se o CPF for válido, false caso contrário
     */
    private boolean isValidCPF(String cpf) {
        if (cpf == null) {
            return false;
        }
        
        // Remove formatação
        cpf = cpf.replaceAll("[^0-9]", "");
        
        // Verifica se tem 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }
        
        // Verifica se todos os dígitos são iguais
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        
        // Calcula o primeiro dígito verificador
        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int primeiroDigito = 11 - (soma % 11);
        if (primeiroDigito >= 10) {
            primeiroDigito = 0;
        }
        
        // Calcula o segundo dígito verificador
        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int segundoDigito = 11 - (soma % 11);
        if (segundoDigito >= 10) {
            segundoDigito = 0;
        }
        
        // Verifica se os dígitos calculados conferem
        return Character.getNumericValue(cpf.charAt(9)) == primeiroDigito &&
               Character.getNumericValue(cpf.charAt(10)) == segundoDigito;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cliente cliente = (Cliente) obj;
        return Objects.equals(cpf, cliente.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cpf);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "nome='" + nome + '\'' +
                ", cpf='" + getCpfFormatado() + '\'' +
                '}';
    }
}