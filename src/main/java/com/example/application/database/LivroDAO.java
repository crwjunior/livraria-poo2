package com.example.application.database;

import com.example.application.dto.LivroDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LivroDAO {

    private static Connection connection;

    static {
        try {
            connection = new FabricaDeConexoes().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void adicionarLivro(LivroDTO livroDTO) throws SQLException {
        String sql = """
                    INSERT INTO livros (titulo, autor, descricao, dataPublicacao)
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

    public static List<LivroDTO> listarLivros() throws SQLException {
        List<LivroDTO> livroDTOList = new ArrayList<>();
        String sql = """
                SELECT * FROM livros;
                """;
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();

        try {
            while (resultSet.next()) {
                LivroDTO livroDTO = new LivroDTO();
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

    public static void atualizarLivro(Integer id, LivroDTO novoLivro) throws SQLException {
        String sql = "UPDATE livros SET titulo=?, autor=?, descricao=?, dataPublicacao=? WHERE id=?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        try {
            preparedStatement.setString(1, novoLivro.getTitulo());
            preparedStatement.setString(2, novoLivro.getAutor());
            preparedStatement.setString(3, novoLivro.getDescricao());
            preparedStatement.setDate(4, Date.valueOf(novoLivro.getDataPublicacao()));
            preparedStatement.setInt(5, id);

            int rowsUpdated = preparedStatement.executeUpdate();

            if (rowsUpdated == 0) {
                throw new RuntimeException("Nenhum registro de livro atualizado. O ID especificado pode não existir.");
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    public static LivroDTO buscarLivroPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM livros WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = null;

        try {
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                LivroDTO livroDTO = new LivroDTO();
                livroDTO.setId(resultSet.getInt("id"));
                livroDTO.setTitulo(resultSet.getString("titulo"));
                livroDTO.setAutor(resultSet.getString("autor"));
                livroDTO.setDescricao(resultSet.getString("descricao"));
                livroDTO.setDataPublicacao(resultSet.getDate("dataPublicacao").toLocalDate());
                return livroDTO;
            } else {
                return null;
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



    public static void deletarLivro(Integer id) throws SQLException {
        String sql = """
                    DELETE FROM livros WHERE id = ?;
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
