### 요구사항 1 - index.html 읽기

1단계

```java
BufferedReader reader = new BufferedReader(new InputStreamReader(in));
```

```java
StringBuilder sb = new StringBuilder();
String line;
```

```java
while(!"".equals(line = reader.readLine())) {
	sb.append(line);

	if(line == null) return;
}

log.info("Request: {}", sb);
```

21:20:47.478 [INFO ] [Thread-0] [webserver.RequestHandler] - GET / HTTP1.1Request: Host: localhost:8080Connection: keep-aliveCache-Control: max-age=0sec-ch-ua: "Not)A;Brand";v="8", "Chromium";v="138", "Google Chrome";v="138"sec-ch-ua-mobile: ?0sec-ch-ua-platform: "macOS"Upgrade-Insecure-Requests: 1User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7Sec-Fetch-Site: noneSec-Fetch-Mode: navigateSec-Fetch-User: ?1Sec-Fetch-Dest: documentAccept-Encoding: gzip, deflate, br, zstdAccept-Language: en-CA,en;q=0.9,ko-KR;q=0.8,ko;q=0.7,en-US;q=0.6Cookie: grafana_session=e11bc71953c4a77b5f0d8376919245ec; grafana_session_expiry=1756700358

이런식으로 쭉 읽어와 Log에 찍힘

2단계

```java
HttpRequest request = new HttpRequest(reader);

String url = request.getUrl();

log.debug("URL : {}", url);
```

```java
public class HttpRequest {
    private String url;
    private String requestPath;

    public HttpRequest (BufferedReader b) throws IOException {
        String firstLine =  b.readLine();
        String[] firstTokens = firstLine.split(" ");
        String url = firstTokens[1];

        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
```

이미 클래스가 있길래 활용!

HttpReqeust에 BufferReader를 인자로 넘겨주고 첫줄을 split으로 나눈다

그러면 firstTokens[0] = GET firstTokens[1] = /index.html firstTokens[2] = HTTP 1.1 이런식이니 url인 firstTokens[1]을 url에 저장후 return 해주는 getUrl() 생성

21:20:47.478 [DEBUG] [Thread-0] [webserver.RequestHandler] - URL : /index.html

그럼 이렇게 로그에 찍힌다

3단계

```java
byte[] bytes = Files.readAllBytes(new File("./webapp" + url).toPath());

log.info("Response : {}", new String(bytes));
```

여기서 url은 /index.html 이기에 폴더에 들어가서 파일을 가져오고 그걸 로그에 찍는다

```java
byte[] bytes = Files.readAllBytes(new File("./webapp" + url).toPath());

log.info("Response : {}", new String(bytes));

// TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
DataOutputStream dos = new DataOutputStream(out);
response200Header(dos, bytes.length);
responseBody(dos, bytes);
```

- 이렇게 찍어서 body로 보내면 `localhost:8080/index.html` 에 들어갔을 때 html을 읽어온다

### 요구사항 2 Get 회원가입

```java
public class HttpRequest {
    private String url;
    private String requestPath;
    private Map<String, String> params = new HashMap<>();

    public HttpRequest (BufferedReader b) throws IOException {
        String firstLine =  b.readLine();
        String[] firstTokens = firstLine.split(" ");
        String url = firstTokens[1];

        log.debug("URL: " + url);

        this.url = url;

        int index = url.indexOf("?");

        if(index == -1) {
            log.debug("? 없음");
        } else{
            this.requestPath = url.substring(0, index);

            String queryString = url.substring(index + 1);

            params = HttpRequestUtils.parseQueryString(queryString);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
```

url = `/user/create?userId=soyun ...`

requestPath = `/user/create`

params = `(userId, soyun), (password, 1234)…`

```java
HttpRequest request = new HttpRequest(reader);

String url = request.getUrl();
Map<String, String> params = request.getParams();
```

이렇게 값을 가져온다음

```java
if(url.startsWith("/user/create")){
	User user = User.builder()
		  .userId(params.get("userId"))
	    .password(params.get("password"))
      .name(params.get("name"))
      .email(params.get("email"))
      .build();

	log.debug("User : {}", user);
}
```

user 생성하기

그러면 `16:14:08.585 [DEBUG] [Thread-17] [webserver.RequestHandler] - User : User [userId=test, password=1234, name=test, email=test%[40test.com](http://40test.com/)]` 이렇게 User가 생성된다

+) `decode`를 추가해야 할듯!

