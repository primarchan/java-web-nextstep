# 5. 웹 서버 리팩토링, 서블릿 컨테이너와 서블릿의 관계
## 5.1 HTTP 웹 서버 리팩토링 실습
### 5.1.1 리팩토링할 부분 찾기
- 리팩토링을 하기 위해서는 먼저 Bad Smell 이 나는 코드를 찾을 수 있는 능력을 키워야 함
- 리팩토링을 어떻게 하느냐하는 능력보다는 리팩토링이 필요한 시점과 종료해야하는 시점을 판단해야 하는 능력 중요
- 리팩토링을 진행할 때, 어떤 기준을 가지고 하기 보다는 직관에 의존해 진행
- 위와 같은 직관 능력을 향상시키기 위해서는 좋은 코드, 나쁜 코드 가리지 않고 다른 개발자가 구현한 많은 코드를 읽어야 함
### 5.1.2 리팩토링 1단계 힌트
#### 5.1.2.1 요청 데이터를 처리하는 로직을 별도의 클래스로 분리`(HttpRequest)`
- 클라이언트 요청 데이터를 담고 있는 `InputStream` 을 생성자로 받아 `HTTP 메서드`, `URL`, `헤더`, `본문`을 분리하는 작업
- 헤더는 `Map<String, String>` 에 저장해 관리하고 `getHeader("필드 이름")` 메서드를 통해 접근 가능하도록 구현
- GET 과 POST 메서드에 따라 전달되는 인자를 `Map<String, String>` 에 저장해 관리하고 `getHeader("필드 이름")`메서드를 통해 접근 가능하도록 구현
#### 5.1.2.2 응답 데이터를 처리하는 로직을 별도의 클래스로 분리`(HttpResponse)`
- RequestHandler 클래스를 보면 응답 데이터 처리를 위한 많은 중복이 존재 -> 해당 중복 제거
- 응답 헤더 정보를 `Map<String, String>` 으로 관리
- 응답을 보낼 때, `HTML`, `CSS`, `JavaScript` 파일을 직접 읽어 응답으로 보내는 메서드 `forward()`, 
- 다른 URL 로 리다이렉트하는 메서드는 `sendRedirect()` 메서드로 나누어 구현
#### 5.1.2.3 다형성을 활용해 클라이언트 요청을 URL 에 대한 분기 처리 제거
- 각 요청과 응답에 대한 처리를 담당하는 부분을 추상화 해 인터페이스로 만듦
- 인터페이스는 다음과 같이 구현
 ```java
public interface Controller() {
    void service(HttpRequest request, HttpResponse response);
}
```
- 각 분기문을 `Controller 인터페이스`를 `구현하는(implements) 클래스`를 만들어 분리
- 이렇게 생성한 Controller 구현체를 
- 클라이언트 요청을
- `Controller 인터페이스`를 구현하는 `AbstractController 추상클래스`를 추가해 중복을 제거
- `service()` 메서드에서 `GET` 과 `POST` `HTTP 메서드`에 따라 `doGet()`, `doPost()` 메서드를 호출
