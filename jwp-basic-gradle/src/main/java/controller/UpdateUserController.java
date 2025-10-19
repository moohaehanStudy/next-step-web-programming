package controller;

import dao.UserDao;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.SessionUserUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class UpdateUserController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(UpdateUserController.class);

    @Override
    public String execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        User value = SessionUserUtils.getUserFromSession(req.getSession());

        if(value == null){
            log.error("로그인 한 회원만 목록을 볼 수 있습니다 -> updateUserServlet");

            return "/index.jsp";
        } else{
            UserDao userDao = new UserDao();
            User user = null;
            try {
                user = userDao.findByUserId(req.getParameter("userId"));
            } catch (SQLException e) {
                log.error("사용자 조회 중 오류 발생: {}", e.getMessage());
                return "redirect:/user/list";
            }

            if(SessionUserUtils.isSameUser(req.getSession(), user)){
                User updatedUser = new User(
                        req.getParameter("userId"),
                        req.getParameter("password"),
                        req.getParameter("name"),
                        req.getParameter("email")
                );

                userDao.update(updatedUser);

                return "redirect:/user/list";
            } else{
                log.error("자기 자신의 정보만 수정할 수 있습니다");

                return "redirect:/user/list";
            }
        }
    }
}
