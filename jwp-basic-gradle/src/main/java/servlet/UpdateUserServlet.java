package servlet;

import db.DataBase;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/user/update")
public class UpdateUserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        User user = DataBase.findUserById(req.getParameter("userId"));
        user.update(new User(req.getParameter("userId"),  req.getParameter("password"), req.getParameter("name"), req.getParameter("email")));

        resp.sendRedirect("/user/list");
    }
}
