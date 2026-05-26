package com.template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LojaPeluciaDAO {
    String sql = "INSERT INTO pelucias (nome_pelucia, tipo_pelucia, qtde_pelucia, pelagem_pelucia, valor_pelucia) VALUES (?, ?, ?, ?, ?)"; // parametro vazio, tendo segurança pra preencher o dado depois

        try (
    Connection conn = new Conexao().conectar();
    PreparedStatement stmt = conn.prepareStatement(sql)) { // estudar try with resources


        // try aqui é tipo: "tenta fazer isso"
        // e se der erro vai pro catch lá embaixo

        stmt.setString(1, pelucia.getNome());
        stmt.setString(2, pelucia.getTipo());
        stmt.setInt(3, pelucia.getQuantidade());
        stmt.setString(4, pelucia.getPelagem());
        stmt.setDouble(5, pelucia.getValor());

        stmt.executeUpdate();

        System.out.println("\nPelucia cadastrada com sucesso!");

    } catch (
    SQLException e) {
        System.out.println("Erro ao inserir pelucia: " + e.getMessage());
    }
}

public void listar() {

    String sql = "SELECT * FROM pelucias";

    try (Connection conn = new Conexao().conectar(); // abrindo conexao com o banco
         PreparedStatement stmt = conn.prepareStatement(sql); // vai preprar o comando do sql
         ResultSet rs = stmt.executeQuery()) { // vai aguardar o resultado da consulta

        //result set é pra armazenar e permitir acessar os dados da consulta
        System.out.println("\n LISTA DE PELUCIAS:\n");

        while (rs.next()) {

            System.out.println(
                    "ID: " + rs.getInt("id_pelucia") +
                            " | Nome da pelucia: " + rs.getString("nome_pelucia") +
                            " | Tipo da pelucia: " + rs.getString("tipo_pelucia") +
                            " | Quantidade da pelucia: " + rs.getInt("qtde_pelucia") +
                            " | Valor da pelucia: " + rs.getDouble("valor_pelucia")
            );
        }

    } catch (SQLException e) { // se der erro, vai mostrar isso
        System.out.println("Erro ao listar pelúcias: " + e.getMessage());
    }
}

public void atualizar(LojaPeluciaDTO pelucia) {
    // update com WHERE pra não atualizar tudo sem querer kkk
    String sql = "UPDATE pelucias SET nome_pelucia = ?, tipo_pelucia = ?, valor_pelucia = ?, pelagem_pelucia = ?, qtde_pelucia = ? WHERE id_pelucia = ?";

    try (Connection conn = new Conexao().conectar();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, pelucia.getNome());
        stmt.setString(2, pelucia.getTipo());
        stmt.setDouble(3, pelucia.getValor());
        stmt.setString(4, pelucia.getPelagem());
        stmt.setInt(5, pelucia.getQuantidade());
        stmt.setInt(6, pelucia.getId());

        int linhas = stmt.executeUpdate();

        if (linhas > 0) {
            System.out.println("\n Pelucia atualizada com sucesso!");
        } else {
            System.out.println("\nNenhuma pelucia encontrada com esse ID.");
        }

    } catch (SQLException e) {
        System.out.println(" Erro ao atualizar: " + e.getMessage());
    }
}

public void deletar(int id) {
    // deleta pelo ID
    String sql = "DELETE FROM pelucias WHERE id_pelucia = ?";

    try (Connection conn = new Conexao().conectar();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        // coloca o ID no ?
        stmt.setInt(1, id);

        int linhas = stmt.executeUpdate();


        // verifica se deletou mesmo

        if (linhas > 0) {
            System.out.println("Pelucia removida");
        } else {
            System.out.println("\n ID não encontrado.");
        }

    } catch (SQLException e) {
        System.out.println("Erro ao deletar: " + e.getMessage());
    }
}
