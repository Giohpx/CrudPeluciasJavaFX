package com.template.controller;
import static com.template.util.DialogUtil.showWarning;
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
import java.util.regex.Pattern;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;

import static com.template.util.Clear.limparCamposETabela;

public class MainController implements Initializable {

    @FXML private Label lblContador;
    @FXML private Label lblMensagem;
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

        Platform.runLater(() -> {
            if (txtID.getScene() != null) {
                txtID.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                    if (acaoPendente != AcaoPendente.NENHUM) {
                        if (event.getCode() == KeyCode.ENTER) {
                            confirmarAcao();
                            event.consume();
                        } else if (event.getCode() == KeyCode.ESCAPE) {
                            cancelarAcao();
                            event.consume();
                        }
                    }
                });
            }
        });
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

        acaoPendente = AcaoPendente.ALTERAR;
        if (lblMensagem != null) {
            lblMensagem.setText("Pressione ENTER para confirmar a ALTERAÇÃO ou ESC para cancelar.");
            lblMensagem.setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
        }
    }

    public class PeluciaValidator {
        //validar cadastro
        public static boolean validarPelucia(String nome, String email, String senha, String login){
            if(nome.isEmpty() || email.isEmpty() || senha.isEmpty() || login.isEmpty()){
                showWarning("Preencha todos os campos antes de prosseguir.");
                return false;
            }
            if(!validarEmail(email)){
                showWarning("Digite um e-mail válido (exemplo@dominio.com)!");
                return false;
            }
            return true;
        }

        public static boolean validarEmail(String email) { return Pattern.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$",email); }

        //validar pesquisa
        public static boolean validarTermo(String termo){
            if (termo.isEmpty()) {
                showWarning("Digite um termo de pesquisa.");
                return false;
            }
            return true;
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
            cancelarAcao();
        });

        taskDeletar.setOnFailed(e -> {
            isProcessando.set(false);
            cancelarAcao();
        });

        new Thread(taskDeletar).start();
    }

    private void confirmarAcao() {
        if (acaoPendente == AcaoPendente.ALTERAR) {
            executarAlteracao();
        } else if (acaoPendente == AcaoPendente.DELETAR) {
            executarExclusao();
        }
    }

    private void cancelarAcao() {
        acaoPendente = AcaoPendente.NENHUM;
        if (lblMensagem != null) {
            lblMensagem.setText("");
        }
    }

    @FXML
    private void btnLimparAction(ActionEvent event) {
        cancelarAcao();
        limparCamposETabela(tblUsuario, txtID, txtNome, txtTipo, txtPelagem, txtValor, txtQtde_pelucias);
    }

    @FXML
    private void carregarCampos() {
        cancelarAcao();

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
