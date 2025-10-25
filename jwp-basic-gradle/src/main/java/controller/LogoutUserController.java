package controller;

import util.SessionUserUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LogoutUserController implements Controller {
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if(SessionUserUtils.isLoggedIn(req.getSession())){
            HttpSession session = req.getSession();
            session.removeAttribute("user");

            return "redirect:/home.jsp";
        } else{
            return "redirect:/user/login.jsp";
        }
    }
}
