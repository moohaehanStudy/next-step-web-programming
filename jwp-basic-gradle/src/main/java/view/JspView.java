package view;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JspView implements View {
    public static final String REDIRECT = "redirect:";
    private String view;

    public JspView(String view) {
        this.view = view;
    }

    @Override
    public void render(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        if(view != null){
            if (view.startsWith(REDIRECT)) {
                String path = view.substring(REDIRECT.length());
                resp.sendRedirect(path);
            } else {
                RequestDispatcher dispatcher = req.getRequestDispatcher(view);
                dispatcher.forward(req, resp);
            }
        }
    }
}
