package controller;

import dao.UserDao;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class ForwardController implements Controller {
    private final Logger log = LoggerFactory.getLogger(ForwardController.class);

    private String forwardUrl;

    public ForwardController(String forwardUrl) {
        this.forwardUrl = forwardUrl;
    }

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        if(forwardUrl.equals("/user/updateForm.jsp")) {
            Object user = req.getSession().getAttribute("user");

            if(user != null) {
                String userId = req.getParameter("userId");
                UserDao userDao = new UserDao();
                User currentUser = null;
                try {
                    currentUser = userDao.findByUserId(userId);
                } catch (SQLException e) {
                    log.error("사용자 조회 중 오류 발생: {}", e.getMessage());
                }
                req.setAttribute("user", currentUser);
            }
        }
        return forwardUrl;
    }
}
