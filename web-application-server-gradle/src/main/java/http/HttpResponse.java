package http;

import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;

public class HttpResponse {
    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
    private Map<String, String> headers = new HashMap<>();
    private DataOutputStream dos = null;

    public HttpResponse(OutputStream outputStream) {
        dos = new DataOutputStream(outputStream);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void forward(String path){
        try {
            byte[] bytes = Files.readAllBytes(new File("./webapp" + path).toPath());
            response200Header(bytes.length, path);
            responseBody(bytes);
        } catch(IOException e){
            log.error(e.getMessage());
        }
    }

    public void response200Header(int length, String url){
        try{
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            addHeader("Content-Type", getContentType(url));
            addHeader("Content-Length", String.valueOf(length));
            processHeader();
            dos.flush();
        } catch (IOException e){
            log.error(e.getMessage());
        }
    }

    private String getContentType(String url) {
        String type = "text/html";

        if(url.endsWith(".css"))
            type = "text/css";

        return type + ";charset=UTF-8";
    }

    public void responseBody(byte[] bytes){
        try {
            dos.write(bytes, 0, bytes.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void sendRedirect(String location){
        try{
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            addHeader("Location", location);
            addHeader("Content-Length", "0");
            processHeader();
            dos.flush();
        } catch(IOException e){
            log.error(e.getMessage());
        }
    }

    public void processHeader(){
        try {
            for (String key : headers.keySet()) {
                dos.writeBytes(key + ": " + headers.get(key) + "\r\n");
            }
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

}
