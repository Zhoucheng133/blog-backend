package blog.backend.blog.MariaDB;

import java.sql.*;
import java.util.ArrayList;
import blog.backend.blog.Controllers.blog;
import blog.backend.blog.Controllers.blogContent;

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

    public static Boolean Insert(String title, int top, String tag, String cata) {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(DRIVER);
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            stmt = conn.createStatement();
            String sql;
            if(!tag.isEmpty()){
                sql = String.format("INSERT INTO blog (title, createDate, path, top, tag, cata) VALUES ('%s', CONVERT_TZ(CURRENT_TIMESTAMP(), 'UTC', '+8:00'), '%s', '%d', '%s', '%s')", title, title+".md", top, tag, cata);
//                sql = "INSERT INTO blog (title, createDate, path, top, tag, cata) VALUES ('"+title+"',CONVERT_TZ(CURRENT_TIMESTAMP(), 'UTC', '+8:00'), '"+title+".md', "+top+", '"+tag+"', '"+cata+"');";
            }else{
                sql = String.format("INSERT INTO blog (title, createDate, path, top, cata) VALUES ('%s', CONVERT_TZ(CURRENT_TIMESTAMP(), 'UTC', '+8:00'), '%s', '%d', '%s')", title, title+".md", top, cata);
//                sql = "INSERT INTO blog (title, createDate, path, top, cata) VALUES ('"+title+"',CONVERT_TZ(CURRENT_TIMESTAMP(), 'UTC', '+8:00'), '"+title+".md', "+top+");";
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
            ResultSet resultSet = statement.executeQuery("SELECT * FROM blog ORDER BY createDate DESC;" );
            while (resultSet.next()) {
                blog data=new blog(resultSet.getInt(1), resultSet.getString(2), resultSet.getTimestamp(3), resultSet.getString(4), resultSet.getBoolean(5), resultSet.getString(6), resultSet.getString(7));
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


    public static ArrayList<String> getAllTitles(){
        ArrayList<String> titles = new ArrayList<>();
        try {
            Class.forName(DRIVER);
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            String sql = "SELECT title FROM blog";
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                titles.add(title);
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
        return titles;
    }

    public static Boolean delBlog(String name){
        Connection connection = null;
        try {
            Class.forName(DRIVER);
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String query = "DELETE FROM blog WHERE title = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, name);
            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected > 0){
                String alterSQL = "ALTER TABLE blog AUTO_INCREMENT=1";
                Statement alterStatement = connection.createStatement();
                alterStatement.execute(alterSQL);
                alterStatement.close();
                connection.close();
                return true;
            }
        } catch (SQLException e) {
            return false;
        } catch (ClassNotFoundException e) {
            return false;
        }
        return false;
    }

    public static blogContent getBlogByName(String name){
        String title="";
        String tag=null;
        Timestamp date=null;
        String cata=null;
        try {
            Class.forName(DRIVER);
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT title, createDate, tag FROM blog WHERE title = '" + name + "'");
            if (resultSet.next()) {
                title = resultSet.getString(1);
                date=resultSet.getTimestamp(2);
                tag = resultSet.getString(3);
                cata = resultSet.getString(4);
                if (title == null || title.equals("null")) {
                    return new blogContent("", null, null, cata);
                }
            }
            resultSet.close();
            statement.close();
            connection.close();
            return new blogContent(title, tag, date, cata);
        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
        }
        return new blogContent("", null, null, null);
    }

    public static blogContent getBlogById(int id){
        String title="";
        String tag=null;
        Timestamp date=null;
        try {
            Class.forName(DRIVER);
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT title, createDate, tag FROM blog WHERE id = " + id);
            if (resultSet.next()) {
                title = resultSet.getString(1);
                date=resultSet.getTimestamp(2);
                tag = resultSet.getString(3);
                if (title == null || title.equals("null")) {
                    return new blogContent("", null, null, null);
                }
            }
            resultSet.close();
            statement.close();
            connection.close();
            return new blogContent(title, tag, date, null);
        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
        }
        return new blogContent("", null, null, null);
    }
}
