package controller.user;

import controller.Controller;
import dao.UserDao;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginUserController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(LoginUserController.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String userId = req.getParameter("userId");
        String password = req.getParameter("password");
        
        UserDao userDao = new UserDao();
        User user = userDao.findByUserId(userId);

        if(user == null){
            return "redirect:/user/login_failed.jsp";
        } else {
            if(user.comparePassword(password)){
                HttpSession session = req.getSession();
                session.setAttribute("user", user);

                return "redirect:/home.jsp";
            } else {
                return "redirect:/user/login_failed.jsp";
            }
        }
    }
}
