package controller;


import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;

/**
 * 요청 URL 이 같더라도 HTTP 메서드가 다른 경우, 새로운 Controller 클래스를 추가하지 않고 Controller 하나로
 * Get(doGet 메서드), POST(doPost 메서드)를 모두 지원하는 것이 가능
 */
public abstract class AbstractController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        HttpMethod method = request.getMethod();

        if(method.isPost()) {
            doPost(request, response);
        } else {
            doGet(request, response);
        }
    }

    protected void doPost(HttpRequest request, HttpResponse response) {}

    protected void doGet(HttpRequest request, HttpResponse response) {}
}
