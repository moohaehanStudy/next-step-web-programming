package controller;

import db.DataBase;
import model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ForwardController implements Controller {
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
                User currentUser = DataBase.findUserById(userId);
                req.setAttribute("user", currentUser);
            }
        }
        return forwardUrl;
    }
}
