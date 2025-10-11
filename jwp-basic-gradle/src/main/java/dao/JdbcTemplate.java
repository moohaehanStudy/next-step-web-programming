package dao;

import jdbc.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public abstract class JdbcTemplate {
    public void update(String sql) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;

        try{
            con = ConnectionManager.getConnection();
            ps = con.prepareStatement(sql);
            setValues(ps);

            ps.executeUpdate();
        } finally{
            if(ps != null) ps.close();
            if(con != null) con.close();
        }
    }

    public List query(String sql) {

    }

    public Object queryForObject(String sql){

    }

    abstract void setValues(PreparedStatement ps) throws SQLException;

    abstract Object mapRow(ResultSet rs) throws SQLException;
}
