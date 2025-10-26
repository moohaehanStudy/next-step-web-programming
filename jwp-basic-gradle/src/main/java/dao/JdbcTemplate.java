package dao;

import exception.CustomException;
import jdbc.ConnectionManager;
import jdbc.KeyHolder;

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

    public void update(String sql, Object... values) throws SQLException {
        PreparedStatementSetter pss = createPreparedStatementSetter(values);
        update(sql, pss);
    }

    public PreparedStatementSetter createPreparedStatementSetter(Object... values){
        return ps -> {
            for(int i = 0; i < values.length; i++){
                ps.setObject(i + 1, values[i]);
            }
        };
    }

    public <T> List<T> query(String sql, PreparedStatementSetter pss, RowMapper<T> rm) throws SQLException {
        try (Connection con = ConnectionManager.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            pss.setValues(ps);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> list = new ArrayList<>();
                while (rs.next()) {
                    list.add(rm.mapRow(rs));
                }
                return list;
            }
        } catch (SQLException e) {
            throw new CustomException("DB에서 값을 가져오는 중 문제가 발생했습니다.");
        }
    }

    public <T> List<T> query(String sql, RowMapper<T> rm, Object... values) throws SQLException {
        PreparedStatementSetter pss = createPreparedStatementSetter(values);

        return query(sql, pss, rm);
    }

    public <T> T queryForObject(String sql, PreparedStatementSetter pss, RowMapper<T> rm) throws SQLException {
        List<T> lists = query(sql, pss, rm);

        if(lists.isEmpty()){
            return null;
        }

        return lists.get(0);
    }

    public void update(String sql, PreparedStatementSetter pss, KeyHolder keyHolder) {
        try(Connection con = ConnectionManager.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
            pss.setValues(ps);
            ps.executeUpdate();

            //JDBC 기능으로, DB가 AUTO_INCREMENT로 생성한 키를 ResultSet 형태로 반환
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    keyHolder.setId(rs.getLong(1));
                }
            }
        } catch(SQLException e){
            throw new CustomException("DB 업데이트 중 문제가 발생했습니다.");
        }
    }

    public <T> T queryForObject(String sql, RowMapper<T> rm, Object... values) throws SQLException {
        PreparedStatementSetter pss = createPreparedStatementSetter(values);
        return queryForObject(sql, pss, rm);
    }
}
