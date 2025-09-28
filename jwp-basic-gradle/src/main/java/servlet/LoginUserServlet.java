package servlet;

import db.DataBase;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/user/login")
public class LoginUserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
           resp.sendRedirect("/user/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        req.setCharacterEncoding("utf-8");
        res.setContentType("text/html;charset=UTF-8");

        User user = DataBase.findUserById(req.getParameter("userId"));

        if(user == null){
            res.sendRedirect("/user/login_failed.html");
        } else {
            if(user.getUserId().equals(req.getParameter("userId"))){
                HttpSession session = req.getSession();
                session.setAttribute("user", user);
                res.sendRedirect("/index.jsp");
            } else {
                res.sendRedirect("/user/login_failed.html");
            }
        }
    }
}
