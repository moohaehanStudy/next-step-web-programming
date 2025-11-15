package controller.qna;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.Controller;
import dao.AnswerDao;
import model.Answer;
import model.Result;
import view.JsonView;
import view.ModelAndView;
import view.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class DeleteAnswerController implements Controller {

    @Override
    public ModelAndView execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Long answerId = Long.parseLong(req.getParameter("answerId"));

        AnswerDao answerDao = new AnswerDao();
        answerDao.delete(answerId);

        ModelAndView mav = new ModelAndView(new JsonView());
        mav.addObject("success", true);

        return mav;
    }
}
