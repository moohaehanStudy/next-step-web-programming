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
- 그래서 나온 것이 **JSTL(JavaServer Pages Standard Library) 와 EL(Expression Language)**이다
  - JPS의 복잡도를 낮춰 유지보수를 쉽게 하자는 목적으로 MVC 패턴을 적용한 프레임워크
  - JSTL과 EL을 활용하면 JSP에서 자바 구문을 완전히 제거할 수 있다

