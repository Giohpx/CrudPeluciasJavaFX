package com.template;

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
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;

public class MainController implements Initializable {

    @FXML private Label lblContador;
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

    @FXML private Button btnAlterar;
    @FXML private Button btnDeletar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colPelagem.setCellValueFactory(new PropertyValueFactory<>("pelagem"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("qtde_pelucias"));

        // Aplica a validação visual (borda vermelha se vazio)
        configurarValidacaoVisual(txtNome);
        configurarValidacaoVisual(txtTipo);
        configurarValidacaoVisual(txtPelagem);
        configurarValidacaoVisual(txtValor);
        configurarValidacaoVisual(txtQtde_pelucias);

        // Lógica do campo ID (Bloqueio e Alerta Visual)
        txtID.setEditable(false);
        txtID.setStyle("-fx-background-color: #e0e0e0;");

        txtID.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                txtID.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 3px; -fx-background-color: #ffe6e6;");
            } else {
                txtID.setStyle("-fx-background-color: #e0e0e0;");
            }
        });

        // --- NOVO: Bloqueia números nos campos de texto (Nome, Tipo, Pelagem) ---
        txtNome.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches(".*\\d.*")) {
                txtNome.setText(newValue.replaceAll("\\d", "")); // Remove qualquer número
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
        // ------------------------------------------------------------------------

        // Bloqueia letra no campo da quantidade
        txtQtde_pelucias.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtQtde_pelucias.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Bloqueia letra no campo do valor
        txtValor.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                txtValor.setText(oldValue);
            }
        });

        carregarUsuarios();
    }

    // Destaca o campo de vermelho se estiver vazio
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

        if (txtNome.getText().isEmpty() || txtValor.getText().isEmpty() || txtQtde_pelucias.getText().isEmpty()) {
            System.out.println("Preencha todos os campos corretamente antes de salvar!");
            return;
        }

        UsuarioDTO pelucia = new UsuarioDTO();

        pelucia.setNome(txtNome.getText());
        pelucia.setTipo(txtTipo.getText());
        pelucia.setPelagem(txtPelagem.getText());
        pelucia.setValor(Double.parseDouble(txtValor.getText()));
        pelucia.setQtde_pelucias(Integer.parseInt(txtQtde_pelucias.getText()));

        UsuarioDAO dao = new UsuarioDAO();
        dao.inserir(pelucia);

        carregarUsuarios();
        btnLimparAction(null);
    }

    @FXML
    private void btnAlterarAction(ActionEvent event) {

        System.out.println("ALTERAR CLICADO");

        if (txtID.getText().isEmpty() || txtNome.getText().isEmpty() || txtValor.getText().isEmpty()) {
            System.out.println("Campos inválidos para alteração!");
            return;
        }

        UsuarioDTO pelucia = new UsuarioDTO();

        pelucia.setId(Integer.parseInt(txtID.getText()));
        pelucia.setNome(txtNome.getText());
        pelucia.setTipo(txtTipo.getText());
        pelucia.setPelagem(txtPelagem.getText());
        pelucia.setValor(Double.parseDouble(txtValor.getText()));
        pelucia.setQtde_pelucias(Integer.parseInt(txtQtde_pelucias.getText()));

        UsuarioDAO dao = new UsuarioDAO();
        dao.atualizar(pelucia);

        carregarUsuarios();
    }

    @FXML
    private void btnLimparAction(ActionEvent event) {

        txtID.clear();
        txtNome.clear();
        txtTipo.clear();
        txtPelagem.clear();
        txtValor.clear();
        txtQtde_pelucias.clear();

        // Força a remoção das bordas vermelhas ao limpar a tela
        txtNome.setStyle("");
        txtTipo.setStyle("");
        txtPelagem.setStyle("");
        txtValor.setStyle("");
        txtQtde_pelucias.setStyle("");
        txtID.setStyle("-fx-background-color: #e0e0e0;");
    }

    @FXML
    private void bntDeletarAction(ActionEvent event) {

        if (txtID.getText().isEmpty()) {
            System.out.println("Selecione um registro para deletar.");
            return;
        }

        int id = Integer.parseInt(txtID.getText());

        UsuarioDAO dao = new UsuarioDAO();
        dao.deletar(id);

        carregarUsuarios();
        btnLimparAction(null);
    }

    @FXML
    private void carregarCampos() {

        UsuarioDTO objUsuarioDTO = tblUsuario.getSelectionModel().getSelectedItem();

        if (objUsuarioDTO != null) {

            // Verifica se o ID do item clicado já está no campo txtID (efeito Liga/Desliga)
            if (txtID.getText().equals(String.valueOf(objUsuarioDTO.getId()))) {

                // Modo "Desliga"
                btnLimparAction(null);
                tblUsuario.getSelectionModel().clearSelection();

            } else {

                // Modo "Liga"
                txtID.setText(String.valueOf(objUsuarioDTO.getId()));
                txtNome.setText(objUsuarioDTO.getNome());
                txtTipo.setText(objUsuarioDTO.getTipo());
                txtPelagem.setText(objUsuarioDTO.getPelagem());
                txtValor.setText(String.valueOf(objUsuarioDTO.getValor()));
                txtQtde_pelucias.setText(String.valueOf(objUsuarioDTO.getQtde_pelucias()));

                // Remove alertas visuais ao preencher
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

        ArrayList<UsuarioDTO> listaUsuarios =
                objUsuarioDAO.listar();

        System.out.println(
                "Registros encontrados: " + listaUsuarios.size()
        );

        tblUsuario.setItems(
                FXCollections.observableArrayList(listaUsuarios)
        );

        // Atualiza o contador na tela
        if (lblContador != null) {
            lblContador.setText("Total de registros: " + listaUsuarios.size());
        }
    }
}