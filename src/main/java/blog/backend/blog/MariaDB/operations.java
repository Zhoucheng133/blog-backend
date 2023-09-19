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
            ResultSet resultSet = statement.executeQuery("SELECT * FROM blog ORDER BY createDate DESC;" );
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

    public static blogContent getTitle(int id){
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
                    return new blogContent("", null, null);
                }
            }
            resultSet.close();
            statement.close();
            connection.close();
            return new blogContent(title, tag, date);
        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
        }
        return new blogContent("", null, null);
    }
}
