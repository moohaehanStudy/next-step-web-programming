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
        JdbcTemplate jdbcTemplate = new JdbcTemplate() {
            void setValues(PreparedStatement ps) throws SQLException{
                ps.setString(1, user.getUserId());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getName());
                ps.setString(4, user.getEmail());
            }

            void mapRow(ResultSet rs) throws SQLException {}
        };
        jdbcTemplate.update(INSERTQUERY);
    }

    public void update(User user) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate() {
            void setValues(PreparedStatement ps) throws SQLException{
                ps.setString(1, user.getPassword());
                ps.setString(2, user.getName());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getUserId());
            }

            void mapRow(ResultSet rs) throws SQLException {}
        };
        jdbcTemplate.update(UPDATEQUERY);
    }

    public User findByUserId(String userId) throws SQLException {
        JdbcTemplate selectJdbcTemplate = new JdbcTemplate() {
            void setValues(PreparedStatement ps) throws SQLException{
                ps.setString(1, userId);
            }

            Object mapRow(ResultSet rs) throws SQLException {
                return new User(
                        rs.getString(USERID),
                        rs.getString(PASSWORD),
                        rs.getString(NAME),
                        rs.getString(EMAIL)
                );
            }
        };

        return (User)selectJdbcTemplate.queryForObject(SELECTONEQUERY);
    }

    public List<User> findAll() throws SQLException {
        JdbcTemplate selectJdbcTemplate = new JdbcTemplate() {
            void setValues(PreparedStatement ps) throws SQLException {}

            Object mapRow(ResultSet rs) throws SQLException {
                return new User(
                        rs.getString(USERID),
                        rs.getString(PASSWORD),
                        rs.getString(NAME),
                        rs.getString(EMAIL)
                );
            }
        };
        return (List<User>) selectJdbcTemplate.query(SELECTALLQUERY);
    }
}
