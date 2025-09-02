# Sistema de Gerenciamento de Contas Bancárias 🏦

Um sistema bancário moderno desenvolvido em Java com interface gráfica JavaFX que permite criar contas, realizar depósitos, saques, transferências e listar contas cadastradas.

## 🚀 Funcionalidades

- ✅ **Criar conta** com nome e CPF
- ✅ **Gerar ID aleatório** de 6 dígitos para cada conta no momento da criação
- ✅ **Validar CPF** antes de criação
- ✅ **Realizar depósito** na conta com ID identificador
- ✅ **Realizar saque** com validação de saldo
- ✅ **Transferência entre contas** com validações robustas
- ✅ **Listar contas existentes** em tempo real
- ✅ **Buscar contas** por ID ou nome do cliente
- ✅ **Interface gráfica moderna** com JavaFX
- ✅ **Validações de negócio** robustas
- ✅ **Tratamento de exceções** completo
- ✅ **Arquitetura limpa** seguindo padrões MVC
- ✅ **Testes unitários** com JUnit 5
- ✅ **Validação de CPF** antes de criação
- ✅ **Validação de saldo** antes de saque
- ✅ **Validação de ID** antes de transferência de conta
- ✅ **Validação de ID** antes de busca de conta
- ✅ **Não colocar Setters nos objetos** como: saldo, cpf, ID, nome
- ✅ **Colocar máscara de formatação** nos campos que contêm valores numéricos com o padrão do sistema monetário BR

## 🧠 Tecnologias Utilizadas

- ☕ **Java 21** (ou versão compatível)
- 🎨 **JavaFX 21** para interface gráfica
- 🔄 **Programação Orientada a Objetos** (POO)
- 🧩 **Arquitetura MVC** (Model-View-Controller)
- 📦 **Maven 3.9.9** para gerenciamento de dependências
- 🧪 **JUnit 5** para testes unitários
- 📚 **Boas práticas**: encapsulamento, SRP, coesão

## 📁 Estrutura do Projeto

```
sistema_bancario/
├── src/
│   ├── main/java/
│   │   ├── app/                 # Interface JavaFX e classe Main
│   │   │   └── BankApp.java     # Aplicação principal com GUI
│   │   ├── model/               # Entidades do domínio
│   │   │   ├── Cliente.java     # Classe Cliente
│   │   │   └── Conta.java       # Classe Conta
│   │   ├── service/             # Lógica de negócio
│   │   │   └── ContaService.java # Serviços bancários
│   │   ├── repository/          # Camada de dados
│   │   │   └── ContaRepository.java # Repositório em memória
│   │   └── module-info.java     # Configuração do módulo Java
│   └── test/java/
│       └── ContaServiceTest.java # Testes unitários
├── pom.xml                      # Configuração Maven
└── README.md                    # Este arquivo
```

## 🛠️ Pré-requisitos

- **Java 21** ou superior
- **Maven 3.9.9** ou superior
- **JavaFX 21** (incluído nas dependências)


