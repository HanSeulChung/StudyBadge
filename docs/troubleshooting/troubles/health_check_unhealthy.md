# 로드밸런서 대상그룹 unhealthy 문제 (with 간헐적 502 에러)

### https로 배포할 때 AWS Router53 + ACM + AWS 로드밸런서를 사용했을 때의 상황


## Problem

보안 그룹의 인바운드도 8080/ 443 다 허용하고, 

로드 밸런서(http → https) 리스너도 8080 / 443 포트 규칙을 다 준수했음에도

Unhealthy로 상태 확인이 되는 점

![image](https://github.com/user-attachments/assets/1c2375f4-43cc-48b8-8976-0ea24768b2b9)


401에러와 502에러가 뜨는 점

![image](https://github.com/user-attachments/assets/3ea5f78f-5b98-43e2-82d2-1cc43ea978f9)

![image](https://github.com/user-attachments/assets/35073cbc-bc4b-4ac4-b72c-a6471624be98)

## Reason

health check하는 경로를 permitAll로 설정해놓지 않았기 때문

## Try to solve

1. health-check하는 경로를 “/”로 설정해놓고 securityConfig에서 “/”경로를 permitAll을 했다.
    
    401 에러
    
    경로 “/”를 permitAll으로 했음에도 https://도메인/ 에서 401 에러가 뜬다
    ![image](https://github.com/user-attachments/assets/6db13c1e-a5c4-441f-9441-31fa6ecb9337)

   
    

1. health-check 하는 경로를 따로 설정해두고 permitAll 해둠
    
    ![image](https://github.com/user-attachments/assets/a1858635-e687-4f91-a5f7-661ccb49bd67)

    
    health-check하는 경로를 설정한 뒤 securityConfig에서 경로를 permitAll에 추가한다.
    
    ```java
    @RestController
    public class HealthCheckController {
    
      @GetMapping("/health-check")
      public String healthcheck() {
        return "OK";
      }
    }
    ```
    
    ![image](https://github.com/user-attachments/assets/abf15ff2-0b50-453c-8c28-956a582eba0b)

    

403포트는 설정하는 것이 아니었음.
