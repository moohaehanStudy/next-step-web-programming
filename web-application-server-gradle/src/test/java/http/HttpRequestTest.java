package http;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpRequestTest {
    private String testDirectory = "./src/test/resources/";

    @Test
    public void request_GET() throws Exception{
        InputStream in = new FileInputStream(new File(testDirectory+"Http_GET.txt"));

        HttpRequest httpRequest = new HttpRequest(in);

        assertEquals("GET", httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getRequestPath());
        assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("soyun", httpRequest.getParam("userId"));
    }

    @Test
    public void request_POST() throws Exception{
        InputStream in = new FileInputStream(new File(testDirectory+ "Http_POST.txt"));

        HttpRequest httpRequest = new HttpRequest(in);

        assertEquals("POST", httpRequest.getMethod());
        assertEquals("/user/create", httpRequest.getRequestPath());
        assertEquals("keep-alive", httpRequest.getHeader("Connection"));
        assertEquals("soyun", httpRequest.getParam("userId"));
    }


}
