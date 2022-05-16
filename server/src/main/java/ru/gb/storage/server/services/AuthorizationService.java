package ru.gb.storage.server.services;

import java.sql.*;

public class AuthorizationService {
    private Connection connection;
    private Statement statement;

    public String checkUserInDB(String login, String password) throws SQLException {
        connectToDB();
        String sqlRequest = String.format("SELECT nickname FROM users WHERE login = '%s' AND pass = '%s'", login, password);
        ResultSet result = statement.executeQuery(sqlRequest);
        if (result.next()){
            return result.getString(1);
        }
        return null;
    }

    private void connectToDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:cloudDB.db");
            statement = connection.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnectDB() {
        try {
            connection.close();
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

}
