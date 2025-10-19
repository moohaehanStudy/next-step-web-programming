package dao;

import model.Question;

import java.sql.SQLException;
import java.util.List;

public class QuestionDao {
    private static final String INSERTQUERY = "INSERT INTO QUESTIONS VALUES (?, ?, ?, ?)";
    private static final String SELECTQUERY = "SELECT * FROM QUESTIONS WHERE QUESTION_ID = ?";
    private static final String SELECTALLQUERY = "SELECT * FROM QUESTIONS";
    private static final String QUESTIONID = "questionId";
    private static final String WRITER = "writer";
    private static final String TITLE = "title";
    private static final String CONTENTS = "contents";
    private static final String CREATEDDATE = "createdDate";
    private static final String COUNTOFANSWER = "countOfAnswer";

    public void insert(Question question) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        PreparedStatementSetter pss = ps -> {
            ps.setString(1, question.getWriter());
            ps.setString(2, question.getTitle());
            ps.setString(3, question.getContents());
            ps.setDate(4, question.getTimestamp());
        };

        jdbcTemplate.update(INSERTQUERY, pss);
    }

    public List<Question> getQuestions() throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        PreparedStatementSetter pss = ps -> {};
        RowMapper<Question> rm = createRowMapper();

        return jdbcTemplate.query(SELECTALLQUERY, pss, rm);
    }

    public Question getQuestion(String questionId) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        PreparedStatementSetter pss = ps -> {
            ps.setString(1, questionId);
        };
        RowMapper<Question> rm = createRowMapper();

        return jdbcTemplate.queryForObject(SELECTQUERY, pss, rm);
    }

    private RowMapper<Question> createRowMapper() {
        RowMapper<Question> rm = rs -> new Question(
                rs.getLong(QUESTIONID),
                rs.getString(WRITER),
                rs.getString(TITLE),
                rs.getString(CONTENTS),
                rs.getDate(CREATEDDATE),
                rs.getInt(COUNTOFANSWER)
        );

        return rm;
    }
}
