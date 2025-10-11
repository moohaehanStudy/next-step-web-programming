package dao;

import jdbc.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

    public List query(String sql) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = ConnectionManager.getConnection();
            ps = con.prepareStatement(sql);
            setValues(ps);
            rs = ps.executeQuery();

            List<Object> list = new ArrayList<>();
            while (rs.next()) {
                list.add(this.mapRow(rs));
            }

            return list;
        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        }
    }

    public Object queryForObject(String sql) throws SQLException {
        List lists = query(sql);

        if(lists.isEmpty()){
            return null;
        }

        return lists.get(0);
    }

    abstract void setValues(PreparedStatement ps) throws SQLException;

    abstract Object mapRow(ResultSet rs) throws SQLException;
}
