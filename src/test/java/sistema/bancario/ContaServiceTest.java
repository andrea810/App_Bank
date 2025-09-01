package sistema.bancario;

import model.Cliente;
import model.Conta;
import repository.ContaRepository;
import service.ContaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para o ContaService.
 * Valida todas as funcionalidades do sistema bancário.
 */
@DisplayName("Testes do Sistema Bancário")
class ContaServiceTest {
    
    private ContaService contaService;
    private ContaRepository contaRepository;
    
    @BeforeEach
    void setUp() {
        contaRepository = new ContaRepository();
        contaService = new ContaService(contaRepository);
    }
    
    @Nested
    @DisplayName("Testes de Criação de Conta")
    class TestsCriacaoConta {
        
        @Test
        @DisplayName("Deve criar conta com dados válidos")
        void deveCriarContaComSucesso() {
            // Given
            String nome = "João Silva";
            String cpf = "11144477735";
            
            // When
            Conta conta = contaService.criarConta(nome, cpf);
            
            // Then
            assertNotNull(conta);
            assertEquals(nome, conta.getCliente().getNome());
            assertEquals(cpf, conta.getCliente().getCpf());
            assertEquals(BigDecimal.ZERO, conta.getSaldo());
            assertNotNull(conta.getId());
            assertEquals(6, conta.getId().length());
        }
        
        @Test
        @DisplayName("Deve gerar ID único para cada conta")
        void deveGerarIdUnicoParaCadaConta() {
            // Given
            String nome1 = "João Silva";
            String cpf1 = "11144477735";
            String nome2 = "Maria Santos";
            String cpf2 = "11122233396";
            
            // When
            Conta conta1 = contaService.criarConta(nome1, cpf1);
            Conta conta2 = contaService.criarConta(nome2, cpf2);
            
            // Then
            assertNotEquals(conta1.getId(), conta2.getId());
        }
        
        @Test
        @DisplayName("Deve rejeitar criação com nome vazio")
        void deveRejeitarCriacaoComNomeVazio() {
            // Given
            String nome = "";
            String cpf = "11144477735";
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contaService.criarConta(nome, cpf)
            );
            
            assertTrue(exception.getMessage().contains("Nome"));
        }
        
        @Test
        @DisplayName("Deve rejeitar criação com CPF inválido")
        void deveRejeitarCriacaoComCpfInvalido() {
            // Given
            String nome = "João Silva";
            String cpf = "12345678900"; // CPF inválido
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contaService.criarConta(nome, cpf)
            );
            
