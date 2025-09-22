```java
BufferedReader reader = new BufferedReader(new InputStreamReader(in));
⬇️
BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
```

위

- InputStreamReader의 기본 인코딩을 사용
- 같은 코드라도 실행 환경에 따라 다른 인코딩으로 읽을 수 있어 플랫폼 의존적임

아래

- 항상 UTF-8로 읽는다
- 플랫폼에 상관없이 결과가 동일
- 웹 서버에서 HTTP 요청/응답은 거의 항상 UTF-8이 표준이므로, 이 방식이 안전


**요청 메시지**

요청 라인(Request Line)

- “HTTP-메소드 URI HTTP-버전”으로 구성되어있음
- URI는 클라이언트가 서버에 유일하게 식별할 수 있는 요청 자원의 경로

요청 헤더(Request Header)

- <필드 이름>:<필드 값> 쌍으로 이루어져있음
- 만약 필드 이름 하나에, 여러 개의 필드값을 전달하고 싶으면 쉼표(,) 사용


**응답 메시지**

상태 라인(Status Line)

- “HTTP-버전 상태코드 응답구문”으로 구성되어있음
- 200 = 상태코드 값 성공


- /index.html 요청을 한 번 보냈는데 여러 개의 추가 요청이 발생하는 이유
    - 서버가 웹 페이지를 구성하는 모든 자원(HTML, CSS, JS 등)을 한번에 응답으로 보내지 않아서
    1. 웹 서버는 첫 번째로 /index.html 요청에 대한 응답에 HTML만 보냄
    2. 응답을 받은 브라우저가 HTML 내용을 분석해 자원을 다시 요청

**쿠키**

- HTTP는 요청을 보내고 응답을 받으면 클라이언트와 서버 간의 연결을 끊는다 = 무상태 프로토콜
- HTTP는 로그인과 같이 클라이언트의 행위를 기억하기 위한 목적으로 지원하는 것이 쿠키이다

1. 서버에서 로그인 요청을 받으면 로그인 성공/실패 여부에 따라 응답 헤더에 Set-Cookie 로 결과 값을 저장
2. 클라이언트는 응답 헤더에 Set-Cookie 가 존재할 경우 값을 읽어 서버에 보내는 요청 헤더의 Cookie 헤더 값으로 다시 전송

- 서버가 전달하는 쿠키 정보는 클라이언트에 저장해 관리하기에 보안 이슈가 있음. 이런 단점을 보완하기 위해 세션이 등장
- 세션 또한 쿠키를 기반으로 하지만, 상태 데이터를 서버에 저장한다