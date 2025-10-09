package dao;

import jdbc.ConnectionManager;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    public void insert(User user) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;

        try{
            con = ConnectionManager.getConnection();
            String sql = "INSERT INTO USERS VALUES (?, ?, ?, ?)";
            ps = con.prepareStatement(sql);
            ps.setString(1, user.getUserId());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.setString(4, user.getEmail());

            ps.executeUpdate();
        } finally{
            if(ps != null) ps.close();
            if(con != null) con.close();
        }
    }

    public User findByUserId(String userId) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            con = ConnectionManager.getConnection();
            String sql = "SELECT userId, password, name, email FROM USERS WHERE userId = ?";

            ps = con.prepareStatement(sql);
            ps.setString(1, userId);

            rs = ps.executeQuery();

            User user = null;
            if(rs.next()){
                user = new User(
                        rs.getString("userId"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("email")
                );
            }
            return user;
        }  finally {
            if(rs != null) rs.close();
            if(ps != null) ps.close();
            if(con != null) con.close();
        }
    }

    public List<User> findAll() throws SQLException{
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            con = ConnectionManager.getConnection();
            String sql = "SELECT userId, password, name, email FROM USERS";

            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();

            List<User> users = new ArrayList<>();

            while(rs.next()){
                User user = new User(
                        rs.getString("userId"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("email")
                );
                users.add(user);
            }
            return users;
        } finally {
            if(rs != null) rs.close();
            if(ps != null) ps.close();
            if(con != null) con.close();
        }
    }

    public void update(User user) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;

        try{
            con = ConnectionManager.getConnection();
            String sql = "UPDATE USERS SET password = ?, name = ?, email = ? WHERE userId = ?";

            ps = con.prepareStatement(sql);
            ps.setString(1, user.getPassword());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getUserId());

            ps.executeUpdate();
        } finally{
            if(ps != null) ps.close();
            if(con != null) con.close();
        }
    }
}
