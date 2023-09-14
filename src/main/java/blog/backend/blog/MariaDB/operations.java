package blog.backend.blog.MariaDB;

import java.sql.*;
import java.util.ArrayList;

public class operations {
    private static final String DRIVER = "org.mariadb.jdbc.Driver";
    private static final String URL = "jdbc:mariadb://"+dbData.url+":3306/" + dbData.dbName;
    private static final String USER = dbData.name;
    private static final String PASSWORD = dbData.password;

    public static int Check2(String tableName, String columnName1, String value1, String columnName2, String value2) {
        int tmp = 0;
        try {
            Class.forName(DRIVER);
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT id FROM " + tableName + " WHERE " + columnName1
                    + " = '" + value1 + "' AND " + columnName2 + " = '" + value2 + "'");
            if (resultSet.next()) {
                tmp = resultSet.getInt(1);
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
        }
        return tmp;
    }

    public static ArrayList<String> getAllTitle(){
        ArrayList<String> titles = new ArrayList<>();
        try {
            Class.forName(DRIVER);
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String sql = "SELECT title FROM blog";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                titles.add(title);
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
        }
        return titles;
    }
}
