package http;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestTest {
    private String testDirectory = "./src/test/resources/";

    @Test
    public void request_GET() throws Exception{
        InputStream in = new FileInputStream(new File(testDirectory+"Http_GET.txt"));

        HttpRequest httpRequest = new HttpRequest(in);

        assertEquals("GET", httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getUrl());
        assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("soyun", httpRequest.getParam("userId"));
    }

    @Test
    public void request_POST() throws Exception{
        InputStream in = new FileInputStream(new File(testDirectory+ "Http_POST.txt"));

        HttpRequest httpRequest = new HttpRequest(in);

        assertEquals("POST", httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getUrl());
        assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("soyun", httpRequest.getParam("userId"));
    }

    @Test
    public void responseForward() throws Exception{
        // Http_Forward.txt 결과는 응답 body에 index.html이 포함되어야한다
        HttpResponse  httpResponse = new HttpResponse(createOutputStream("Http_Forward.txt"));
        httpResponse.forward("/index.html");
    }

    @Test
    public void responseRedirect() throws Exception{
        //Http_Redirect.txt 결과는 응답 Header에
        //Location 정보가 /index.html로 포함되어 있어야 한다
        HttpResponse httpResponse = new HttpResponse(createOutputStream("Http_Redirect.txt"));
        httpResponse.sendRedirect("/index.html");
    }

    @Test
    public void responseCookies() throws Exception{
        //Http_Cookie.txt 결과는 응답 header에 Set-Cookie 값으로
        //logined=true 값이 포함되어 있어야 한다
        HttpResponse httpResponse = new HttpResponse(createOutputStream("Http_Cookies.txt"));
        httpResponse.addHeader("Set-Cookie", "logined=true");
        httpResponse.sendRedirect("/index.html");
    }

    private OutputStream createOutputStream(String fileName) throws Exception{
        return new FileOutputStream(new File(testDirectory + fileName));
    }


}
