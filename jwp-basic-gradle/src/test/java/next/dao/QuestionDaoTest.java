package next.dao;

import dao.QuestionDao;
import jdbc.ConnectionManager;
import model.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuestionDaoTest {
    @BeforeEach
    public void setup() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("jwp.sql"));
        DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());
    }

    @Test
    public void crud() throws Exception {
        Question expected = new Question(1, "test", "Question title", "QUestion contents", Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()), 0);
        QuestionDao questionDao = new QuestionDao();
        questionDao.findById(1L);
        Question actual = questionDao.findById(expected.getQuestionId());
        assertEquals(expected, actual);
    }

    @Test
    public void findAll() throws Exception {
        QuestionDao questionDao = new QuestionDao();
        List<Question> questions = questionDao.findAll();
        assertEquals(1, questions.size());
    }

    @Test
    public void findById() throws Exception {
        QuestionDao questionDao = new QuestionDao();
        Question question = questionDao.findById(1L);
        assertEquals("test", question.getWriter());
    }
}
