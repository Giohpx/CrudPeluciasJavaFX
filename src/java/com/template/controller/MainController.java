package com.template.controller;

import com.template.model.dao.UsuarioDAO;
import com.template.model.dto.UsuarioDTO;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;

public class MainController implements Initializable {

    @FXML private Label lblContador;
    @FXML private Label lblMensagem; // NOVO: Label para mensagens de confirmação
    @FXML private TextField txtID;
    @FXML private TextField txtNome;
    @FXML private TextField txtTipo;
    @FXML private TextField txtPelagem;
    @FXML private TextField txtValor;
    @FXML private TextField txtQtde_pelucias;

    @FXML private TableView<UsuarioDTO> tblUsuario;

    @FXML private TableColumn<UsuarioDTO, Integer> colID;
    @FXML private TableColumn<UsuarioDTO, String> colNome;
    @FXML private TableColumn<UsuarioDTO, String> colTipo;
    @FXML private TableColumn<UsuarioDTO, String> colPelagem;
    @FXML private TableColumn<UsuarioDTO, Double> colValor;
    @FXML private TableColumn<UsuarioDTO, Integer> colQuantidade;

    @FXML private Button btnSalvar;
    @FXML private Button btnAlterar;
    @FXML private Button btnDeletar;

    private BooleanProperty isProcessando = new SimpleBooleanProperty(false);

