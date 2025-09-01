package app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Cliente;
import model.Conta;
import repository.ContaRepository;
import service.ContaService;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Aplicação principal do Sistema Bancário com interface gráfica JavaFX.
 * Implementa todas as funcionalidades bancárias com design moderno e UX otimizada.
 */
public class BankApp extends Application {
    private ContaService contaService;
    private TableView<Conta> tabelaContas;
    private Label labelTotalContas;
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    @Override
    public void start(Stage primaryStage) {
        // Inicializa os serviços
        ContaRepository repository = new ContaRepository();
        contaService = new ContaService(repository);

        // Configura a janela principal
        primaryStage.setTitle("Sistema Bancário - Gerenciamento de Contas");
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);

        // Cria o layout principal
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Header
        VBox header = criarHeader();
        root.setTop(header);

        // Conteúdo principal
        TabPane tabPane = criarTabPane();
        root.setCenter(tabPane);

        // Footer
        HBox footer = criarFooter();
        root.setBottom(footer);

        // Cria e exibe a cena
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Atualiza a tabela inicial
        atualizarTabelaContas();
    }

    private VBox criarHeader() {
        VBox header = new VBox();
        header.setStyle("-fx-background-color: #2c3e50; -fx-padding: 20;");
        header.setAlignment(Pos.CENTER);

        Label titulo = new Label("🏦 Sistema Bancário");
        titulo.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label subtitulo = new Label("Gerenciamento de Contas Bancárias");
        subtitulo.setStyle("-fx-font-size: 14px; -fx-text-fill: #bdc3c7;");

        header.getChildren().addAll(titulo, subtitulo);
        return header;
    }

    private TabPane criarTabPane() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Aba de criação de conta
        Tab tabCriarConta = new Tab("✅ Criar Conta");
        tabCriarConta.setContent(criarPainelCriarConta());

        // Aba de operações
        Tab tabOperacoes = new Tab("💰 Operações");
        tabOperacoes.setContent(criarPainelOperacoes());

        // Aba de consultas
        Tab tabConsultas = new Tab("🔍 Consultas");
        tabConsultas.setContent(criarPainelConsultas());

        // Aba de listagem
        Tab tabListagem = new Tab("📋 Contas Cadastradas");
        tabListagem.setContent(criarPainelListagem());

        tabPane.getTabs().addAll(tabCriarConta, tabOperacoes, tabConsultas, tabListagem);
        return tabPane;
    }

    private ScrollPane criarPainelCriarConta() {
        VBox painel = new VBox(15);
        painel.setPadding(new Insets(20));
        painel.setStyle("-fx-background-color: white;");

        Label titulo = new Label("Criar Nova Conta");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Formulário
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(15);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 10;");

        Label lblNome = new Label("Nome Completo:");
        lblNome.setStyle("-fx-font-weight: bold;");
        TextField txtNome = new TextField();
        txtNome.setPromptText("Digite o nome completo do cliente");
        txtNome.setPrefWidth(300);

        Label lblCpf = new Label("CPF:");
        lblCpf.setStyle("-fx-font-weight: bold;");
        TextField txtCpf = new TextField();
        txtCpf.setPromptText("000.000.000-00");
        txtCpf.setPrefWidth(200);
        aplicarMascaraCPF(txtCpf);

        Button btnCriar = new Button("Criar Conta");
        btnCriar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        btnCriar.setPrefWidth(150);

        form.add(lblNome, 0, 0);
        form.add(txtNome, 1, 0);
        form.add(lblCpf, 0, 1);
        form.add(txtCpf, 1, 1);
        form.add(btnCriar, 1, 2);

        // Área de resultado
        TextArea areaResultado = new TextArea();
        areaResultado.setEditable(false);
        areaResultado.setPrefRowCount(8);
        areaResultado.setStyle("-fx-background-color: #f8f9fa;");

        btnCriar.setOnAction(e -> {
            try {
                String nome = txtNome.getText().trim();
                String cpf = txtCpf.getText().trim();

                if (nome.isEmpty() || cpf.isEmpty()) {
                    mostrarErro(areaResultado, "Todos os campos são obrigatórios!");
                    return;
                }

                Conta conta = contaService.criarConta(nome, cpf);
                
                String resultado = "✅ CONTA CRIADA COM SUCESSO!\n\n" +
                        "ID da Conta: " + conta.getId() + "\n" +
                        "Cliente: " + conta.getCliente().getNome() + "\n" +
                        "CPF: " + conta.getCliente().getCpfFormatado() + "\n" +
                        "Saldo Inicial: " + conta.getSaldoFormatado();
                
                areaResultado.setText(resultado);
                areaResultado.setStyle("-fx-text-fill: #27ae60; -fx-background-color: #d5f4e6;");
                
                // Limpa os campos
                txtNome.clear();
                txtCpf.clear();
                
                // Atualiza a tabela
                atualizarTabelaContas();
                
            } catch (Exception ex) {
                mostrarErro(areaResultado, ex.getMessage());
            }
        });

        painel.getChildren().addAll(titulo, form, new Label("Resultado:"), areaResultado);
        
        ScrollPane scroll = new ScrollPane(painel);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private ScrollPane criarPainelOperacoes() {
        VBox painel = new VBox(20);
        painel.setPadding(new Insets(20));
        painel.setStyle("-fx-background-color: white;");

        Label titulo = new Label("Operações Bancárias");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Painel de Depósito
        VBox painelDeposito = criarPainelDeposito();
        
        // Painel de Saque
        VBox painelSaque = criarPainelSaque();
        
        // Painel de Transferência
        VBox painelTransferencia = criarPainelTransferencia();

        painel.getChildren().addAll(titulo, painelDeposito, painelSaque, painelTransferencia);
        
        ScrollPane scroll = new ScrollPane(painel);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private VBox criarPainelDeposito() {
        VBox painel = new VBox(10);
        painel.setStyle("-fx-background-color: #e8f5e8; -fx-padding: 15; -fx-background-radius: 10;");

        Label titulo = new Label("💰 Depósito");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        HBox form = new HBox(10);
        form.setAlignment(Pos.CENTER_LEFT);

        TextField txtIdDeposito = new TextField();
        txtIdDeposito.setPromptText("ID da Conta");
        txtIdDeposito.setPrefWidth(120);

        TextField txtValorDeposito = new TextField();
        txtValorDeposito.setPromptText("Valor (R$)");
        txtValorDeposito.setPrefWidth(150);
        aplicarMascaraMoeda(txtValorDeposito);

        Button btnDepositar = new Button("Depositar");
        btnDepositar.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

        Label lblResultadoDeposito = new Label();
        lblResultadoDeposito.setWrapText(true);

        btnDepositar.setOnAction(e -> {
            try {
                String id = txtIdDeposito.getText().trim();
                String valorStr = txtValorDeposito.getText().trim();

                if (id.isEmpty() || valorStr.isEmpty()) {
                    mostrarErroLabel(lblResultadoDeposito, "Todos os campos são obrigatórios!");
                    return;
                }

                BigDecimal valor = parseMoeda(valorStr);
                contaService.depositar(id, valor);
                
                Optional<Conta> conta = contaService.buscarContaPorId(id);
                String resultado = "✅ Depósito realizado! Novo saldo: " + conta.get().getSaldoFormatado();
                
                lblResultadoDeposito.setText(resultado);
                lblResultadoDeposito.setStyle("-fx-text-fill: #27ae60;");
                
                txtIdDeposito.clear();
                txtValorDeposito.clear();
                atualizarTabelaContas();
                
            } catch (Exception ex) {
                mostrarErroLabel(lblResultadoDeposito, ex.getMessage());
            }
        });

        form.getChildren().addAll(new Label("ID:"), txtIdDeposito, new Label("Valor:"), txtValorDeposito, btnDepositar);
        painel.getChildren().addAll(titulo, form, lblResultadoDeposito);
        
        return painel;
    }

    private VBox criarPainelSaque() {
        VBox painel = new VBox(10);
        painel.setStyle("-fx-background-color: #ffeaa7; -fx-padding: 15; -fx-background-radius: 10;");

        Label titulo = new Label("💸 Saque");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #d63031;");

        HBox form = new HBox(10);
        form.setAlignment(Pos.CENTER_LEFT);

        TextField txtIdSaque = new TextField();
        txtIdSaque.setPromptText("ID da Conta");
        txtIdSaque.setPrefWidth(120);

        TextField txtValorSaque = new TextField();
        txtValorSaque.setPromptText("Valor (R$)");
        txtValorSaque.setPrefWidth(150);
        aplicarMascaraMoeda(txtValorSaque);

        Button btnSacar = new Button("Sacar");
        btnSacar.setStyle("-fx-background-color: #d63031; -fx-text-fill: white; -fx-font-weight: bold;");

        Label lblResultadoSaque = new Label();
        lblResultadoSaque.setWrapText(true);

        btnSacar.setOnAction(e -> {
            try {
                String id = txtIdSaque.getText().trim();
                String valorStr = txtValorSaque.getText().trim();

                if (id.isEmpty() || valorStr.isEmpty()) {
                    mostrarErroLabel(lblResultadoSaque, "Todos os campos são obrigatórios!");
                    return;
                }

                BigDecimal valor = parseMoeda(valorStr);
                contaService.sacar(id, valor);
                
                Optional<Conta> conta = contaService.buscarContaPorId(id);
                String resultado = "✅ Saque realizado! Novo saldo: " + conta.get().getSaldoFormatado();
                
                lblResultadoSaque.setText(resultado);
                lblResultadoSaque.setStyle("-fx-text-fill: #27ae60;");
                
                txtIdSaque.clear();
                txtValorSaque.clear();
                atualizarTabelaContas();
                
            } catch (Exception ex) {
                mostrarErroLabel(lblResultadoSaque, ex.getMessage());
            }
        });

        form.getChildren().addAll(new Label("ID:"), txtIdSaque, new Label("Valor:"), txtValorSaque, btnSacar);
        painel.getChildren().addAll(titulo, form, lblResultadoSaque);
        
        return painel;
    }

    private VBox criarPainelTransferencia() {
        VBox painel = new VBox(10);
        painel.setStyle("-fx-background-color: #ddd6fe; -fx-padding: 15; -fx-background-radius: 10;");

        Label titulo = new Label("🔄 Transferência");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #6c5ce7;");

        HBox form = new HBox(10);
        form.setAlignment(Pos.CENTER_LEFT);

        TextField txtIdOrigemTransf = new TextField();
        txtIdOrigemTransf.setPromptText("ID Origem");
        txtIdOrigemTransf.setPrefWidth(100);

        TextField txtIdDestinoTransf = new TextField();
        txtIdDestinoTransf.setPromptText("ID Destino");
        txtIdDestinoTransf.setPrefWidth(100);

        TextField txtValorTransf = new TextField();
        txtValorTransf.setPromptText("Valor (R$)");
        txtValorTransf.setPrefWidth(150);
        aplicarMascaraMoeda(txtValorTransf);

        Button btnTransferir = new Button("Transferir");
        btnTransferir.setStyle("-fx-background-color: #6c5ce7; -fx-text-fill: white; -fx-font-weight: bold;");

        Label lblResultadoTransf = new Label();
        lblResultadoTransf.setWrapText(true);

        btnTransferir.setOnAction(e -> {
            try {
                String idOrigem = txtIdOrigemTransf.getText().trim();
                String idDestino = txtIdDestinoTransf.getText().trim();
                String valorStr = txtValorTransf.getText().trim();

                if (idOrigem.isEmpty() || idDestino.isEmpty() || valorStr.isEmpty()) {
                    mostrarErroLabel(lblResultadoTransf, "Todos os campos são obrigatórios!");
                    return;
                }

                BigDecimal valor = parseMoeda(valorStr);
                contaService.transferir(idOrigem, idDestino, valor);
                
                String resultado = "✅ Transferência realizada com sucesso!";
                
                lblResultadoTransf.setText(resultado);
                lblResultadoTransf.setStyle("-fx-text-fill: #27ae60;");
                
                txtIdOrigemTransf.clear();
                txtIdDestinoTransf.clear();
                txtValorTransf.clear();
                atualizarTabelaContas();
                
            } catch (Exception ex) {
                mostrarErroLabel(lblResultadoTransf, ex.getMessage());
            }
        });

        form.getChildren().addAll(
            new Label("Origem:"), txtIdOrigemTransf,
            new Label("Destino:"), txtIdDestinoTransf,
            new Label("Valor:"), txtValorTransf,
            btnTransferir
        );
        painel.getChildren().addAll(titulo, form, lblResultadoTransf);
        
        return painel;
    }

    private ScrollPane criarPainelConsultas() {
        VBox painel = new VBox(15);
        painel.setPadding(new Insets(20));
        painel.setStyle("-fx-background-color: white;");

        Label titulo = new Label("Consultas e Buscas");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Busca por ID
        VBox painelBuscaId = new VBox(10);
        painelBuscaId.setStyle("-fx-background-color: #e3f2fd; -fx-padding: 15; -fx-background-radius: 10;");
        
        Label tituloBuscaId = new Label("🔍 Buscar por ID");
        tituloBuscaId.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1976d2;");
        
        HBox formBuscaId = new HBox(10);
        formBuscaId.setAlignment(Pos.CENTER_LEFT);
        
        TextField txtBuscaId = new TextField();
        txtBuscaId.setPromptText("Digite o ID da conta");
        txtBuscaId.setPrefWidth(200);
        
        Button btnBuscarId = new Button("Buscar");
        btnBuscarId.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-weight: bold;");
        
        TextArea areaResultadoBuscaId = new TextArea();
        areaResultadoBuscaId.setEditable(false);
        areaResultadoBuscaId.setPrefRowCount(4);
        areaResultadoBuscaId.setStyle("-fx-background-color: #f8f9fa;");
        
        btnBuscarId.setOnAction(e -> {
            String id = txtBuscaId.getText().trim();
            if (id.isEmpty()) {
                mostrarErro(areaResultadoBuscaId, "Digite um ID para buscar!");
                return;
            }
            
            Optional<Conta> conta = contaService.buscarContaPorId(id);
            if (conta.isPresent()) {
                Conta c = conta.get();
                String resultado = "✅ CONTA ENCONTRADA:\n\n" +
                        "ID: " + c.getId() + "\n" +
                        "Cliente: " + c.getCliente().getNome() + "\n" +
                        "CPF: " + c.getCliente().getCpfFormatado() + "\n" +
                        "Saldo: " + c.getSaldoFormatado();
                
                areaResultadoBuscaId.setText(resultado);
                areaResultadoBuscaId.setStyle("-fx-text-fill: #27ae60; -fx-background-color: #d5f4e6;");
            } else {
                mostrarErro(areaResultadoBuscaId, "Conta não encontrada com ID: " + id);
            }
        });
        
        formBuscaId.getChildren().addAll(txtBuscaId, btnBuscarId);
        painelBuscaId.getChildren().addAll(tituloBuscaId, formBuscaId, areaResultadoBuscaId);

        // Busca por Nome
        VBox painelBuscaNome = new VBox(10);
        painelBuscaNome.setStyle("-fx-background-color: #f3e5f5; -fx-padding: 15; -fx-background-radius: 10;");
        
        Label tituloBuscaNome = new Label("👤 Buscar por Nome");
        tituloBuscaNome.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #7b1fa2;");
        
        HBox formBuscaNome = new HBox(10);
        formBuscaNome.setAlignment(Pos.CENTER_LEFT);
        
        TextField txtBuscaNome = new TextField();
        txtBuscaNome.setPromptText("Digite o nome do cliente");
        txtBuscaNome.setPrefWidth(200);
        
        Button btnBuscarNome = new Button("Buscar");
        btnBuscarNome.setStyle("-fx-background-color: #7b1fa2; -fx-text-fill: white; -fx-font-weight: bold;");
        
        TextArea areaResultadoBuscaNome = new TextArea();
        areaResultadoBuscaNome.setEditable(false);
        areaResultadoBuscaNome.setPrefRowCount(6);
        areaResultadoBuscaNome.setStyle("-fx-background-color: #f8f9fa;");
        
        btnBuscarNome.setOnAction(e -> {
            String nome = txtBuscaNome.getText().trim();
            if (nome.isEmpty()) {
                mostrarErro(areaResultadoBuscaNome, "Digite um nome para buscar!");
                return;
            }
            
            List<Conta> contas = contaService.buscarContasPorNome(nome);
            if (!contas.isEmpty()) {
                StringBuilder resultado = new StringBuilder("✅ CONTAS ENCONTRADAS (" + contas.size() + "):\n\n");
                
                for (Conta c : contas) {
                    resultado.append("ID: ").append(c.getId())
                            .append(" | Cliente: ").append(c.getCliente().getNome())
                            .append(" | Saldo: ").append(c.getSaldoFormatado())
                            .append("\n");
                }
                
                areaResultadoBuscaNome.setText(resultado.toString());
                areaResultadoBuscaNome.setStyle("-fx-text-fill: #27ae60; -fx-background-color: #d5f4e6;");
            } else {
                mostrarErro(areaResultadoBuscaNome, "Nenhuma conta encontrada para o nome: " + nome);
            }
        });
        
        formBuscaNome.getChildren().addAll(txtBuscaNome, btnBuscarNome);
        painelBuscaNome.getChildren().addAll(tituloBuscaNome, formBuscaNome, areaResultadoBuscaNome);

        painel.getChildren().addAll(titulo, painelBuscaId, painelBuscaNome);
        
        ScrollPane scroll = new ScrollPane(painel);
        scroll.setFitToWidth(true);
        return scroll;
    }

    private VBox criarPainelListagem() {
        VBox painel = new VBox(15);
        painel.setPadding(new Insets(20));
        painel.setStyle("-fx-background-color: white;");

        Label titulo = new Label("Contas Cadastradas");
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        // Botão de atualizar
        Button btnAtualizar = new Button("🔄 Atualizar Lista");
        btnAtualizar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAtualizar.setOnAction(e -> atualizarTabelaContas());

        // Tabela de contas
        tabelaContas = new TableView<>();
        tabelaContas.setStyle("-fx-background-color: #f8f9fa;");

        TableColumn<Conta, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(80);

        TableColumn<Conta, String> colNome = new TableColumn<>("Cliente");
        colNome.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCliente().getNome()));
        colNome.setPrefWidth(200);

        TableColumn<Conta, String> colCpf = new TableColumn<>("CPF");
        colCpf.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCliente().getCpfFormatado()));
        colCpf.setPrefWidth(150);

        TableColumn<Conta, String> colSaldo = new TableColumn<>("Saldo");
        colSaldo.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSaldoFormatado()));
        colSaldo.setPrefWidth(120);

        tabelaContas.getColumns().addAll(colId, colNome, colCpf, colSaldo);

        painel.getChildren().addAll(titulo, btnAtualizar, tabelaContas);
        return painel;
    }

    private HBox criarFooter() {
        HBox footer = new HBox();
        footer.setStyle("-fx-background-color: #34495e; -fx-padding: 10;");
        footer.setAlignment(Pos.CENTER);

        labelTotalContas = new Label("Total de contas: 0");
        labelTotalContas.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        footer.getChildren().add(labelTotalContas);
        return footer;
    }

    private void atualizarTabelaContas() {
        List<Conta> contas = contaService.listarTodasContas();
        tabelaContas.getItems().clear();
        tabelaContas.getItems().addAll(contas);
        labelTotalContas.setText("Total de contas: " + contas.size());
    }

    private void aplicarMascaraCPF(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                String digits = newValue.replaceAll("[^0-9]", "");
                if (digits.length() > 11) {
                    digits = digits.substring(0, 11);
                }
                
                StringBuilder formatted = new StringBuilder();
                for (int i = 0; i < digits.length(); i++) {
                    if (i == 3 || i == 6) {
                        formatted.append(".");
                    } else if (i == 9) {
                        formatted.append("-");
                    }
                    formatted.append(digits.charAt(i));
                }
                
                if (!formatted.toString().equals(newValue)) {
                    textField.setText(formatted.toString());
                }
            }
        });
    }

    private void aplicarMascaraMoeda(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                String digits = newValue.replaceAll("[^0-9]", "");
                if (!digits.isEmpty()) {
                    try {
                        double value = Double.parseDouble(digits) / 100.0;
                        String formatted = String.format("%.2f", value).replace(".", ",");
                        if (!formatted.equals(newValue)) {
                            textField.setText(formatted);
                        }
                    } catch (NumberFormatException e) {
                        textField.setText(oldValue);
                    }
                }
            }
        });
    }

    private BigDecimal parseMoeda(String valor) throws ParseException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ParseException("Valor não pode ser vazio", 0);
        }
        
        // Remove formatação e converte
        String valorLimpo = valor.replace(",", ".").replaceAll("[^0-9.]", "");
        try {
            return new BigDecimal(valorLimpo);
        } catch (NumberFormatException e) {
            throw new ParseException("Formato de valor inválido", 0);
        }
    }

    private void mostrarErro(TextArea area, String mensagem) {
        area.setText("❌ ERRO: " + mensagem);
        area.setStyle("-fx-text-fill: #d63031; -fx-background-color: #ffeaa7;");
    }

    private void mostrarErroLabel(Label label, String mensagem) {
        label.setText("❌ " + mensagem);
        label.setStyle("-fx-text-fill: #d63031;");
    }

    public static void main(String[] args) {
        launch(args);
    }
}