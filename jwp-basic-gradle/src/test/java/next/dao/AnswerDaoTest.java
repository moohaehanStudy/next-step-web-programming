package next.dao;

import dao.AnswerDao;
import jdbc.ConnectionManager;
import model.Answer;
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

public class AnswerDaoTest {
    @BeforeEach
    public void setup() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("jwp.sql"));
        DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());
    }

    @Test
    public void crud() throws Exception {
        Answer expected = new Answer(1, "test", "Answer contents", Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()), 1L);
        AnswerDao answerDao = new AnswerDao();
        answerDao.insert(expected);
        Answer actual = answerDao.findById(expected.getAnswerId());
        assertEquals(expected, actual);
    }

    @Test
    public void findAll() throws Exception {
        AnswerDao answerDao = new AnswerDao();
        List<Answer> answers = answerDao.findAllByQuestionId(1L);
        assertEquals(answers.size(), 1);
    }

    @Test
    public void findById() throws Exception {
        AnswerDao answerDao = new AnswerDao();
        Answer answer = answerDao.findById(1L);
        assertEquals("test", answer.getWriter());
    }
}
