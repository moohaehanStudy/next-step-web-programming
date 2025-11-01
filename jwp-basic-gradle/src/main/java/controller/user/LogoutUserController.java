package controller.user;

import controller.Controller;
import util.SessionUserUtils;
import view.JspView;
import view.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LogoutUserController implements Controller {
    @Override
    public View execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if(SessionUserUtils.isLoggedIn(req.getSession())){
            HttpSession session = req.getSession();
            session.removeAttribute("user");

            return new JspView("redirect:/home.jsp");
        } else{
            return new JspView("redirect:/user/login.jsp");
        }
    }
}
