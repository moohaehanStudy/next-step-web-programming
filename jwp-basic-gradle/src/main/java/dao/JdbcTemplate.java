package dao;

import jdbc.ConnectionManager;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class JdbcTemplate {
    public void update(User user) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;

        try{
            con = ConnectionManager.getConnection();
            String sql = createQuery();
            ps = con.prepareStatement(sql);
            setValues(user, ps);

            ps.executeUpdate();
        } finally{
            if(ps != null) ps.close();
            if(con != null) con.close();
        }
    }

    abstract void setValues(User user, PreparedStatement ps) throws SQLException;

    abstract String createQuery();


}
