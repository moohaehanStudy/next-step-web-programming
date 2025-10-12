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

        PreparedStatementSetter pss = ps -> {
            ps.setString(1, user.getUserId());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.setString(4, user.getEmail());
        };

        jdbcTemplate.update(INSERTQUERY, pss);
    }

    public void update(User user) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        PreparedStatementSetter pss = ps -> {
            ps.setString(1, user.getPassword());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getUserId());
        };

        jdbcTemplate.update(UPDATEQUERY, pss);
    }

    public User findByUserId(String userId) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        PreparedStatementSetter pss = ps -> {
            ps.setString(1, userId);
        };

        RowMapper<User> rm = createRowMapper();

        return jdbcTemplate.queryForObject(SELECTONEQUERY, pss, rm);
    }

    private RowMapper<User> createRowMapper() {
        RowMapper<User> rm = rs -> new User(
                rs.getString(USERID),
                rs.getString(PASSWORD),
                rs.getString(NAME),
                rs.getString(EMAIL)
        );

        return rm;
    }

    public List<User> findAll() throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        PreparedStatementSetter pss = ps -> {};

        RowMapper<User> rm = createRowMapper();

        return jdbcTemplate.query(SELECTALLQUERY, pss, rm);
    }
}