            assertTrue(exception.getMessage().contains("CPF"));
        }
        
        @Test
        @DisplayName("Deve rejeitar criação com CPF já cadastrado")
        void deveRejeitarCriacaoComCpfJaCadastrado() {
            // Given
            String nome1 = "João Silva";
            String nome2 = "João Santos";
            String cpf = "11144477735";
            
            contaService.criarConta(nome1, cpf);
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contaService.criarConta(nome2, cpf)
            );
            
            assertTrue(exception.getMessage().contains("Já existe"));
        }
    }
    
    @Nested
    @DisplayName("Testes de Depósito")
    class TestsDeposito {
        
        private Conta conta;
        
        @BeforeEach
        void setUp() {
            conta = contaService.criarConta("João Silva", "11144477735");
        }
        
        @Test
        @DisplayName("Deve realizar depósito com valor válido")
        void deveRealizarDepositoComValorValido() {
            // Given
            BigDecimal valor = new BigDecimal("100.00");
            
            // When
            contaService.depositar(conta.getId(), valor);
            
            // Then
            assertEquals(valor, conta.getSaldo());
        }
        
        @Test
        @DisplayName("Deve acumular múltiplos depósitos")
        void deveAcumularMultiplosDepositos() {
            // Given
            BigDecimal valor1 = new BigDecimal("100.00");
            BigDecimal valor2 = new BigDecimal("50.00");
            BigDecimal valorEsperado = new BigDecimal("150.00");
            
            // When
            contaService.depositar(conta.getId(), valor1);
            contaService.depositar(conta.getId(), valor2);
            
            // Then
            assertEquals(valorEsperado, conta.getSaldo());
        }
        
        @Test
        @DisplayName("Deve rejeitar depósito com valor negativo")
        void deveRejeitarDepositoComValorNegativo() {
            // Given
            BigDecimal valorNegativo = new BigDecimal("-50.00");
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contaService.depositar(conta.getId(), valorNegativo)
            );
            
            assertTrue(exception.getMessage().contains("positivo"));
        }
        
        @Test
        @DisplayName("Deve rejeitar depósito em conta inexistente")
        void deveRejeitarDepositoEmContaInexistente() {
            // Given
            String idInexistente = "999999";
            BigDecimal valor = new BigDecimal("100.00");
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contaService.depositar(idInexistente, valor)
            );
            
            assertTrue(exception.getMessage().contains("não encontrada"));
        }
    }
    
    @Nested
    @DisplayName("Testes de Saque")
    class TestsSaque {
        
        private Conta conta;
        
        @BeforeEach
        void setUp() {
            conta = contaService.criarConta("João Silva", "11144477735");
            contaService.depositar(conta.getId(), new BigDecimal("1000.00"));
        }
        
        @Test
        @DisplayName("Deve realizar saque com saldo suficiente")
        void deveRealizarSaqueComSaldoSuficiente() {
            // Given
            BigDecimal valorSaque = new BigDecimal("200.00");
            BigDecimal saldoEsperado = new BigDecimal("800.00");
            
            // When
            contaService.sacar(conta.getId(), valorSaque);
            
            // Then
            assertEquals(saldoEsperado, conta.getSaldo());
        }
        
        @Test
        @DisplayName("Deve rejeitar saque com saldo insuficiente")
        void deveRejeitarSaqueComSaldoInsuficiente() {
            // Given
            BigDecimal valorSaque = new BigDecimal("1500.00");
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contaService.sacar(conta.getId(), valorSaque)
            );
            
            assertTrue(exception.getMessage().contains("insuficiente"));
        }
        
        @Test
        @DisplayName("Deve rejeitar saque com valor negativo")
        void deveRejeitarSaqueComValorNegativo() {
            // Given
            BigDecimal valorNegativo = new BigDecimal("-50.00");
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contaService.sacar(conta.getId(), valorNegativo)
            );
            
            assertTrue(exception.getMessage().contains("positivo"));
        }
    }
    
    @Nested
    @DisplayName("Testes de Transferência")
    class TestsTransferencia {
        
        private Conta contaOrigem;
        private Conta contaDestino;
        
        @BeforeEach
        void setUp() {
            contaOrigem = contaService.criarConta("João Silva", "11144477735");
            contaDestino = contaService.criarConta("Maria Santos", "11122233396");
            contaService.depositar(contaOrigem.getId(), new BigDecimal("1000.00"));
        }
        
        @Test
        @DisplayName("Deve realizar transferência com dados válidos")
        void deveRealizarTransferenciaComDadosValidos() {
            // Given
            BigDecimal valor = new BigDecimal("300.00");
            
            // When
            contaService.transferir(contaOrigem.getId(), contaDestino.getId(), valor);
            
            // Then
            assertEquals(new BigDecimal("700.00"), contaOrigem.getSaldo());
            assertEquals(valor, contaDestino.getSaldo());
        }
        
        @Test
        @DisplayName("Deve rejeitar transferência para a mesma conta")
        void deveRejeitarTransferenciaParaMesmaConta() {
            // Given
            BigDecimal valor = new BigDecimal("100.00");
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contaService.transferir(contaOrigem.getId(), contaOrigem.getId(), valor)
            );
            
            assertTrue(exception.getMessage().contains("devem ser diferentes"));
        }
        
        @Test
        @DisplayName("Deve rejeitar transferência com saldo insuficiente")
        void deveRejeitarTransferenciaComSaldoInsuficiente() {
            // Given
            BigDecimal valor = new BigDecimal("1500.00");
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contaService.transferir(contaOrigem.getId(), contaDestino.getId(), valor)
            );
            
            assertTrue(exception.getMessage().contains("insuficiente"));
        }
        
        @Test
        @DisplayName("Deve rejeitar transferência com conta origem inexistente")
        void deveRejeitarTransferenciaComContaOrigemInexistente() {
            // Given
            String idInexistente = "999999";
            BigDecimal valor = new BigDecimal("100.00");
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contaService.transferir(idInexistente, contaDestino.getId(), valor)
            );
            
            assertTrue(exception.getMessage().contains("não encontrada"));
        }
        
        @Test
        @DisplayName("Deve rejeitar transferência com conta destino inexistente")
        void deveRejeitarTransferenciaComContaDestinoInexistente() {
            // Given
            String idInexistente = "999999";
            BigDecimal valor = new BigDecimal("100.00");
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contaService.transferir(contaOrigem.getId(), idInexistente, valor)
            );
            
            assertTrue(exception.getMessage().contains("não encontrada"));
        }
    }
    
    @Nested
    @DisplayName("Testes de Busca e Consulta")
    class TestsBuscaConsulta {
        
        private Conta conta1;
        private Conta conta2;
        
        @BeforeEach
        void setUp() {
            conta1 = contaService.criarConta("João Silva", "11144477735");
            conta2 = contaService.criarConta("Maria Silva", "11122233396");
        }
        
        @Test
        @DisplayName("Deve buscar conta por ID existente")
        void deveBuscarContaPorIdExistente() {
            // When
            Optional<Conta> contaEncontrada = contaService.buscarContaPorId(conta1.getId());
            
            // Then
            assertTrue(contaEncontrada.isPresent());
            assertEquals(conta1.getId(), contaEncontrada.get().getId());
        }
        
        @Test
        @DisplayName("Deve retornar vazio para ID inexistente")
        void deveRetornarVazioParaIdInexistente() {
            // When
            Optional<Conta> contaEncontrada = contaService.buscarContaPorId("999999");
            
            // Then
            assertFalse(contaEncontrada.isPresent());
        }
        
        @Test
        @DisplayName("Deve buscar contas por nome")
        void deveBuscarContasPorNome() {
            // When
            List<Conta> contasEncontradas = contaService.buscarContasPorNome("Silva");
            
            // Then
            assertEquals(2, contasEncontradas.size());
        }
        
        @Test
        @DisplayName("Deve listar todas as contas")
        void deveListarTodasAsContas() {
            // When
            List<Conta> todasContas = contaService.listarTodasContas();
            
            // Then
            assertEquals(2, todasContas.size());
        }
        
        @Test
        @DisplayName("Deve consultar saldo de conta existente")
        void deveConsultarSaldoDeContaExistente() {
            // Given
            BigDecimal valorDeposito = new BigDecimal("500.00");
            contaService.depositar(conta1.getId(), valorDeposito);
            
            // When
            BigDecimal saldo = contaService.consultarSaldo(conta1.getId());
            
            // Then
            assertEquals(valorDeposito, saldo);
        }
        
        @Test
        @DisplayName("Deve rejeitar consulta de saldo para conta inexistente")
        void deveRejeitarConsultaSaldoParaContaInexistente() {
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contaService.consultarSaldo("999999")
            );
            
            assertTrue(exception.getMessage().contains("não encontrada"));
        }
    }
    
    @Nested
    @DisplayName("Testes de Validação de CPF")
    class TestsValidacaoCpf {
        
        @Test
        @DisplayName("Deve aceitar CPF válido")
        void deveAceitarCpfValido() {
            // Given
            String cpfValido = "12345678909"; // CPF válido
            
            // When & Then
            assertDoesNotThrow(() -> {
                contaService.criarConta("Teste", cpfValido);
            });
        }
        
        @Test
        @DisplayName("Deve rejeitar CPF com todos os dígitos iguais")
        void deveRejeitarCpfComTodosDigitosIguais() {
            // Given
            String cpfInvalido = "11111111111";
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contaService.criarConta("Teste", cpfInvalido)
            );
            
            assertTrue(exception.getMessage().contains("CPF"));
        }
        
        @Test
        @DisplayName("Deve rejeitar CPF com menos de 11 dígitos")
        void deveRejeitarCpfComMenosDeOnzeDigitos() {
            // Given
            String cpfInvalido = "123456789";
            
            // When & Then
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> contaService.criarConta("Teste", cpfInvalido)
            );
            
            assertTrue(exception.getMessage().contains("CPF"));
        }
    }
    
    @Test
    @DisplayName("Deve retornar total de contas correto")
    void deveRetornarTotalDeContasCorreto() {
        // Given
        assertEquals(0, contaService.listarTodasContas().size());
        
        // When
        contaService.criarConta("João Silva", "11144477735");
        contaService.criarConta("Maria Santos", "11122233396");
        
        // Then
        assertEquals(2, contaService.listarTodasContas().size());
    }
}