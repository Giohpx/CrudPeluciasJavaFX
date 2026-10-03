package com.template.controller;

import com.template.model.dao.UsuarioDAO;
import com.template.model.dto.UsuarioDTO;
import com.template.util.DialogUtil;
import com.template.validator.IPeluciaValidador;

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

import static com.template.util.Clear.limparCamposETabela;

public class MainController implements Initializable {

    @FXML
    private Label lblContador;

    @FXML
    private Label lblMensagem;

    @FXML
    private TextField txtID;

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtTipo;

    @FXML
    private TextField txtPelagem;

    @FXML
    private TextField txtValor;

    @FXML
    private TextField txtQtde_pelucias;

    @FXML
    private TableView<UsuarioDTO> tblUsuario;

    @FXML
    private TableColumn<UsuarioDTO, Integer> colID;

    @FXML
    private TableColumn<UsuarioDTO, String> colNome;

    @FXML
    private TableColumn<UsuarioDTO, String> colTipo;

    @FXML
    private TableColumn<UsuarioDTO, String> colPelagem;

    @FXML
    private TableColumn<UsuarioDTO, Double> colValor;

    @FXML
    private TableColumn<UsuarioDTO, Integer> colQuantidade;

    @FXML
    private Button btnSalvar;

    @FXML
    private Button btnAlterar;

    @FXML
    private Button btnDeletar;

    private BooleanProperty isProcessando =
            new SimpleBooleanProperty(false);

    private enum AcaoPendente {
        NENHUM,
        ALTERAR,
        DELETAR
    }

    private AcaoPendente acaoPendente =
            AcaoPendente.NENHUM;

    private final IPeluciaValidador peluciasValidator;

