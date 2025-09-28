package servlet;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.SessionUserUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/user/list")
public class ListUserServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(ListUserServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        User value = SessionUserUtils.getUserFromSession(req.getSession());

        if(value != null){
            req.setAttribute("users", DataBase.findAll());

            RequestDispatcher dispatcher = req.getRequestDispatcher("/user/list.jsp");
            dispatcher.forward(req, resp);
        } else {
            log.error("로그인 한 회원만 목록을 볼 수 있습니다. -> listuserServlet");

            RequestDispatcher dispatcher = req.getRequestDispatcher("/user/login.jsp");
            dispatcher.forward(req, resp);
        }
    }
}
