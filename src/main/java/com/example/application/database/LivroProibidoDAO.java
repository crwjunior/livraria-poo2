package com.example.application.database;

import com.example.application.dto.LivroDTO;
import com.example.application.dto.LivrosProibidosDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroProibidoDAO {

    private static Connection connection;

    static {
        try {
            connection = new FabricaDeConexoes().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void adicionarLivro(LivrosProibidosDTO livroDTO) throws SQLException {
        String sql = """
                    INSERT INTO livros_proibidos (titulo, autor, descricao, dataPublicacao)
                    VALUES (?,?,?,?);
            """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try {
            preparedStatement.setString(1, livroDTO.getTitulo());
            preparedStatement.setString(2, livroDTO.getAutor());
            preparedStatement.setString(3, livroDTO.getDescricao());
            preparedStatement.setDate(4, Date.valueOf(livroDTO.getDataPublicacao()));
            preparedStatement.executeUpdate();

        } catch (SQLException ex){
            throw new RuntimeException(ex);
        } finally {
            if (preparedStatement !=null){
                preparedStatement.close();
            }
        }
    }

    public static List<LivrosProibidosDTO> listarLivros() throws SQLException {
        List<LivrosProibidosDTO> livroDTOList = new ArrayList<>();
        String sql = """
                SELECT * FROM livros_proibidos;
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        try {
            while (resultSet.next()) {
                LivrosProibidosDTO livroDTO = new LivrosProibidosDTO();
                livroDTO.setId(resultSet.getInt("id"));
                livroDTO.setTitulo(resultSet.getString("titulo"));
                livroDTO.setAutor(resultSet.getString("autor"));
                livroDTO.setDescricao(resultSet.getString("descricao"));
                livroDTO.setDataPublicacao(resultSet.getDate("dataPublicacao").toLocalDate());
                livroDTOList.add(livroDTO);
            }
        } catch (SQLException ex){
            throw new RuntimeException(ex);
        } finally {
            if (preparedStatement !=null){
                preparedStatement.close();
            }
        }
        return livroDTOList;
    }

    public static LivrosProibidosDTO buscarLivroProibidoPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM livros_proibidos WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = null;

        try {
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                LivrosProibidosDTO livroDTO = new LivrosProibidosDTO();
                livroDTO.setId(resultSet.getInt("id"));
                livroDTO.setTitulo(resultSet.getString("titulo"));
                livroDTO.setAutor(resultSet.getString("autor"));
                livroDTO.setDescricao(resultSet.getString("descricao"));
                livroDTO.setDataPublicacao(resultSet.getDate("dataPublicacao").toLocalDate());
                return livroDTO;
            } else {
                return null; // Retornar null se o livro proibido com o ID especificado não for encontrado.
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }
    public static void atualizarLivroProibido(Integer id, LivrosProibidosDTO novoLivro) throws SQLException {
        String sql = "UPDATE livros_proibidos SET titulo=?, autor=?, descricao=?, dataPublicacao=? WHERE id=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        try {
            preparedStatement.setString(1, novoLivro.getTitulo());
            preparedStatement.setString(2, novoLivro.getAutor());
            preparedStatement.setString(3, novoLivro.getDescricao());
            preparedStatement.setDate(4, Date.valueOf(novoLivro.getDataPublicacao()));
            preparedStatement.setInt(5, id);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new RuntimeException("Nenhum registro de livro proibido atualizado. O ID especificado pode não existir.");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }


    public static void deletarLivro(Integer id) throws SQLException {
        String sql = """
                    DELETE FROM livros_proibidos WHERE id = ?;
            """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

        } catch (SQLException ex){
            throw new RuntimeException(ex);
        } finally {
            if (preparedStatement !=null){
                preparedStatement.close();
            }
        }
    }
}
