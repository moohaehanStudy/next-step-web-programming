package dao;

import model.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class QuestionDao {
    private static final Logger log = LoggerFactory.getLogger(QuestionDao.class);

    private static final String SELECTQUERY = "SELECT questionId, writer, title, contents, createdDate, countOfAnswer FROM QUESTIONS WHERE questionId = ?";
    private static final String SELECTALLQUERY = "SELECT questionId, writer, title, createdDate, countOfAnswer FROM QUESTIONS "
            + "order by questionId desc";
    private static final String QUESTIONID = "questionId";
    private static final String WRITER = "writer";
    private static final String TITLE = "title";
    private static final String CONTENTS = "contents";
    private static final String CREATEDDATE = "createdDate";
    private static final String COUNTOFANSWER = "countOfAnswer";

    public List<Question> findAll() throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        PreparedStatementSetter pss = ps -> {};
        RowMapper<Question> rm = rs -> new Question(
                rs.getLong("questionId"),
                rs.getString("writer"),
                rs.getString("title"),
                null,
                rs.getTimestamp("createdDate"),
                rs.getInt("countOfAnswer")
        );

        return jdbcTemplate.query(SELECTALLQUERY, pss, rm);
    }

    public Question findById(Long questionId) throws SQLException {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        PreparedStatementSetter pss = ps -> {
            ps.setLong(1, questionId);
        };

        RowMapper<Question> rm = rs -> new Question(
                rs.getLong(QUESTIONID),
                rs.getString(WRITER),
                rs.getString(TITLE),
                rs.getString(CONTENTS),
                rs.getTimestamp(CREATEDDATE),
                rs.getInt(COUNTOFANSWER)
        );

        return jdbcTemplate.queryForObject(SELECTQUERY, pss, rm);
    }
}
