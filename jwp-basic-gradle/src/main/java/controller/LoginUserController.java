package controller;

import dao.UserDao;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class LoginUserController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(LoginUserController.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = req.getParameter("userId");
        String password = req.getParameter("password");
        
        UserDao userDao = new UserDao();
        User user = null;
        
        try {
            user = userDao.findByUserId(userId);
        } catch (SQLException e) {
            log.error("사용자 조회 중 오류 발생: {}", e.getMessage());
            return "redirect:/user/login_failed.jsp";
        }

        if(user == null){
            return "redirect:/user/login_failed.jsp";
        } else {
            if(user.comparePassword(password)){
                HttpSession session = req.getSession();
                session.setAttribute("user", user);

                return "redirect:/index.jsp";
            } else {
                return "redirect:/user/login_failed.jsp";
            }
        }
    }
}