```

## 🎯 Como Usar

### 1. **Criar Conta**
- Acesse a aba "✅ Criar Conta"
- Preencha o nome completo do cliente
- Digite o CPF (com ou sem formatação)
- Clique em "Criar Conta"
- O sistema gerará automaticamente um ID de 6 dígitos

### 2. **Realizar Depósito**
- Acesse a aba "💰 Operações"
- Na seção "Depósito", digite o ID da conta
- Digite o valor a ser depositado (formato: 1234,56)
- Clique em "Depositar"

### 3. **Realizar Saque**
- Acesse a aba "💰 Operações"
- Na seção "Saque", digite o ID da conta
- Digite o valor a ser sacado (formato: 1234,56)
- Clique em "Sacar"
- O sistema validará se há saldo suficiente

### 4. **Transferir entre Contas**
- Acesse a aba "💰 Operações"
- Na seção "Transferência", digite o ID da conta de origem
- Digite o ID da conta de destino
- Digite o valor a ser transferido (formato: 1234,56)
- Clique em "Transferir"

### 5. **Buscar Contas**
- Acesse a aba "🔍 Consultas"
- **Buscar por ID**: Digite o ID da conta e clique em "Buscar"
- **Buscar por Nome**: Digite o nome (ou parte) do cliente e clique em "Buscar"

### 6. **Listar Todas as Contas**
- Acesse a aba "📋 Contas Cadastradas"
- Visualize todas as contas em uma tabela organizada
- Clique em "🔄 Atualizar Lista" para atualizar os dados

## 🔒 Validações Implementadas

### **Validação de CPF**
- Algoritmo oficial de validação de CPF brasileiro
- Rejeita CPFs com todos os dígitos iguais
- Verifica dígitos verificadores
- Aceita CPF com ou sem formatação

### **Validações de Negócio**
- **Criação de Conta**: Nome obrigatório, CPF válido, CPF único
- **Depósito**: Valor positivo, conta existente
- **Saque**: Valor positivo, conta existente, saldo suficiente
- **Transferência**: Contas diferentes, ambas existentes, saldo suficiente
- **Busca**: IDs válidos, tratamento de casos não encontrados

### **Formatação Monetária**
- Valores exibidos no padrão brasileiro (R$ 1.234,56)
- Máscaras de entrada para facilitar digitação
- Conversão automática de formatos

## 🧪 Testes

O projeto inclui uma suíte completa de testes unitários com JUnit 5:

```bash
# Executar todos os testes
mvn test

# Executar testes com relatório detalhado
mvn test -Dtest=ContaServiceTest
```

### **Cobertura de Testes**
- ✅ Criação de contas (válidas e inválidas)
- ✅ Operações de depósito
- ✅ Operações de saque
- ✅ Transferências entre contas
- ✅ Buscas e consultas
- ✅ Validação de CPF
- ✅ Tratamento de exceções
- ✅ Casos extremos e edge cases

## 🏗️ Arquitetura

### **Padrão MVC**
- **Model**: `Cliente.java`, `Conta.java` - Entidades do domínio
- **View**: `BankApp.java` - Interface gráfica JavaFX
- **Controller**: `ContaService.java` - Lógica de negócio

### **Camadas**
- **Apresentação**: Interface JavaFX com design moderno
- **Serviço**: Lógica de negócio e validações
- **Repositório**: Armazenamento em memória
- **Modelo**: Entidades de domínio

### **Princípios SOLID**
- **SRP**: Cada classe tem uma responsabilidade única
- **OCP**: Extensível sem modificar código existente
- **DIP**: Dependências por abstração (interfaces)
- **Encapsulamento**: Atributos privados, sem setters desnecessários

## 🎨 Interface Gráfica

### **Design Moderno**
- Interface responsiva e intuitiva
- Cores e ícones que facilitam a navegação
- Feedback visual para operações
- Mensagens de erro e sucesso claras

### **Funcionalidades da UI**
- Abas organizadas por funcionalidade
- Máscaras de formatação automática
- Validação em tempo real
- Tabela dinâmica de contas
- Contador de contas em tempo real

## 🔧 Configuração de Desenvolvimento

### **IDE Recomendada**
- IntelliJ IDEA ou Eclipse
- Plugin do JavaFX instalado
- JDK 21 configurado

### **Dependências Maven**
```xml
<!-- JavaFX Controls -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21.0.1</version>
</dependency>

<!-- JUnit 5 -->
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.10.1</version>
    <scope>test</scope>
</dependency>
```

## 📝 Exemplos de Uso

### **CPFs Válidos para Teste**
- `12345678901`
- `11144477735`
- `98765432100`

### **Fluxo Completo de Teste**
1. Criar conta: "João Silva", CPF: "12345678901"
2. Criar conta: "Maria Santos", CPF: "98765432100"
3. Depositar R$ 1.000,00 na conta do João
4. Transferir R$ 300,00 do João para Maria
5. Sacar R$ 200,00 da conta do João
6. Consultar saldos finais