### 요구사항 3 Post 회원가입

```java
    public HttpRequest (BufferedReader b) throws IOException {
        String line =  b.readLine();
        String[] firstTokens = line.split(" ");
        String url = firstTokens[1];
        method = firstTokens[0];

        log.debug("URL: " + url);

        this.url = url;

        int index = url.indexOf("?");

        if(index == -1) {
            log.debug("? 없음");
        } else{
            this.requestPath = url.substring(0, index);

            queryString = url.substring(index + 1);
        }

        //요청 헤더 읽기
        while ((line = b.readLine()) != null && !line.isEmpty()) {
            String [] tokens = line.split(":");

            headers.put(tokens[0].trim(), tokens[1].trim());
        }

        //body 읽기
        if(headers.containsKey("Content-Length")){
            int length = Integer.parseInt(headers.get("Content-Length"));
            body = IOUtils.readData(b, length);
        }
    }
```

- Header 읽기 (빈줄이 나올때까지) headers Map에 넣기
- body 읽기, 만약에 headers에 Content-Length가 있으면 바디가 있다는 뜻이니까
    - 제공되는 IOUtils 클래스를 사용해서 String body 만들기

```java
else if(url.startsWith("/user/create") && request.getMethod().equals("POST")) {
	String body =  request.getBody();

	Map<String, String> bodyToken = HttpRequestUtils.parseQueryString(body);

	User user = User.builder()
		.userId(bodyToken.get("userId"))
    .password(bodyToken.get("password"))
    .name(bodyToken.get("name"))
    .email(bodyToken.get("email"))
    .build();

    log.debug("User : {} with Method : {}", user, request.getMethod());

}
```

- 가져온 body로 HttpRequestUtils 사용해서 Map에 저장
- 그걸로 User 생성
- `17:11:50.498 [DEBUG] [Thread-15] [webserver.RequestHandler] - User : User [userId=test, password=1, name=1, email=1234%[401234.com](http://401234.com/)] with Method : GET`
- `17:15:27.528 [DEBUG] [Thread-13] [webserver.RequestHandler] - User : User [userId=1, password=11, name=1, email=test%[40test.com](http://40test.com/)] with Method : POST`
- 둘 다 잘 되는 모습!

### 요구사항 4 회원가입 후 index.html 리다이렉트

```java
private void response302Header(DataOutputStream dos, int lengthOfBodyContent, String location) {
	try{
			dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
	    dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
	    dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
	    dos.writeBytes("Location: " + location + "\r\n");
	    dos.writeBytes("\r\n");
  } catch (IOException e) {
	    log.error(e.getMessage());
  }
}
```

- HTTP 302 = URL 리다이렉션을 수행하는 일반적인 방법
- Location 이라는 필드값을 넣음
- 필수 헤더:
    - Location: 클라이언트가 리다이렉트 할 새 URL

    ```java
    HTTP/1.1 302 Found
    Location: /index.html
    Content-Length: 0
    ```

- 권장 헤더(필수는 아님)
    - Content-Length: 보통 0 (Body가 없으면)
    - Content-Type: Body가 있으면 지정

### 요구사항 5 로그인 Set Cookie

```java
private void response302HeaderWithCookie(DataOutputStream dos, String location, String cookie) {
	try{
		dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
    //Path=/ 를 붙여야 브라우저가 모든 URL 요청 시 cookie값을 보냄
    //없으면 저장되는 범위가 현재 요청한 URL과 동일한 경로에만 유효할 수 있음
    dos.writeBytes("Set-Cookie: " + cookie + "; Path=/\r\n");
    dos.writeBytes("Location: " + location + "\r\n");
    dos.writeBytes("\r\n");
	} catch (IOException e) {
    log.error(e.getMessage());
  }
}
```

- 처음 요청할 때는 cookie에 값이 잘 들어갔는디 새로고침하거나 index.html로 돌아가면 값이 없어지길래 왜 그러지 하고 보니 Path를 적용해줘야한다고 함

**Cookie 필드 속성**

- NAME=VALUE
    - 쿠키의 이름과 값
    - 서버가 Set-Cookie 헤더로 클라이언트에게 보낼 때 반드시 포함
- Expires=DATE
    - 쿠키의 만료일을 지정
    - 이 날짜 이후에는 브라우저가 쿠키를 삭제
    - 지정하지 않으면 세션 쿠키로 취급되어 브라우저를 종료하면 사라짐
