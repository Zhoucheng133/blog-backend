package blog.backend.blog.MariaDB;

import java.sql.*;
import java.util.ArrayList;
import blog.backend.blog.Controllers.blog;

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

    public static Boolean Insert(String title, int top, String tag) {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            stmt = conn.createStatement();
            String sql;
            if(!tag.isEmpty()){
                sql = "INSERT INTO blog (title, createDate, path, top, tag) VALUES ('"+title+"',CONVERT_TZ(CURRENT_TIMESTAMP(), 'UTC', '+8:00'), '"+title+".md', "+top+", '"+tag+"');";
            }else{
                sql = "INSERT INTO blog (title, createDate, path, top) VALUES ('"+title+"',CONVERT_TZ(CURRENT_TIMESTAMP(), 'UTC', '+8:00'), '"+title+".md', "+top+");";
            }
            stmt.executeUpdate(sql);
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (stmt != null)
                    stmt.close();
            } catch (SQLException se2) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
            }
        }
    }

    public static ArrayList<blog> getAllBlogs(){
        ArrayList<blog> list=new ArrayList<>();
        try {
            Class.forName(DRIVER);
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM blog" );
            while (resultSet.next()) {
                blog data=new blog(resultSet.getInt(1), resultSet.getString(2), resultSet.getTimestamp(3), resultSet.getString(4), resultSet.getBoolean(5), resultSet.getString(6));
                list.add(data);
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
            return list;
        }
        return list;
    }
}
