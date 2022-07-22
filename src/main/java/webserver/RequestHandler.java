package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

/**
 * RequestHandler 클래스는 Thread 를 상속하고 있으며, 사용자 요청에 대한 처리와 응답에 대한 처리를 담당하는 가장 중심이 되는 클래스
 */
public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    /**
     * run() 메서드에서 InputStream 은 클라이언트(웹 브라우저)에서 서버로 요청을 보낼 때 전달되는 데이터,
     * OutStream 은 서버에서 클라이언트에 응답을 보낼 때 전달되는 데이터를 담당하는 스트림
     */
    public void run() {
        log.debug("connection: {}", connection); // Socket[addr=/0:0:0:0:0:0:0:1,port=60441,localport=8080]
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            log.debug("request line: {}", line); // GET /index.html HTTP/1.1

            if (line == null) {
                return;
            }

            String[] tokens = line.split(" ");

            while (!line.equals("")) {
                line = br.readLine();
                log.debug("header: {}", line);
            }

            String url = tokens[1];
            if (url.startsWith("/user/create")) {
                int index = url.indexOf("?");
                String queryString = url.substring(index + 1);
                Map<String, String> params = HttpRequestUtils.parseQueryString(queryString);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                log.debug("user: {}", user);
            } else {
                DataOutputStream dos = new DataOutputStream(out);
                // byte[] body = "Hello World".getBytes();
                byte[] body = Files.readAllBytes(new File("./webapp" + tokens[1]).toPath());
                response200Header(dos, body.length);
                responseBody(dos, body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}

/**
 * 요구사항 #1 - index.html 응답하기
 * http://localhost:8080/index.html 로 접근했을 때 webapp 디렉토리의 index.html 파일을 읽어 클라이언트에 응답
 */

/**
 * 요구사항 #2 - Get 방식으로 회원가입하기
 * "회원가입" 메뉴를 클릭하면 http://localhost:8080/user/form.html 으로 이동하면서 회원가입 진행
 * 회원가입을 하면 다음과 같은 형태로 사용자가 입력한 값이 서버에 전달
 * /user/create?userId=primarchan%passoword=1111&name=Kevin&email=primarch%40yanolja.com
 * HTML 과 URL 을 비교해 보고 사용자가 입력한 값을 파싱해 model.User 클래스에 저장
 */

/**
 * 요구사항 #3 - POST 방식으로 회원가입하기
 * http://localhost:8080/user/form.html 파일의 form 태그 method 를 get 에서 post 로 수정한 후 회원가입이 정상적으로 동작하도록 구현
 */
