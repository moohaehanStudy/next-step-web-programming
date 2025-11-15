//    package next.dao;
//
//    import dao.UserDao;
//    import jdbc.ConnectionManager;
//    import model.User;
//    import org.junit.jupiter.api.BeforeEach;
//    import org.junit.jupiter.api.Test;
//    import org.springframework.core.io.ClassPathResource;
//    import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
//    import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
//
//    import java.util.List;
//
//    import static org.junit.jupiter.api.Assertions.assertEquals;
//
//    public class UserDaoTest {
//    @BeforeEach
//    public void setup() {
//        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
//        populator.addScript(new ClassPathResource("jwp.sql"));
//        DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());
//    }
//
//    @Test
//    public void crud() throws Exception {
//        User expected = new User("userId", "password", "name", "test@test.com");
//        UserDao userDao = new UserDao();
//        userDao.insert(expected);
//        User actual = userDao.findByUserId(expected.getUserId());
//        assertEquals(expected, actual);
//
//        expected.update(new User("userId", "password2", "name2", "test2@test.com"));
//        userDao.update(expected);
//        actual = userDao.findByUserId(expected.getUserId());
//        assertEquals(expected, actual);
//    }
//
//    @Test
//    public void findAll() throws Exception {
//        UserDao userDao = new UserDao();
//        List<User> users = userDao.findAll();
//        assertEquals(2, users.size());
//    }
//
//    @Test
//    public void findById() throws Exception {
//        UserDao userDao = new UserDao();
//        User user = userDao.findByUserId("soyun");
//
//        assertEquals("1234", user.getPassword());
//    }
//}
