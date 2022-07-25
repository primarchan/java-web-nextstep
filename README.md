# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* http://localhost:8080/index.html 로 접근했을 때 webapp 디렉토리의 index.html 파일을 읽어 클라이언트에 응답

### 요구사항 2 - get 방식으로 회원가입
* "회원가입" 메뉴를 클릭하면 http://localhost:8080/user/form.html 으로 이동하면서 회원가입 진행
* 회원가입을 하면 다음과 같은 형태로 사용자가 입력한 값이 서버에 전달
* /user/create?userId=primarchan%passoword=1111&name=Kevin&email=primarch%40yanolja.com
* HTML 과 URL 을 비교해 보고 사용자가 입력한 값을 파싱해 model.User 클래스에 저장

### 요구사항 3 - post 방식으로 회원가입
* http://localhost:8080/user/form.html 파일의 form 태그 method 를 get 에서 post 로 수정한 후 회원가입이 정상적으로 동작하도록 구현

### 요구사항 4 - redirect 방식으로 이동
* "회원가입"을 완료하면 /index.html 페이지로 이동
* 현재는 URL 이 /user/create 로 유지되는 상태로 읽어서 전달할 파일이 없음
* 따라서 회원가입을 완료한 후 /index.html 페이지로 이동
* 브라우저 URL 도 /user/create 가 아니라 /index.html로 변경 필요

### 요구사항 5 - cookie
* "로그인" 메뉴를 클릭하면 http://localhost:8080/user/login.html 로 이동해 로그인 가능
* 로그인 성공 시 "/index.html" 로 이동, 로그인 실패 시 "/login_failed.html"로 이동
* 기 회원가입한 사용자로 로그인 가능해야 함
* 로그인 성공 시 로그인 상태 유지 필요
* 로그인 성공 시 요청 헤더의 Cookie 헤더 값이 logined=true, 로그인 실패 시 logined=false 로 전달
* 접근하고 있는 사용자가 "로그인" 상태일 경우(Cookie 값이 logined=true) http://localhost:8080/user/list 로 접근했을 때 사용자 목록을 출력
* 만약 로그인 하지 않은 상태라면 로그인 페이지(login.html) 로 이동

### 요구사항 6 - stylesheet 적용
* 지금까지 구현한 소스코드는 CSS 파일을 지원하지 못하고 있음 -> CSS 파일을 지원하도록 구현

### heroku 서버에 배포 후
* 
