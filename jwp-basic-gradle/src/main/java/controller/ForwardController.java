package controller;

import dao.UserDao;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.JspView;
import view.View;

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
    public View execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        if(forwardUrl.equals("/user/updateForm.jsp")) {
            Object user = req.getSession().getAttribute("user");

            if(user != null) {
                String userId = req.getParameter("userId");
                UserDao userDao = new UserDao();
                User currentUser = userDao.findByUserId(userId);

                req.setAttribute("user", currentUser);
            }
        }
        return new JspView(forwardUrl);
    }
}
