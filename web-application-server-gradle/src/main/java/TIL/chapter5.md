### 1딘계

- 이 부분은 이미 어느정도 HttpRequest에서 구현되어 있었음
```java
public String getHeader(String key) {
    return headers.get(key);
}

public String getParam(String key) {
    return params.get(key);
}
```
```java
if(method.equals("GET")) {
    params = HttpRequestUtils.parseQueryString(queryString);
} else if(method.equals("POST")) {
    params = HttpRequestUtils.parseQueryString(body);
}
```
- `RequestHandler`를 최대한 짧게 구현하고 싶어서 메서드 타입별로 parse하는 부분을 `HttpRequest`로 옮김


### 2단계
- 응답데이터 처리의 중복을 제거하기 위해 `HttpResponse` 클래스를 만들기
```java
HttpResponse response = new HttpResponse(out);
```
- run() 메서드에서 reponse 객체 선언 후
```java
response.sendRedirect("/index.html");
response.response200Header(sb.length(), url);
response.responseBody(sb.toString().getBytes());
```
- 이런식으로 처리해줌

트러블 슈팅
1. `Failed to load resource: net::ERR_CONTENT_LENGTH_MISMATCH`
   2. 이 오류는 브라우저가 서버로부터 응답을 받는 과정에서 Content-Length 헤더 값과 실제 응답 바디 길이가 일치하지 않을 때 발생
- 왜 일어났나 하고 보니, processHeader() 메서드에서 header와 body 사이에 `\r\n`을 넣어주지 않아서 발생하던 에러였음
- 넣어주니 잘 작동함

2. 로그인 후 쿠키 세팅이 안되던 문제 발생
- 왜 일어났냐?
```java
response.sendRedirect("/user/login.html");
response.addHeader("Set-Cookie", "logined=failed; Path=/");
```
- 이런식으로 redirect를 먼저하고 header에 값을 추가했기 떄문,, 순서를 다시 바꾸니까 잘 들어가는 거 확인!

지금까지 2단계까지 했는데 고민점
- run()에서 쿠키 값을 넣어주는 것이 맞는가? 차라리 302Header를 HttpResponse에 만들어서 한번에 처리하는 것이 맞지 않을까? 하는 생각이 든다
- HttpResponse도 중복이 꽤나 있는 거 같아서 좀 더 짧게 가능한지를 고민해봐야할듯하다


### 3단계
```java
    private static final Map<String, Controller> controllerMap;

    static {
        controllerMap = new HashMap<>();
        controllerMap.put("/user/create", new CreateUserController());
        controllerMap.put("/user/list", new ListUserController());
        controllerMap.put("/user/login", new LoginController());
    }
```
- 필요한 URL와 그에 알맞은 `controller`를 미리 매핑해두기
```java
String requestPath = request.getRequestPath();
Controller controller = controllerMap.get(requestPath);
            
if(controller != null) {
    controller.service(request, response);
} else{
    log.debug("아직 지원하지 않는 URL입니다.{}", requestPath);
}
response.forward(requestPath);
```
- `requestPath` 찾아서 그에 알맞은 `controller.service()` 호출

```java
public class AbstractController implements Controller {

    @Override
    public void service(HttpRequest request, HttpResponse response) {

        if(request.getMethod().equals("GET")){
            doGet(request, response);
        } else if(request.getMethod().equals("POST")){
            doPost(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {}
    protected void doGet(HttpRequest request, HttpResponse response) {}
}
```
- protected는 private과 유사하지만, 상속받는 자식 클래스에서 사용을 할 수 있도록 해준다는 점에서 private과 차이가 있다.
- 즉, 변수를 보호하고자 하면서도, 상속클래스에서 사용하기를 원하면,
- 굳이 private 선언 후 getter&setter를 사용하여 가져오지 말고, 변수를 protected로 선언해주면 된다.
[출처] [java 강의] 상속 / super / protected 접근 제어자 / object class|작성자 Yooni
- 근데 찾아보니까 같은 패키지 내에 있으면 상관없다는 걸 보니,, 나에게는 해당이 안되는듯? 그래도 나중에 변경이 될지도 모르니 protected로 구현!

### 4단계
```java
package http;

public enum HttpMethod {
    GET,
    POST;
}
```
- 하드코딩은 좋지 않으니 (ex: `"GET".equals(reqeust.getMethod()`) 등) **`enum`** 사용하기

### 5단계
요청 라인 parsing을 따로 클래스로 빼기 -> HttpRequest에서 요청 라인을 처리하기에는 하는 일이 너무 많아짐
```java
requestLine = new RequestLine(line);
```
```java
public class RequestLine {
    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

    private HttpMethod method;
    private String url;
    private Map<String, String> params = new HashMap<>();

    public RequestLine(String requestLine) {
        log.debug("request line: {}", requestLine);

        String[] firstTokens = requestLine.split(" ");

        if (firstTokens.length != 3) {
            throw new IllegalArgumentException(requestLine + "이 형식에 맞지 않습니다.");
        }

        method = HttpMethod.valueOf(firstTokens[0]);
        if (method == HttpMethod.POST) {
            url = firstTokens[1];
            log.debug("url: {}", url);
            return;
        }

        int index = firstTokens[1].indexOf("?");

        if (index == -1) {
            url = firstTokens[1];

        } else {
            url = firstTokens[1].substring(0, index);
            params = HttpRequestUtils.parseQueryString(firstTokens[1].substring(index + 1));
        }

        log.debug("url: {}", url);
    }
}
```
- 4단계에서 만든 enum 값 활용을 볼 수 있음


### 해야할 거
- cookie 키 값이 중복으로 들어올 때의 처리
  - 원래 있던 키 찾아서 지우고, 새로 들어온걸로 처리로 해결완료

### 5.2 웹 서버 리팩토링 구현 및 설명
#### 테스트 코드를 기반으로 개발할 경우의 효과
- 클래스에 버그가 있는지를 빨리 찾아 구현할 수 있음
- 디버깅하기 쉽다
- 테스트 코드가 있기 때문에 마음 놓고 리팩토링을 할 수 있다는 것

- 클래스에 대한 단위 테스트를 하는 것은 결과적으로 디버깅을 좀 더 쉽고 빠르게 할 수 있기 때문에 개발 생산성을 높여준다

#### 어떻게해야 리팩토링을 잘하게될까?
- 프로그래밍 경험이 많지 않은데 어떻게 객체의 책임을 분리하고 좋은 설계를 하기가 쉽지 않음
- 객체지향 설계를 잘하려면 많은 연습, 경험, 고민이 필요하다
- 경험이 많지 않은 상태에서는 일단 새로운 객체를 추가했으면 객체를 최대한 활용하기 위해 노력해 본다
- 객체를 활용하는 연습을 하기 위해서는
  - 객체에서 값을 꺼낸 후 로직을 구현하려고 하지 말고, 값을 가지고 있는 객체에 메시지를 보내 일을 시키도록 연습해보자


