package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import javax.xml.crypto.Data;

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

            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = br.readLine();
            log.debug("request line: {}", line); // GET /index.html HTTP/1.1

            if (line == null) {
                return;
            }

            String[] tokens = line.split(" ");
            boolean logined = false;
            int contentLength = 0;

            while (!line.equals("")) {
                log.debug("header: {}", line);
                line = br.readLine();
                if (line.contains("Cookie")) {
                    logined = isLogin(line);
                }
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
                DataOutputStream dos = new DataOutputStream(out);
                response302LoginSuccessHeader(dos);
                DataBase.addUser(user);
                // url = "/index.html"; -> 회원가입 요청(/user/craete)을 완료한 후 URL 요청 값을 "/index.html" 로 변경하면 새로고침 시 이전 요청이 재전송되는 이슈 발생
            } else if ("/user/login".equals(url)) {
                String body = IOUtils.readData(br, contentLength);
                Map<String, String> params = HttpRequestUtils.parseQueryString(body);
                log.debug("params: {}", params);
                User user = DataBase.findUserById(params.get("userId"));
                if (user == null) {
                    responseResource(out, "/user/login_failed.html");
                    return;
                }
                if (user.getPassword().equals(params.get("password"))) {
                    DataOutputStream dos = new DataOutputStream(out);
                    response302LoginSuccessHeader(dos);
                } else {
                    responseResource(out, "/user/login_failed.html");
                }
            } else if ("/user/list".equals(url)) {
                if (!logined) {
                    responseResource(out, "/user/login.html");
                    return;
                }
                Collection<User> users = DataBase.findAll();
                StringBuilder sb = new StringBuilder();
                sb.append("<table border='1'>");
                for (User user : users) {
                    sb.append("<tr>");
                    sb.append("<td>" + user.getUserId() + "</td>");
                    sb.append("<td>" + user.getName() + "</td>");
                    sb.append("<td>" + user.getEmail() + "</td>");
                    sb.append("</tr>");
                }
                sb.append("</table>");
                byte[] body = sb.toString().getBytes();
                DataOutputStream dos = new DataOutputStream(out);
                response200Header(dos, body.length);
                responseBody(dos, body);
            } else if (url.endsWith(".css")) {
                DataOutputStream dos = new DataOutputStream(out);
                byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
                response200CssHeader(dos, body.length);
                responseBody(dos, body);
            } else {
                responseResource(out, url);
            }

            // Get 요청 시
            /*
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
             */
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

    private void response200CssHeader(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css \r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302LoginSuccessHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("Location: /index.html \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseResource(OutputStream out, String url) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        response200Header(dos, body.length);
        responseBody(dos, body);
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private int getContentLength(String line) {
        String[] headerTokens = line.split(":");
        return Integer.parseInt(headerTokens[1].trim());
    }

    private boolean isLogin(String line) {
        String[] headerTokens = line.split(":");
        Map<String, String> cookies = HttpRequestUtils.parseCookies(headerTokens[1].trim());
        String value = cookies.get("logined");
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
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

/**
 * 요구사항 #5 - 로그인 하기
 * "로그인" 메뉴를 클릭하면 http://localhost:8080/user/login.html 로 이동해 로그인 가능
 * 로그인 성공 시 "/index.html" 로 이동, 로그인 실패 시 "/login_failed.html"로 이동
 * 기 회원가입한 사용자로 로그인 가능해야 함
 * 로그인 성공 시 로그인 상태 유지 필요
 * 로그인 성공 시 요청 헤더의 Cookie 헤더 값이 logined=true, 로그인 실패 시 logined=false 로 전달
 */

/**
 * 요구사항 #6 - 사용자 목록 출력
 * 접근하고 있는 사용자가 "로그인" 상태일 경우(Cookie 값이 logined=true) http://localhost:8080/user/list 로 접근했을 때 사용자 목록을 출력
 * 만약 로그인 하지 않은 상태라면 로그인 페이지(login.html) 로 이동
 */

/**
 * 요구사항 #7 - CSS 지원하기
 * 지금까지 구현한 소스코드는 CSS 파일을 지원하지 못하고 있음
 * CSS 파일을 지원하도록 구현
 */