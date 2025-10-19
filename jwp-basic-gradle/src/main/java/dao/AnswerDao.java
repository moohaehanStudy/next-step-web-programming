package dao;

import model.Answer;

import java.sql.SQLException;
import java.util.List;

public class AnswerDao {
    private static final String INSERTQUERY = "INSERT INTO QUESTIONS (writer, contents, createdDate, questionId) VALUES (?, ?, ?, ?)";
    private static final String SELECTQUERYBYANSWERID = "SELECT * FROM QUESTIONS WHERE answerId = ?";
    private static final String SELECTALLQUERYBYQUESTIONID = "SELECT * FROM QUESTIONS WHERE questionId = ?";

    public void insert(Answer answer) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        PreparedStatementSetter pss = ps -> {
            ps.setString(1, answer.getWriter());
            ps.setString(2, answer.getContents());
            ps.setTimestamp(3, answer.getTimestamp());
            ps.setLong(4, answer.getQuestionId());
        };

        jdbcTemplate.update(INSERTQUERY, pss);
    }

    public Answer findByAnswerId(long answerId) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        PreparedStatementSetter pss = ps -> {
            ps.setLong(1, answerId);
        };

        RowMapper<Answer> rm = createRowMapper();

        return jdbcTemplate.queryForObject(SELECTQUERYBYANSWERID, pss, rm);
    }

    private RowMapper<Answer> createRowMapper() {
        RowMapper<Answer> rm = rs -> new Answer(
                rs.getString("writer"),
                rs.getString("contents"),
                rs.getLong("questionId")
        );

        return rm;
    }

    private List<Answer> findAll(long questionId) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        PreparedStatementSetter pss = ps -> {
            ps.setLong(1, questionId);
        };
        RowMapper<Answer> rm = createRowMapper();

        return jdbcTemplate.query(SELECTALLQUERYBYQUESTIONID, pss, rm);
    }
}
