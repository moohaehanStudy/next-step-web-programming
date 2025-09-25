package http;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Getter
public class HttpResponse {

    private DataOutputStream dos = null;
    private byte[] body;
    private Map<String, String> headers = new HashMap<>();

    public HttpResponse(OutputStream out) throws IOException {
        dos = new DataOutputStream(out);
    }

    public void forward(String url){
        try {
            body = Files.readAllBytes(new File("./webapp" + url).toPath());
            if(url.endsWith(".css")){
                headers.put("Content-Type", "text/css");
            }
            else if(url.endsWith(".js")){
                headers.put("Content-Type", "application/javascript");
            }
            else {
                headers.put("Content-Type", "text/html;charset=utf-8");
            }
            headers.put("Content-Length", body.length+"");
            response200Header();
            responseBody(body);
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }

    public void forwardBody(String body){
        byte[] contents = body.getBytes();
        headers.put("Content-Type", "text/html;charset=utf-8");
        headers.put("Content-Length", contents.length+"");
        response200Header();
        responseBody(contents);
    }

    public void addHeader(String key, String value){
        headers.put(key, value);
    }

    private void processHeaders(){
        try {
            Set<String> keys = headers.keySet();
            for(String key : keys){
                dos.writeBytes(key + ": " + headers.get(key) + "\r\n");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void response302Header(String locationUrl){
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            processHeaders();
            dos.writeBytes("Location: " + locationUrl + "\r\n");
            // 쿠키가 필요한 경우 이 로직이 아닌 실행되는 로직에서 addHeader를 이용해 넣도록 위임한다.
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void response200Header() {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            processHeaders();
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
