package dao;

import model.User;

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

        jdbcTemplate.update(INSERTQUERY, user.getUserId(), user.getPassword(), user.getName(), user.getEmail());
    }

    public void update(User user) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        jdbcTemplate.update(UPDATEQUERY, user.getPassword(), user.getName(), user.getEmail(), user.getUserId());
    }

    public User findByUserId(String userId) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

//        PreparedStatementSetter pss = new PreparedStatementSetter() {
//            public void setValues(PreparedStatement ps) throws SQLException {
//                ps.setString(1, userId);
//            }
//        };

        RowMapper<User> rm = createRowMapper();

        //return jdbcTemplate.queryForObject(SELECTONEQUERY, pss, rm);
        return jdbcTemplate.queryForObject(SELECTONEQUERY, rm, userId);
    }

    private RowMapper<User> createRowMapper() {
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
        return rm;
    }

    public List<User> findAll() throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        // 람다식으로 변환
        //PreparedStatementSetter pss = ps -> {};

        RowMapper<User> rm = createRowMapper();

        return jdbcTemplate.query(SELECTALLQUERY, rm);
        //return jdbcTemplate.query(SELECTALLQUERY, pss, rm);
    }
}
