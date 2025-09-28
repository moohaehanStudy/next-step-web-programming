```java
req.setAttribute("users", DataBase.findAll());
```
- JSP에 "users"라는 이름으로 전달
- 서블릿도 동적으로 HTML을 생성하려면 StringBuilder를 통해 하나한 구현해줘야함
- 이 같은 서블릿의 한계를 극복하기 위해 등장한 것이 **JSP**이다

#### JSP(Java Server Page)
- JSP는 정적인 HTML은 그대로 두고 동적으로 변경되는 부분만 JSP 구문을 활용해 프로그래밍으로 구현하면 된다
- 자바 구문을 그대로 사용할 수 있다
- 스크립틀릿(scriptlet) 이라고 하는 <% %> 내에 자바 구문을 그대로 사용할 수 있게 되었다
- 근데 많은 로직이 JSP에 자바 코드로 구현되다보니 JSP를 유지보수하기 힘들어졌다
- 그래서 나온 것이 **JSTL(JavaServer Pages Standard Library) 와 EL(Expression Language)** 이다
  - JPS의 복잡도를 낮춰 유지보수를 쉽게 하자는 목적으로 MVC 패턴을 적용한 프레임워크
  - JSTL과 EL을 활용하면 JSP에서 자바 구문을 완전히 제거할 수 있다

#### 고민
1. UserUpdateServlet과 UserUpdateFormServlet을 나누는 것이 맞는가?
```java
@WebServlet(value = { "/user/update", "/user/updateForm" })
```
- 이렇게 하나의 서블릿에 여러개 uri를 걸 수 있다! 
- 근데 이렇게 하면 1개의 doGet(), 1개의 doPost()만 가능한듯? 같은 uri로 다른 post 동작을 하게는 불가능

2. "/" HomeServlet 만들기
```java
@WebServlet("")
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        RequestDispatcher dispatcher = req.getRequestDispatcher("/index.jsp");
        dispatcher.forward(req, resp);
    }
}
```
- `@WebServlet("/")`로 처음에 만들었는데 `/`가 아닌 그냥 `""`로 해야 작동!

3. session에서 값 가져오는 거 클래스로 만들기
4. Exception을 만들어야할까?
5. 같은 이메일로 중복회원가입을 막을까?
6. `redirect("/user/list")` vs `redirect("/user/list.jsp")`
   - 후자로 하니까 세션을 못읽어와서 유저 목록이 출력 안되는 문제 발생
   - 찾아보니 JSP 파일에 직접 접근하게 되면 서블릿을 거치지 않기 때문에 세션체크 로직이 돌아가지 않아서 그런거였음
7. RequestDispatcher로 forward 하는 것과 그냥 redirect의 차이는 무엇인가?