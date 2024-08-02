# Nginx를 프록시 서버로 두었을 때의 SSE 관련 문제 상황
작성자: 정한슬

## Problem 1

http 로컬 → 로컬에서 테스트했을 때는 문제가 없었으나, nignx를 프록시 서버로 두었을 경우 sse 연결이 불안정한 문제

![image](https://github.com/HanSeulChung/StudyBadge/blob/main/docs/troubleshooting/img/sse_error1.png?raw=true)

![image](https://github.com/HanSeulChung/StudyBadge/blob/main/docs/troubleshooting/img/error2.png?raw=true)

sse 연결 후 첫 알림만 바로 확인이 가능하고 이후 알림들은 전달되지 않는 문제

## Reason

`ERR_INCOMPLETE_CHUNKED_ENCODING` 오류가 발생하는 상황

→ nginx가 이벤트 스트림을 올바르게 처리하지 못해서 발생하는 문제

## Try to solve

1. proxy_buffering
2. http 1.1 로 설정
3. chunked_transfer_encoding on설정

```jsx
		proxy_buffering off;
    proxy_cache off;
    proxy_http_version 1.1;
    chunked_transfer_encoding on;
```

---


## Problem 2

spring에서 해당 도메인을 cors허용을 했음에도 cors 오류가 난 상황

→ 게다가 이전에는 나지 않았고 nginx 경로 설정을 분리했을 때 sse 세션 연결하는 곳에서만 났었음
![image](https://github.com/HanSeulChung/StudyBadge/blob/main/docs/troubleshooting/img/sse_error2.png?raw=true)

## Reason

sse 세션 연결하는 경로 설정을 다음과 같이 했기 때문에 cors 설정이 spring에도 되어있고 nginx 에도 되어있어서 중복되었기 때문에 생긴 문제

```jsx
location /api/notifications/subscribe {
    proxy_pass http://127.0.0.1:8080/;
    proxy_set_header Host $host;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header Connection 'keep-alive';
    proxy_set_header Cache-Control 'no-cache';
    proxy_set_header X-Accel-Buffering 'no';
    proxy_set_header Content-Type 'text/event-stream';
    proxy_set_header Last-Event-ID $http_last_event_id;
    proxy_buffering off;
    proxy_cache off;
    chunked_transfer_encoding on;
    proxy_http_version 1.1;
    proxy_read_timeout 86400s;

    # CORS 설정 추가
    add_header 'Access-Control-Allow-Origin' 'https://study-badge.vercel.app';
    add_header 'Access-Control-Allow-Methods' 'GET, OPTIONS';
    add_header 'Access-Control-Allow-Headers' 'Authorization, Content-Type, Last-Event-ID';
    add_header 'Access-Control-Allow-Credentials' 'true';

    # 프리플라이트 요청 처리
    if ($request_method = OPTIONS) {
        add_header 'Access-Control-Allow-Origin' 'https://study-badge.vercel.app';
        add_header 'Access-Control-Allow-Methods' 'GET, OPTIONS';
        add_header 'Access-Control-Allow-Headers' 'Authorization, Content-Type, Last-Event-ID';
        add_header 'Access-Control-Allow-Credentials' 'true';
        add_header 'Access-Control-Max-Age' 1728000;
        add_header 'Content-Type' 'text/plain; charset=UTF-8';  # 주의: 세미콜론을 사용하여 charset 설정
        add_header 'Content-Length' 0;
        return 204;
    }
}
```

## Try to solve

```jsx
location /api/notifications/subscribe {
    proxy_pass http://127.0.0.1:8080/;
    proxy_set_header Host $host;
    proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    proxy_set_header X-Forwarded-Proto $scheme;
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header Connection 'keep-alive';
    proxy_set_header Cache-Control 'no-cache';
    proxy_set_header X-Accel-Buffering 'no';
    proxy_set_header Content-Type 'text/event-stream';
    proxy_set_header Last-Event-ID $http_last_event_id;
    proxy_buffering off;
    proxy_cache off;
    chunked_transfer_encoding on;
    proxy_http_version 1.1;
    proxy_read_timeout 86400s;
}
```

cors 설정을 지우니 해결 됨.

