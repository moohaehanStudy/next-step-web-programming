package dao;

import exception.CustomException;
import jdbc.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate{
    public void update(String sql, PreparedStatementSetter pss) throws SQLException {
        try(Connection con = ConnectionManager.getConnection();
        PreparedStatement ps = con.prepareStatement(sql)){
         pss.setValues(ps);
         ps.executeUpdate();
        } catch(SQLException e){
            throw new CustomException("DB 업데이트 중 문제가 발생했습니다.");
        }
    }

    public <T> List<T> query(String sql, PreparedStatementSetter pss, RowMapper<T> rm) throws SQLException {
        try(Connection con = ConnectionManager.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery()){
            pss.setValues(ps);

            List<T> list = new ArrayList<>();
            while (rs.next()) {
                list.add(rm.mapRow(rs));
            }

            return list;
        } catch(SQLException e){
            throw new CustomException("DB에서 값을 가져오는 중 문제가 발생했습니다.");
        }
    }

    public <T> T queryForObject(String sql, PreparedStatementSetter pss, RowMapper<T> rm) throws SQLException {
        List<T> lists = query(sql, pss, rm);

        if(lists.isEmpty()){
            return null;
        }

        return lists.get(0);
    }
}
