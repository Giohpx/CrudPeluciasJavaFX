package com.template.model.dao;

import com.template.Conexao;
import com.template.model.dto.UsuarioDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UsuarioDAO {

    private static final Logger logger = Logger.getLogger(UsuarioDAO.class.getName());

    public ArrayList<UsuarioDTO> listar() {

        String sql = "SELECT * FROM pelucia";
        ArrayList<UsuarioDTO> lista = new ArrayList<>();

        try (
                Connection conn = new Conexao().conectar();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {

                UsuarioDTO pelucia = new UsuarioDTO();

                pelucia.setId(rs.getInt("id_pelucia"));
                pelucia.setNome(rs.getString("nome"));
                pelucia.setTipo(rs.getString("tipo_pelucia"));
                pelucia.setPelagem(rs.getString("pelagem"));
                pelucia.setValor(rs.getDouble("valor"));
                pelucia.setQtde_pelucias(rs.getInt("qtde_pelucias"));

                lista.add(pelucia);
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao listar pelúcias.", e);
        }

        return lista;
    }

    public void inserir(UsuarioDTO pelucia) {

        String sql = "INSERT INTO pelucia (nome, tipo_pelucia, pelagem, valor, qtde_pelucias) VALUES (?, ?, ?, ?, ?)";

        try (
                Connection conn = new Conexao().conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, pelucia.getNome());
            stmt.setString(2, pelucia.getTipo());
            stmt.setString(3, pelucia.getPelagem());
            stmt.setDouble(4, pelucia.getValor());
            stmt.setInt(5, pelucia.getQtde_pelucias());

            stmt.executeUpdate();

            logger.info("Pelúcia cadastrada com sucesso!");

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao inserir pelúcia.", e);
        }
    }

    public void atualizar(UsuarioDTO pelucia) {

        String sql = "UPDATE pelucia SET nome = ?, tipo_pelucia = ?, pelagem = ?, valor = ?, qtde_pelucias = ? WHERE id_pelucia = ?";

        try (
                Connection conn = new Conexao().conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setString(1, pelucia.getNome());
            stmt.setString(2, pelucia.getTipo());
            stmt.setString(3, pelucia.getPelagem());
            stmt.setDouble(4, pelucia.getValor());
            stmt.setInt(5, pelucia.getQtde_pelucias());
            stmt.setInt(6, pelucia.getId());

            int linhas = stmt.executeUpdate();

            if (linhas > 0) {
                logger.info("Pelúcia atualizada com sucesso!");
            } else {
                logger.warning("Nenhuma pelúcia encontrada com esse ID para atualizar.");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao atualizar pelúcia.", e);
        }
    }

    public void deletar(int id) {

        String sql = "DELETE FROM pelucia WHERE id_pelucia = ?";

        try (
                Connection conn = new Conexao().conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {

            stmt.setInt(1, id);

            int linhas = stmt.executeUpdate();

            if (linhas > 0) {
                logger.info("Pelúcia removida com sucesso!");
            } else {
                logger.warning("ID não encontrado para deletar.");
            }

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao deletar pelúcia.", e);
        }
    }
}