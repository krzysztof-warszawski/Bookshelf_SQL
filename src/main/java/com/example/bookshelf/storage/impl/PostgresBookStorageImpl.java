package com.example.bookshelf.storage.impl;

import com.example.bookshelf.storage.BookStorage;
import com.example.bookshelf.type.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresBookStorageImpl implements BookStorage {

    private static final String POSTGRES_JDBC_URL = "jdbc:postgresql://localhost:5432/bookshelf_db";
    private static final String POSTGRES_USER_NAME = "postgres";
    private static final String POSTGRES_USER_PASS = "postgres";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Server can't find postgresql Driver class: \n" + e);
        }
    }

    private Connection initializeDataBaseConnection() {
        try {
            return DriverManager.getConnection(POSTGRES_JDBC_URL, POSTGRES_USER_NAME, POSTGRES_USER_PASS);
        } catch (SQLException e) {
            System.err.println("Server can't initialize databse connection: \n" + e);
            throw new RuntimeException("Server can't initialize databse connection");
        }
    }

    private void closeDataBaseResources(Connection connection, Statement statement) {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error during closing database resources: \n" + e);
            throw new RuntimeException("Error during closing database resources");
        }
    }

    @Override
    public Book getBook(long id) {

        final String sqlSelectBook = "SELECT * FROM books WHERE book_id = ?;";

        Connection connection = initializeDataBaseConnection();
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sqlSelectBook);

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Book book = new Book();
                book.setId(resultSet.getLong("book_id"));
                book.setTitle(resultSet.getString("title"));
                book.setAuthor(resultSet.getString("author"));
                book.setPageSum(resultSet.getInt("page_sum"));
                book.setYearOfPublished(resultSet.getInt("year_of_published"));
                book.setPublishingHouse(resultSet.getString("publishing_house"));

                return book;
            }
        } catch (SQLException e) {
            System.err.println("Error during invoke SQL query: \n" + e.getMessage());
            throw new RuntimeException("Error during invoke SQL query");
        } finally {
            closeDataBaseResources(connection, preparedStatement);
        }

        return null;
    }

    @Override
    public List<Book> getAllBooks() {

        final String sqlSelectAllBook = "SELECT * FROM books;";

        Connection connection = initializeDataBaseConnection();
        Statement statement = null;

        List<Book> books = new ArrayList<>();

        try {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlSelectAllBook);

            while (resultSet.next()) {
                Book book = new Book();
                book.setId(resultSet.getLong("book_id"));
                book.setTitle(resultSet.getString("title"));
                book.setAuthor(resultSet.getString("author"));
                book.setPageSum(resultSet.getInt("page_sum"));
                book.setYearOfPublished(resultSet.getInt("year_of_published"));
                book.setPublishingHouse(resultSet.getString("publishing_house"));

                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error during invoke SQL query: \n" + e.getMessage());
            throw new RuntimeException("Error during invoke SQL query");
        } finally {
            closeDataBaseResources(connection, statement);
        }

        return books;
    }

    @Override
    public void addBook(Book book) {

        final String sqlInsertBook = "INSERT INTO books(" +
                "book_id, title, author, page_sum, year_of_published, publishing_house)" +
                "VALUES (?, ?, ?, ?, ?, ?);";

        Connection connection = initializeDataBaseConnection();
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sqlInsertBook);

            preparedStatement.setLong(1, book.getId());
            preparedStatement.setString(2, book.getTitle());
            preparedStatement.setString(3, book.getAuthor());
            preparedStatement.setInt(4, book.getPageSum());
            preparedStatement.setInt(5, book.getYearOfPublished());
            preparedStatement.setString(6, book.getPublishingHouse());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error during invoke SQL query: \n" + e.getMessage());
            throw new RuntimeException("Error during invoke SQL query");
        } finally {
            closeDataBaseResources(connection, preparedStatement);
        }
    }
}