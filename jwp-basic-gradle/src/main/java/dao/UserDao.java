package dao;

import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserDao {
    private static final String USERID = "userId";
    private static final String PASSWORD = "password";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String INSERTQUERY = "INSERT INTO USERS VALUES (?, ?, ?, ?)";
    private static final String UPDATEQUERY = "UPDATE USERS SET password = ?, name = ?, email = ? WHERE userId = ?";
    private static final String SELECTONEQUERY = "SELECT userId, password, name, email FROM USERS WHERE userId = ?";
    private static final String SELECTALLQUERY = "SELECT userId, password, name, email FROM USERS";

    public void insert(User user) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        PreparedStatementSetter pss = new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException{
                ps.setString(1, user.getUserId());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getName());
                ps.setString(4, user.getEmail());
            }
        };

        jdbcTemplate.update(INSERTQUERY, pss);
    }

    public void update(User user) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        PreparedStatementSetter pss = new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException{
                ps.setString(1, user.getPassword());
                ps.setString(2, user.getName());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getUserId());
            }
        };

        jdbcTemplate.update(UPDATEQUERY, pss);
    }

    public User findByUserId(String userId) throws SQLException {
        JdbcTemplate selectJdbcTemplate = new JdbcTemplate();

        PreparedStatementSetter pss = new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, userId);
            }
        };

        RowMapper<User> rm = new RowMapper<User>(){
            public User mapRow(ResultSet rs) throws SQLException {
                return new User(
                        rs.getString(USERID),
                        rs.getString(PASSWORD),
                        rs.getString(NAME),
                        rs.getString(EMAIL)
                );
            }
        };

        return selectJdbcTemplate.queryForObject(SELECTONEQUERY, pss, rm);
    }

    public List<User> findAll() throws SQLException {
        JdbcTemplate selectJdbcTemplate = new JdbcTemplate();

        PreparedStatementSetter pss = new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {}
        };

        /* 람다식으로 변환
        PreparedStatementSetter pss = ps -> {};
        */

        RowMapper<User> rm = new RowMapper<User>(){
            public User mapRow(ResultSet rs) throws SQLException {
                return new User(
                        rs.getString(USERID),
                        rs.getString(PASSWORD),
                        rs.getString(NAME),
                        rs.getString(EMAIL)
                );
            }
        };

        /* 람다식으로 변환 시
        RowMapper<User> rm = rs -> new User(
                rs.getString(USERID),
                rs.getString(PASSWORD),
                rs.getString(NAME),
                rs.getString(EMAIL)
        );
        */
        return selectJdbcTemplate.query(SELECTALLQUERY, pss, rm);
    }
}