    // NOVO: Enum e variável para controlar qual ação está aguardando confirmação
    private enum AcaoPendente { NENHUM, ALTERAR, DELETAR }
    private AcaoPendente acaoPendente = AcaoPendente.NENHUM;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colPelagem.setCellValueFactory(new PropertyValueFactory<>("pelagem"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("qtde_pelucias"));

        configurarValidacaoVisual(txtNome);
        configurarValidacaoVisual(txtTipo);
        configurarValidacaoVisual(txtPelagem);
        configurarValidacaoVisual(txtValor);
        configurarValidacaoVisual(txtQtde_pelucias);

        txtID.setEditable(false);
        txtID.setStyle("-fx-background-color: #e0e0e0;");

        txtID.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                txtID.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 3px; -fx-background-color: #ffe6e6;");
            } else {
                txtID.setStyle("-fx-background-color: #e0e0e0;");
            }
        });

        txtNome.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches(".*\\d.*")) {
                txtNome.setText(newValue.replaceAll("\\d", ""));
            }
        });

        txtTipo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches(".*\\d.*")) {
                txtTipo.setText(newValue.replaceAll("\\d", ""));
            }
        });

        txtPelagem.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches(".*\\d.*")) {
                txtPelagem.setText(newValue.replaceAll("\\d", ""));
            }
        });

        txtQtde_pelucias.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtQtde_pelucias.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        txtValor.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                txtValor.setText(oldValue);
            }
        });

        BooleanBinding camposInvalidos = Bindings.createBooleanBinding(() ->
                        txtNome.getText().trim().isEmpty() ||
                                txtTipo.getText().trim().isEmpty() ||
                                txtPelagem.getText().trim().isEmpty() ||
                                txtValor.getText().trim().isEmpty() ||
                                txtQtde_pelucias.getText().trim().isEmpty(),
                txtNome.textProperty(),
                txtTipo.textProperty(),
                txtPelagem.textProperty(),
                txtValor.textProperty(),
                txtQtde_pelucias.textProperty()
        );

        BooleanBinding semId = Bindings.createBooleanBinding(() ->
                        txtID.getText().trim().isEmpty(),
                txtID.textProperty()
        );

        if (btnSalvar != null) {
            btnSalvar.disableProperty().bind(camposInvalidos.or(isProcessando));
        }

        if (btnAlterar != null) {
            btnAlterar.disableProperty().bind(camposInvalidos.or(semId).or(isProcessando));
        }

        if (btnDeletar != null) {
            btnDeletar.disableProperty().bind(semId.or(isProcessando));
        }

        carregarUsuarios();

        // NOVO: Adiciona o listener global para capturar as teclas ENTER e ESC na Scene
        Platform.runLater(() -> {
            if (txtID.getScene() != null) {
                txtID.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (acaoPendente != AcaoPendente.NENHUM) {
                        if (event.getCode() == KeyCode.ENTER) {
                            confirmarAcao();
                            event.consume(); // Impede outros disparos do ENTER
                        } else if (event.getCode() == KeyCode.ESCAPE) {
                            cancelarAcao();
                            event.consume(); // Impede outros disparos do ESC
                        }
                    }
                });
            }
        });
    }
    private void btnCadastrarAction(ActionEvent event){
        if(validarPelucia(txtNome.getText(), txtEmail.getText(), txt)){
            UsuarioDTO novoUsuario
        }
    }

    private void configurarValidacaoVisual(TextField campo) {
        campo.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                campo.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 3px;");
            } else {
                campo.setStyle("");
            }
        });
    }

    @FXML
    private void btnSalvarAction(ActionEvent event) {
        // Se houver uma ação pendente de outro botão, cancela.
        cancelarAcao();

        if (txtNome.getText().isEmpty() || txtValor.getText().isEmpty() || txtQtde_pelucias.getText().isEmpty()) {
            return;
        }

        UsuarioDTO pelucia = new UsuarioDTO();
        pelucia.setNome(txtNome.getText());
        pelucia.setTipo(txtTipo.getText());
        pelucia.setPelagem(txtPelagem.getText());
        pelucia.setValor(Double.parseDouble(txtValor.getText()));
        pelucia.setQtde_pelucias(Integer.parseInt(txtQtde_pelucias.getText()));

        isProcessando.set(true);

        Task<Void> taskSalvar = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                UsuarioDAO dao = new UsuarioDAO();
                dao.inserir(pelucia);
                return null;
            }
        };

        taskSalvar.setOnSucceeded(e -> {
            carregarUsuarios();
            btnLimparAction(null);
            isProcessando.set(false);
        });

        taskSalvar.setOnFailed(e -> {
            isProcessando.set(false);
        });

        new Thread(taskSalvar).start();
    }

    @FXML
    private void btnAlterarAction(ActionEvent event) {
        if (txtID.getText().isEmpty() || txtNome.getText().isEmpty() || txtValor.getText().isEmpty()) {
            return;
        }

        // NOVO: Apenas solicita a confirmação em vez de executar direto
        acaoPendente = AcaoPendente.ALTERAR;
        if (lblMensagem != null) {
            lblMensagem.setText("Pressione ENTER para confirmar a ALTERAÇÃO ou ESC para cancelar.");
            lblMensagem.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;"); // Cor Laranja
        }
    }

    private void executarAlteracao() {
        UsuarioDTO pelucia = new UsuarioDTO();
        pelucia.setId(Integer.parseInt(txtID.getText()));
        pelucia.setNome(txtNome.getText());
        pelucia.setTipo(txtTipo.getText());
        pelucia.setPelagem(txtPelagem.getText());
        pelucia.setValor(Double.parseDouble(txtValor.getText()));
        pelucia.setQtde_pelucias(Integer.parseInt(txtQtde_pelucias.getText()));

        isProcessando.set(true);

        Task<Void> taskAlterar = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                UsuarioDAO dao = new UsuarioDAO();
                dao.atualizar(pelucia);
                return null;
            }
        };

        taskAlterar.setOnSucceeded(e -> {
            carregarUsuarios();
            isProcessando.set(false);
            cancelarAcao();
        });

        taskAlterar.setOnFailed(e -> {
            isProcessando.set(false);
            cancelarAcao();
        });

        new Thread(taskAlterar).start();
    }

    @FXML
    private void bntDeletarAction(ActionEvent event) {
        if (txtID.getText().isEmpty()) {
            return;
        }

        acaoPendente = AcaoPendente.DELETAR;
        if (lblMensagem != null) {
            lblMensagem.setText("Pressione ENTER para confirmar a EXCLUSÃO ou ESC para cancelar.");
            lblMensagem.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
        }
    }

    private void executarExclusao() {
        int id = Integer.parseInt(txtID.getText());

        isProcessando.set(true);

        Task<Void> taskDeletar = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                UsuarioDAO dao = new UsuarioDAO();
                dao.deletar(id);
                return null;
            }
        };

        taskDeletar.setOnSucceeded(e -> {
            carregarUsuarios();
            btnLimparAction(null);
            isProcessando.set(false);
            cancelarAcao(); // Limpa a mensagem após sucesso
        });

        taskDeletar.setOnFailed(e -> {
            isProcessando.set(false);
            cancelarAcao();
        });

        new Thread(taskDeletar).start();
    }

    // NOVO: Lida com a confirmação
    private void confirmarAcao() {
        if (acaoPendente == AcaoPendente.ALTERAR) {
            executarAlteracao();
        } else if (acaoPendente == AcaoPendente.DELETAR) {
            executarExclusao();
        }
    }

    // NOVO: Cancela e limpa os estados de confirmação
    private void cancelarAcao() {
        acaoPendente = AcaoPendente.NENHUM;
        if (lblMensagem != null) {
            lblMensagem.setText("");
        }
    }

    @FXML
    private void btnLimparAction(ActionEvent event) {
        cancelarAcao(); // Limpa mensagens e status caso existam

        txtID.clear();
        txtNome.clear();
        txtTipo.clear();
        txtPelagem.clear();
        txtValor.clear();
        txtQtde_pelucias.clear();

        txtNome.setStyle("");
        txtTipo.setStyle("");
        txtPelagem.setStyle("");
        txtValor.setStyle("");
        txtQtde_pelucias.setStyle("");
        txtID.setStyle("-fx-background-color: #e0e0e0;");
    }

    @FXML
    private void carregarCampos() {
        cancelarAcao(); // Limpa status pendentes ao selecionar outro item

        UsuarioDTO objUsuarioDTO = tblUsuario.getSelectionModel().getSelectedItem();

        if (objUsuarioDTO != null) {

            if (txtID.getText().equals(String.valueOf(objUsuarioDTO.getId()))) {

                btnLimparAction(null);
                tblUsuario.getSelectionModel().clearSelection();

            } else {

                txtID.setText(String.valueOf(objUsuarioDTO.getId()));
                txtNome.setText(objUsuarioDTO.getNome());
                txtTipo.setText(objUsuarioDTO.getTipo());
                txtPelagem.setText(objUsuarioDTO.getPelagem());
                txtValor.setText(String.valueOf(objUsuarioDTO.getValor()));
                txtQtde_pelucias.setText(String.valueOf(objUsuarioDTO.getQtde_pelucias()));

                txtNome.setStyle("");
                txtTipo.setStyle("");
                txtPelagem.setStyle("");
                txtValor.setStyle("");
                txtQtde_pelucias.setStyle("");
            }
        }
    }

    @FXML
    private void carregarUsuarios() {

        UsuarioDAO objUsuarioDAO = new UsuarioDAO();

        ArrayList<UsuarioDTO> listaUsuarios = objUsuarioDAO.listar();

        tblUsuario.setItems(
                FXCollections.observableArrayList(listaUsuarios)
        );

        if (lblContador != null) {
            lblContador.setText("Total de registros: " + listaUsuarios.size());
        }
    }
}