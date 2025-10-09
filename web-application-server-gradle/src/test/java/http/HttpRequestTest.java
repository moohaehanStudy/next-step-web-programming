package http;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {

    private String testDirectory = "./src/test/resources/";

    @Test
    void 요청_테스트() throws FileNotFoundException {
        InputStream in = new FileInputStream(new File(testDirectory + "Http_POST.txt"));
        HttpRequest request = new HttpRequest(in);

        assertEquals("POST", request.getHttpMethod());
        assertEquals("/user/create", request.getRequestUrl());
        assertEquals("keep-alive", request.getHeader().get("Connection"));
    }
}