package webserver;

import controller.Controller;
import controller.CreateUserController;
import controller.ListUserController;
import controller.LoginController;

import java.util.HashMap;
import java.util.Map;

/**
 * 각 Controller의 요청 URL 과 URL에 대응하는 Controller 를 연결하는 클래스
 * 웹 애플리케이션에 서비스하는 모든 URL 과  Controller 를 관리
 * 요청 URL 에 해당하는 Controller 를 반환
 */
public class RequestMapping {
    private static Map<String, Controller> controllers = new HashMap<String, Controller>();

    static {
        controllers.put("/user/create", new CreateUserController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/list", new ListUserController());
    }

    public static Controller getController(String requestUrl) {
        return controllers.get(requestUrl);
    }
}
