package servlet;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(value = { "/user/update", "/user/updateForm" })
public class UpdateUserServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(UpdateUserServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        req.setAttribute("user", DataBase.findUserById(req.getParameter("userId")));

        RequestDispatcher dispatcher = req.getRequestDispatcher("/user/update.jsp");
        dispatcher.forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html;charset=UTF-8");

        HttpSession session = req.getSession();
        Object value = session.getAttribute("user");

        if(value == null){
            log.error("로그인 한 회원만 목록을 볼 수 있습니다 -> updateUserServlet");
            RequestDispatcher dispatcher = req.getRequestDispatcher("/index");
            dispatcher.forward(req, resp);
        } else{
            User sessionUser = (User)value;

            if(sessionUser.getUserId().equals(req.getParameter("userId"))){
                User user = DataBase.findUserById(req.getParameter("userId"));
                user.update(new User(
                        req.getParameter("userId"),
                        req.getParameter("password"),
                        req.getParameter("name"),
                        req.getParameter("email"))
                );

                resp.sendRedirect("/user/list");
            } else{
                log.error("자기 자신의 정보만 수정할 수 있습니다");

                RequestDispatcher dispatcher = req.getRequestDispatcher("/user/list");
                dispatcher.forward(req, resp);            }
        }
    }
}
