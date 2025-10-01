package controller;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.SessionUserUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UpdateUserController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(UpdateUserController.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User value = SessionUserUtils.getUserFromSession(req.getSession());

        if(value == null){
            log.error("로그인 한 회원만 목록을 볼 수 있습니다 -> updateUserServlet");

            return "/index.jsp";
        } else{
            User user = DataBase.findUserById(req.getParameter("userId"));

            if(SessionUserUtils.isSameUser(req.getSession(), user)){
                user.update(new User(
                        req.getParameter("userId"),
                        req.getParameter("password"),
                        req.getParameter("name"),
                        req.getParameter("email"))
                );

                return "redirect:/user/list";
            } else{
                log.error("자기 자신의 정보만 수정할 수 있습니다");

                return "redirect:/user/list";
            }
        }
    }
}
