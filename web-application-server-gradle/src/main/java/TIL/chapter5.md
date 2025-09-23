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
