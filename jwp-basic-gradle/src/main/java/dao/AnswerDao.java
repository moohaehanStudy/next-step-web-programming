package dao;

import exception.CustomException;
import jdbc.ConnectionManager;
import jdbc.KeyHolder;
import model.Answer;

import java.sql.*;
import java.util.List;

public class AnswerDao {
    private static final String INSERTQUERY = "INSERT INTO ANSWERS (writer, contents, createdDate, questionId) VALUES (?, ?, ?, ?)";
    private static final String SELECTQUERYBYANSWERID = "SELECT answerId, writer, contents, createdDate, questionId FROM ANSWERS WHERE answerId = ?";
    private static final String SELECTALLQUERYBYQUESTIONID = "SELECT answerId, writer, contents, createdDate FROM ANSWERS WHERE questionId = ?";

    public Answer insert(Answer answer) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        PreparedStatementSetter pss = ps -> {
            ps.setString(1, answer.getWriter());
            ps.setString(2, answer.getContents());
            ps.setTimestamp(3, new Timestamp(answer.getCreatedDate().getTime()));
            ps.setLong(4, answer.getQuestionId());
        };

        KeyHolder keyHolder = new KeyHolder();

        jdbcTemplate.update(INSERTQUERY, pss, keyHolder);
        return findById(keyHolder.getId());
    }

    public Answer findById(long answerId) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        PreparedStatementSetter pss = ps -> {
            ps.setLong(1, answerId);
        };

        RowMapper<Answer> rm = rs -> new Answer(
                rs.getLong("answerId"),
                rs.getString("writer"),
                rs.getString("contents"),
                rs.getTimestamp("createdDate"),
                rs.getLong("questionId")
        );

        return jdbcTemplate.queryForObject(SELECTQUERYBYANSWERID, pss, rm);
    }

    public List<Answer> findAllByQuestionId(long questionId) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        PreparedStatementSetter pss = ps -> {
            ps.setLong(1, questionId);
        };

        RowMapper<Answer> rm = rs -> new Answer(
                rs.getLong("answerId"),
                rs.getString("writer"),
                rs.getString("contents"),
                rs.getTimestamp("createdDate"),
                questionId
                );

        return jdbcTemplate.query(SELECTALLQUERYBYQUESTIONID, pss, rm);
    }
}
