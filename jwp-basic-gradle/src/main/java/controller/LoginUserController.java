package controller;

import db.DataBase;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginUserController implements Controller {
    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = DataBase.findUserById(req.getParameter("userId"));

        if(user == null){
            return "redirect:/user/login_failed.jsp";
        } else {
            if(user.comparePassword(req.getParameter("password"))){
                HttpSession session = req.getSession();
                session.setAttribute("user", user);

                return "redirect:/index.jsp";
            } else {
                return "redirect:/user/login_failed.jsp";
            }
        }
    }
}
