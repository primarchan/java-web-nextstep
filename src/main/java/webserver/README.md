## 4.HTTP 웹 서버 구현을 통해 HTTP 이해하기

### 4.2 HTTP 웹 서버 구현
- 웹 클라이언트는 웹 서버와 데이터를 주고받기 위해 HTTP라는 서로 간에 약속된 규약을 따름
- 모든 HTTP 요청에 대해 요청 라인, 요청 헤더, 빈 공백 문자열은 필수, 요청 본문은 비필수 요소
> - 웹 클라이언트 -> 웹 서버 요청 예시)  
> `POST /user/create HTTP/1.1` -> 요청 라인  
> `HOST: localhost:8080` -> 요청 헤더  
> `Connection-Length: 59`  -> 요청 헤더  
> `Content-type: application/x-www-form-urlencoded`  -> 요청 헤더  
> `Accept: */*` -> 요청 헤더  
> -> 헤더와 본문 사이의 빈 공백 라인  
> `userId=primarchan&password=1111` -> 요청 본문
>
> 
> - 웹 서버 -> 웹 클라이언트 응답 예시   
> `HTTP/1.1 200 OK` -> 상태 라인  
> `Content-type: text/htlml;charset=utf-8` -> 응답 헤더  
> `Content-Length: 20` -> 응답 헤더  
> -> 헤더와 본문 사이의 빈 공백 라인
> `<h1>응답 본문입니다.<h1>` -> 응답 본문  

