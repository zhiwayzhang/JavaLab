package com.qst.dms.dos;

import com.qst.dms.db.DBUtil;

import java.sql.*;

public class MySQLDemo {

    private void closeAll(Connection connection, ResultSet rs, Statement st) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        Connection connection = null;
        connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/temp?useUnicode=true&characterEncoding=UTF-8",
                "root",
                "123456"
        );
        Statement st = connection.createStatement();
        String sql = "insert into temp1 values('0012','CARAlice', 'Peking')";
        st.execute(sql);

        String sql_query = "select* from temp1";
        ResultSet rs = st.executeQuery(sql_query);
        while (rs.next()) {
            String id = rs.getString(1);
            String name = rs.getString(2);
            String address = rs.getString(3);
            System.out.println("{ id:" + id + "  name:"+name + "  address:" + address + " }");
        }



    }
}
