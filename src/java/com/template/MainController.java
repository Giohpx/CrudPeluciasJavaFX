package com.template;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colPelagem.setCellValueFactory(new PropertyValueFactory<>("pelagem"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("qtde_pelucias"));

        carregarUsuarios();
    }

    @FXML
    private void btnSalvarAction(ActionEvent event) {

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
    }

    @FXML
    private void bntDeletarAction(ActionEvent event) {

        int id = Integer.parseInt(txtID.getText());

        UsuarioDAO dao = new UsuarioDAO();
        dao.deletar(id);

        carregarUsuarios();
        btnLimparAction(null);
    }

    @FXML
    private void carregarCampos() {

        UsuarioDTO objUsuarioDTO =
                tblUsuario.getSelectionModel().getSelectedItem();

        if (objUsuarioDTO != null) {

            txtID.setText(String.valueOf(objUsuarioDTO.getId()));
            txtNome.setText(objUsuarioDTO.getNome());
            txtTipo.setText(objUsuarioDTO.getTipo());
            txtPelagem.setText(objUsuarioDTO.getPelagem());
            txtValor.setText(String.valueOf(objUsuarioDTO.getValor()));
            txtQtde_pelucias.setText(
                    String.valueOf(objUsuarioDTO.getQtde_pelucias())
            );
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
    }
}