    // Construtor com Injeção de Dependência
    public MainController(IPeluciaValidador peluciasValidator) {
        this.peluciasValidator = peluciasValidator;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colID.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colNome.setCellValueFactory(
                new PropertyValueFactory<>("nome")
        );

        colTipo.setCellValueFactory(
                new PropertyValueFactory<>("tipo")
        );

        colPelagem.setCellValueFactory(
                new PropertyValueFactory<>("pelagem")
        );

        colValor.setCellValueFactory(
                new PropertyValueFactory<>("valor")
        );

        colQuantidade.setCellValueFactory(
                new PropertyValueFactory<>("qtde_pelucias")
        );

        configurarValidacaoVisual(txtNome, "Nome");
        configurarValidacaoVisual(txtTipo, "Tipo");
        configurarValidacaoVisual(txtPelagem, "Pelagem");
        configurarValidacaoVisual(txtValor, "Valor");
        configurarValidacaoVisual(txtQtde_pelucias, "Quantidade");

        txtID.setEditable(false);
        txtID.setStyle(
                "-fx-background-color: #e0e0e0;"
        );

        txtID.focusedProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (newValue) {

                        txtID.setStyle(
                                "-fx-border-color: red;" +
                                        "-fx-border-width: 2px;" +
                                        "-fx-border-radius: 3px;" +
                                        "-fx-background-color: #ffe6e6;"
                        );

                        DialogUtil.showWarning(
                                lblMensagem,
                                "Não é permitido adicionar ou alterar o ID."
                        );

                    } else {

                        txtID.setStyle(
                                "-fx-background-color: #e0e0e0;"
                        );
                    }
                }
        );

        configurarCampoTextoSemNumeros(txtNome, "Nome");
        configurarCampoTextoSemNumeros(txtTipo, "Tipo");
        configurarCampoTextoSemNumeros(txtPelagem, "Pelagem");
        configurarCampoQuantidade();
        configurarCampoValor();

        BooleanBinding camposInvalidos =
                Bindings.createBooleanBinding(
                        () ->
                                txtNome.getText().trim().isEmpty()
                                        || txtTipo.getText().trim().isEmpty()
                                        || txtPelagem.getText().trim().isEmpty()
                                        || txtValor.getText().trim().isEmpty()
                                        || txtQtde_pelucias.getText().trim().isEmpty(),

                        txtNome.textProperty(),
                        txtTipo.textProperty(),
                        txtPelagem.textProperty(),
                        txtValor.textProperty(),
                        txtQtde_pelucias.textProperty()
                );

        BooleanBinding semId =
                Bindings.createBooleanBinding(
                        () ->
                                txtID.getText().trim().isEmpty(),

                        txtID.textProperty()
                );

        if (btnSalvar != null) {

            btnSalvar.disableProperty().bind(
                    camposInvalidos.or(isProcessando)
            );
        }

        if (btnAlterar != null) {

            btnAlterar.disableProperty().bind(
                    camposInvalidos
                            .or(semId)
                            .or(isProcessando)
            );
        }

        if (btnDeletar != null) {

            btnDeletar.disableProperty().bind(
                    semId.or(isProcessando)
            );
        }

        carregarUsuarios();

        Platform.runLater(() -> {

            if (txtID.getScene() != null) {

                txtID.getScene().addEventFilter(
                        KeyEvent.KEY_PRESSED,
                        event -> {

                            if (acaoPendente != AcaoPendente.NENHUM) {

                                if (event.getCode() == KeyCode.ENTER) {

                                    confirmarAcao();
                                    event.consume();

                                } else if (
                                        event.getCode() == KeyCode.ESCAPE
                                ) {

                                    cancelarAcao();
                                    event.consume();
                                }
                            }
                        }
                );
            }
        });
    }

    private void configurarValidacaoVisual(
            TextField campo,
            String nomeCampo) {

        campo.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (newValue == null
                            || newValue.trim().isEmpty()) {

                        campo.setStyle(
                                "-fx-border-color: red;" +
                                        "-fx-border-width: 2px;" +
                                        "-fx-border-radius: 3px;"
                        );

                        DialogUtil.showWarning(
                                lblMensagem,
                                "O campo " + nomeCampo
                                        + " deve ser preenchido."
                        );

                    } else {

                        campo.setStyle("");
                    }
                }
        );
    }

    private void configurarCampoTextoSemNumeros(
            TextField campo,
            String nomeCampo) {

        campo.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (newValue.matches(".*\\d.*")) {

                        DialogUtil.showWarning(
                                lblMensagem,
                                "Não adicione números no campo "
                                        + nomeCampo + "."
                        );

                        campo.setText(
                                newValue.replaceAll("\\d", "")
                        );
                    }
                }
        );
    }

    private void configurarCampoQuantidade() {

        txtQtde_pelucias.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (!newValue.matches("\\d*")) {

                        DialogUtil.showWarning(
                                lblMensagem,
                                "Use apenas números inteiros no campo Quantidade."
                        );

                        txtQtde_pelucias.setText(
                                newValue.replaceAll("[^\\d]", "")
                        );
                    }
                }
        );
    }

    private void configurarCampoValor() {

        txtValor.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    if (newValue.contains(",")) {

                        DialogUtil.showWarning(
                                lblMensagem,
                                "Não adicione vírgula no campo Valor. "
                                        + "Use ponto. Ex: 10.50"
                        );

                    } else if (!newValue.matches("\\d*(\\.\\d*)?")) {

                        DialogUtil.showWarning(
                                lblMensagem,
                                "Use apenas números no campo Valor. "
                                        + "Para centavos, use ponto. Ex: 10.50"
                        );
                    }

                    if (!newValue.matches("\\d*(\\.\\d*)?")) {

                        txtValor.setText(oldValue);
                    }
                }
        );
    }

    @FXML
    private void btnSalvarAction(ActionEvent event) {

        cancelarAcao();

        if (!peluciasValidator.validarPelucia(
                txtNome.getText(),
                txtTipo.getText(),
                txtPelagem.getText(),
                txtValor.getText(),
                txtQtde_pelucias.getText())) {

            DialogUtil.showWarning(
                    lblMensagem,
                    peluciasValidator.getMensagemErro()
            );

            DialogUtil.showWarning(
                    peluciasValidator.getMensagemErro()
            );

            return;
        }

        UsuarioDTO pelucia = new UsuarioDTO();

        pelucia.setNome(txtNome.getText());
        pelucia.setTipo(txtTipo.getText());
        pelucia.setPelagem(txtPelagem.getText());

        pelucia.setValor(
                Double.parseDouble(
                        txtValor.getText()
                )
        );

        pelucia.setQtde_pelucias(
                Integer.parseInt(
                        txtQtde_pelucias.getText()
                )
        );

        isProcessando.set(true);

        Task<Void> taskSalvar =
                new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {

                        UsuarioDAO dao =
                                new UsuarioDAO();

                        dao.inserir(pelucia);

                        return null;
                    }
                };

        taskSalvar.setOnSucceeded(e -> {

            carregarUsuarios();

            btnLimparAction(null);

            DialogUtil.showInfo(
                    lblMensagem,
                    "Pelúcia adicionada com sucesso!"
            );

            isProcessando.set(false);
        });

        taskSalvar.setOnFailed(e -> {

            DialogUtil.showError(
                    lblMensagem,
                    "Não foi possível adicionar a pelúcia."
            );

            isProcessando.set(false);
        });

        new Thread(taskSalvar).start();
    }

    @FXML
    private void btnAlterarAction(ActionEvent event) {

        if (txtID.getText().isEmpty()) {

            DialogUtil.showWarning(
                    lblMensagem,
                    "Selecione um registro para alterar."
            );

            return;
        }

        if (!peluciasValidator.validarPelucia(
                txtNome.getText(),
                txtTipo.getText(),
                txtPelagem.getText(),
                txtValor.getText(),
                txtQtde_pelucias.getText())) {

            DialogUtil.showWarning(
                    lblMensagem,
                    peluciasValidator.getMensagemErro()
            );

            DialogUtil.showWarning(
                    peluciasValidator.getMensagemErro()
            );

            return;
        }

        acaoPendente =
                AcaoPendente.ALTERAR;

        if (lblMensagem != null) {

            lblMensagem.setText(
                    "Pressione ENTER para confirmar a ALTERAÇÃO "
                            + "ou ESC para cancelar."
            );

            lblMensagem.setStyle(
                    "-fx-text-fill: #e67e22;" +
                            "-fx-font-weight: bold;"
            );
        }
    }

    private void executarAlteracao() {

        UsuarioDTO pelucia =
                new UsuarioDTO();

        pelucia.setId(
                Integer.parseInt(
                        txtID.getText()
                )
        );

        pelucia.setNome(
                txtNome.getText()
        );

        pelucia.setTipo(
                txtTipo.getText()
        );

        pelucia.setPelagem(
                txtPelagem.getText()
        );

        pelucia.setValor(
                Double.parseDouble(
                        txtValor.getText()
                )
        );

        pelucia.setQtde_pelucias(
                Integer.parseInt(
                        txtQtde_pelucias.getText()
                )
        );

        isProcessando.set(true);

        Task<Void> taskAlterar =
                new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {

                        UsuarioDAO dao =
                                new UsuarioDAO();

                        dao.atualizar(pelucia);

                        return null;
                    }
                };

        taskAlterar.setOnSucceeded(e -> {

            carregarUsuarios();

            isProcessando.set(false);

            cancelarAcao();

            DialogUtil.showInfo(
                    lblMensagem,
                    "Pelúcia alterada com sucesso!"
            );
        });

        taskAlterar.setOnFailed(e -> {

            isProcessando.set(false);

            cancelarAcao();

            DialogUtil.showError(
                    lblMensagem,
                    "Não foi possível alterar a pelúcia."
            );
        });

        new Thread(taskAlterar).start();
    }

    @FXML
    private void bntDeletarAction(ActionEvent event) {

        if (txtID.getText().isEmpty()) {

            return;
        }

        acaoPendente =
                AcaoPendente.DELETAR;

        if (lblMensagem != null) {

            lblMensagem.setText(
                    "Pressione ENTER para confirmar a EXCLUSÃO "
                            + "ou ESC para cancelar."
            );

            lblMensagem.setStyle(
                    "-fx-text-fill: #e74c3c;" +
                            "-fx-font-weight: bold;"
            );
        }
    }

    private void executarExclusao() {

        int id =
                Integer.parseInt(
                        txtID.getText()
                );

        isProcessando.set(true);

        Task<Void> taskDeletar =
                new Task<Void>() {

                    @Override
                    protected Void call() throws Exception {

                        UsuarioDAO dao =
                                new UsuarioDAO();

                        dao.deletar(id);

                        return null;
                    }
                };

        taskDeletar.setOnSucceeded(e -> {

            carregarUsuarios();

            btnLimparAction(null);

            isProcessando.set(false);

            cancelarAcao();

            DialogUtil.showInfo(
                    lblMensagem,
                    "Pelúcia excluída com sucesso!"
            );
        });

        taskDeletar.setOnFailed(e -> {

            isProcessando.set(false);

            cancelarAcao();

            DialogUtil.showError(
                    lblMensagem,
                    "Não foi possível excluir a pelúcia."
            );
        });

        new Thread(taskDeletar).start();
    }

    private void confirmarAcao() {

        if (acaoPendente == AcaoPendente.ALTERAR) {

            executarAlteracao();

        } else if (
                acaoPendente == AcaoPendente.DELETAR
        ) {

            executarExclusao();
        }
    }

    private void cancelarAcao() {

        acaoPendente =
                AcaoPendente.NENHUM;

        DialogUtil.clearMessage(lblMensagem);
    }

    @FXML
    private void btnLimparAction(ActionEvent event) {

        cancelarAcao();

        limparCamposETabela(
                tblUsuario,
                txtID,
                txtNome,
                txtTipo,
                txtPelagem,
                txtValor,
                txtQtde_pelucias
        );

        DialogUtil.clearMessage(lblMensagem);
    }

    @FXML
    private void carregarCampos() {

        cancelarAcao();

        UsuarioDTO objUsuarioDTO =
                tblUsuario.getSelectionModel()
                        .getSelectedItem();

        if (objUsuarioDTO != null) {

            if (txtID.getText().equals(
                    String.valueOf(
                            objUsuarioDTO.getId()
                    )
            )) {

                btnLimparAction(null);

                tblUsuario.getSelectionModel()
                        .clearSelection();

            } else {

                txtID.setText(
                        String.valueOf(
                                objUsuarioDTO.getId()
                        )
                );

                txtNome.setText(
                        objUsuarioDTO.getNome()
                );

                txtTipo.setText(
                        objUsuarioDTO.getTipo()
                );

                txtPelagem.setText(
                        objUsuarioDTO.getPelagem()
                );

                txtValor.setText(
                        String.valueOf(
                                objUsuarioDTO.getValor()
                        )
                );

                txtQtde_pelucias.setText(
                        String.valueOf(
                                objUsuarioDTO.getQtde_pelucias()
                        )
                );

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

        UsuarioDAO objUsuarioDAO =
                new UsuarioDAO();

        ArrayList<UsuarioDTO> listaUsuarios =
                objUsuarioDAO.listar();

        tblUsuario.setItems(
                FXCollections.observableArrayList(
                        listaUsuarios
                )
        );

        if (lblContador != null) {

            lblContador.setText(
                    "Total de registros: "
                            + listaUsuarios.size()
            );
        }
    }
}
