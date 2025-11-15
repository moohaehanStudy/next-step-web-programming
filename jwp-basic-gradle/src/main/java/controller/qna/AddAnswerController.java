package controller.qna;

import com.fasterxml.jackson.databind.ObjectMapper;
import controller.Controller;
import dao.AnswerDao;
import model.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.JsonView;
import view.ModelAndView;
import view.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class AddAnswerController implements Controller {
    private static final Logger log = LoggerFactory.getLogger(AddAnswerController.class);

    @Override
    public ModelAndView execute(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Answer answer = new Answer(
                req.getParameter("writer"),
                req.getParameter("contents"),
                Long.parseLong(req.getParameter("questionId"))
        );

        AnswerDao answerDao = new AnswerDao();
        answerDao.insert(answer);

        ModelAndView mav = new ModelAndView(new JsonView());
        mav.addObject("success", true);

        return mav;
    }
}
