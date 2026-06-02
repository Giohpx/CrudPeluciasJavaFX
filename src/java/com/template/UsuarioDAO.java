package com.template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UsuarioDAO {

    public void inserir(UsuarioDTO pelucia) {

        String sql = "INSERT INTO pelucia (nome, tipo_pelucia, pelagem, valor, qtde_pelucia) VALUES (?, ?, ?, ?, ?)";

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

            System.out.println("\nPelucia cadastrada com sucesso!");

        } catch (SQLException e) {
            System.out.println("Erro ao inserir pelucia: " + e.getMessage());
        }
    }

    public ArrayList<UsuarioDTO> listar() { // Mudou de void para ArrayList e removeu o parâmetro

        String sql = "SELECT * FROM pelucia";
        ArrayList<UsuarioDTO> lista = new ArrayList<>(); // Criamos a lista que guardará os dados

        try (
                Connection conn = new Conexao().conectar();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {

            while (rs.next()) {
                // Criamos um objeto para cada linha do banco de dados
                UsuarioDTO pelucia = new UsuarioDTO();

                pelucia.setId(rs.getInt("id_pelucia"));
                pelucia.setNome(rs.getString("nome"));
                pelucia.setTipo(rs.getString("tipo_pelucia"));
                pelucia.setPelagem(rs.getString("pelagem")); // Buscando a pelagem do banco
                pelucia.setValor(rs.getDouble("valor"));
                pelucia.setQtde_pelucias(rs.getInt("qtde_pelucia"));

                lista.add(pelucia);
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar pelúcias: " + e.getMessage());
        }

        return lista;
    }
    public void atualizar(UsuarioDTO pelucia) {

        String sql = "UPDATE pelucia SET nome = ?, tipo_pelucia = ?, pelagem = ?, valor = ?, qtde_pelucia = ? WHERE id_pelucia = ?";

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
                System.out.println("\nPelucia atualizada com sucesso!");
            } else {
                System.out.println("\nNenhuma pelucia encontrada com esse ID.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar: " + e.getMessage());
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
                System.out.println("Pelucia removida");
            } else {
                System.out.println("\nID não encontrado.");
            }

        } catch (SQLException e) {
            System.out.println("Erro ao deletar: " + e.getMessage());
        }
    }
}