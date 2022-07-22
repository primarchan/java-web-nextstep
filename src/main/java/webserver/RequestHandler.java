package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

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
            int contentLength = 0;

            while (!line.equals("")) {
                line = br.readLine();
                log.debug("header: {}", line);
                if (line.contains("Content-Length")) {
                    contentLength = getContentLength(line);
                }
            }

            String url = tokens[1];

            // POST 요청 시
            if ("/user/create".equals(url)) {
                String body = IOUtils.readData(br, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryString(body);
                log.debug("params: {}", params);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                log.debug("post user: {}", user);

                // 회원가입 요청(/user/craete)을 완료한 후 URL 요청 값을 "/index.html" 로 변경
                url = "/index.html";
            }
            // Get 요청 시
            if (url.startsWith("/user/create?")) {
                int index = url.indexOf("?");
                String queryString = url.substring(index + 1);
                Map<String, String> params = HttpRequestUtils.parseQueryString(queryString);
                User user = new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email"));
                log.debug("get user: {}", user);
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

    private int getContentLength(String line) {
        String[] headerTokens = line.split(":");
        return Integer.parseInt(headerTokens[1].trim());
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

/**
 * 요구사항 #4 - 302 status code 적용
 * "회원가입"을 완료하면 /index.html 페이지로 이동
 * 현재는 URL 이 /user/create 로 유지되는 상태로 읽어서 전달할 파일이 없음
 * 따라서 회원가입을 완료한 후 /index.html 페이지로 이동
 * 브라우저 URL 도 /user/create 가 아니라 /index.html로 변경 필요
 */