- Path=PATH
    - 쿠키가 전송될 URL 경로를 지정
    - 해당 경로와 그 하위 경로에서만 쿠키가 브라우저에서 전송됨
    - ex) Path=/account 면 /account/* 요청에만 쿠키가 전송
- Domain=도메인명
    - 쿠키가 적용될 도메인을 지정
    - ex) Domain=example.com 이면 www.example.com, [api.example.com](http://api.example.com) 등 서브 도메인까지 쿠키 전송
    - 지정하지 않으면 현재 요청한 도메인에만 전송
- Secure
    - HTTPS 요청에서만 쿠키가 전송
    - 민감한 세션 쿠키에 주로 사용
- HttpOnly
    - JavaScript의 document.cookie로 접근할 수 없음
    - 세션 쿠키가 탈취되는 것을 막기 위해 사용
- SameSite
    - 쿠키가 크로스 사이트 요청에 포함될 지 결정
    - Strict - 완전 차단(다른 사이트에서 링크 클릭해도 안 보냄)
    - Lax - 안전한 메서드(GET, POST)만 허용
    - None - 모든 크로스 사이트 요청에 전송(단, Secure 필수)

```java
User user = DataBase.findUserById(bodyToken.get("userId"));

if(user == null){
	log.warn("User not found : {}", bodyToken.get("userId"));
	response302HeaderWithCookie(dos, "/user/login_failed.html", "logined=failed");
	
	return;
}else {
	if (user.getPassword().equals(bodyToken.get("password"))) {
	log.debug("Login Success : {}", user.getUserId());
	response302HeaderWithCookie(dos, "/index.html", "logined=true");
	
	return;
} else {
	log.debug("Login Failed : {}", user.getUserId());
	response302HeaderWithCookie(dos, "/user/login_failed.html", "logined=failed");
	
	return;
	}
}
```

- User를 찾았을 때 null이면(회원가입도 안한 userId) → login_failed.html + logined=failed
- User를 찾았지만 비밀번호가 다를 때 → login_failed.html + logined=failed
- User도 찾고 비밀번호도 같을 때 → index.html + logined=true

### 요구사항 6 사용자 목록 출력

```java
Map<String, String> headerToken = new HashMap<>();

else if(url.equals("/user/list")){
	String cookie = request.getHeaders().get("Cookie");

  headerToken = HttpRequestUtils.parseCookies(cookie);

  StringBuilder sb = new StringBuilder();

  if(headerToken.containsKey("logined") && headerToken.get("logined").equals("true")) {
	  Collection<User> users = DataBase.findAll();

    sb.append("<html>");
    sb.append("<head><meta charset=\"UTF-8\"><title>Users</title></head>");
    sb.append("<body>");
    sb.append("<h2>사용자 목록</h2>");
    sb.append("<table border='1'>");
    sb.append("<tr><th>userId</th><th>name</th><th>email</th></tr>");

    for (User u : users) {
	    sb.append("<tr>");
      sb.append("<td>").append(u.getUserId()).append("</td>");
      sb.append("<td>").append(u.getName()).append("</td>");
      sb.append("<td>").append(u.getEmail()).append("</td>");
      sb.append("</tr>");
     }

     sb.append("</table>");
     sb.append("</body>");
     sb.append("</html>");

     response200Header(dos, sb.length());
     responseBody(dos, sb.toString().getBytes());
     }else if(!headerToken.containsKey("logined") || headerToken.get("logined").equals("failed")) {
	     response302HeaderWithCookie(dos, "/user/login.html", "logined=failed");
     }
}
```

- request의 header에서 cookie 값을 가져오고, HttpRequestUtils 클래스의 parseCookies를 사용해서 나눠줌
- map에 logined라는 key가 있고, 그 값이 true면 users 가져와서 table 만들어서 보여주기
- 만약 logined라는 key가 없거나 그 값이 failed면 로그인이 안되어있다는 거니까 login.html로 리다이렉트 하면서 cookie 값도 설정해주기

### 요구사항 7 CSS 적용하기

```java
private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String url) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: "+ getContentType(url) + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getContentType(String url) {
        String type = "text/html;charset=UTF-8";

        if(url.endsWith(".css"))
            type = "text/css";

        return type;
    }
```

- response200Header에 인자로 url 넘겨주면서, url의 형식마다 내려주는 Content-Type을 다르게 주기 + 항상 끝에 “\r\n” 까먹지 않기!
