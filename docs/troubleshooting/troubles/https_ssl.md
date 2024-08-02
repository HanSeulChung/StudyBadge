## Problem

중간 배포 + 클라이언트와 https 연동을 위해 aws router 53 + ACM + 로드밸런서(http → https) 을 통해 간편하게 ssl 인증을 연동해 https로 배포했었으나 public Ip가 2개가 생겨 돈이 나가고 있는 상황

![image](https://github.com/user-attachments/assets/08f347f7-0e55-479d-9877-09352aecb1aa)

![image](https://github.com/user-attachments/assets/41488ac4-deec-4681-bae2-52de6b91bf83)

## Reason

로드 밸런서를 사용할 때 가용 영역 2개를 무조건 선택해야하는 구조인데 여기서 각각 public IP로 설정되어 요금이 시간당 0.005 달러가 나가고 있었다.

0.005 달러 * 3일(14일 ~ 16일) * public IP 2개 = USD 0.87

쉬운 방법으로 https로 배포하는 방법 이었으나 개발자는 늘 비용을 최소화하도록 노력해야한다는 점에서 잘못된 방법이라 생각되어 방법을 바꾸기로 했다.

## Try to solve

ssl을 적용하여 https로 배포할 수 있는 방법은 다음 3가지 방법이 있다.

1. cloudflare을 사용
2. nginx 와 cerbot을 통한 ssl을 연동하는 방법
3. AWS CloudFront 사용

CloudFront도 결국 AWS이기 때문에 일단 제외했다. 

nginx는 ssh에 접속해서 설치하고 conf로 설정값을 두는 것은 간단해 보였으나 Let's Encrypt사이트에서 별도로 인증서를 발급 받아야하는 구조였다. 해당 사이트에서는 발급을 받으면 3개월의 무료 만료 기간이 존재한다. 물론 cerbot을 이용해서 자동적으로 만료기간 전에 무료 발급을 할 수 있도록 설정할 수 있지만, 그냥 그런 설정보다는 cloudflare가 알아서 인증서를 책임져주는 구조를 선택하기로 결정했다.

→ 고려해야하는 점 cloudflare를 사용하면 우회하는 속도가 너무 느림….

→ cloudflare를 사용했을 때 손쉽게 https연동이 되었으나 refresh token 쿠키 문제로 결국 ssl을 cerobt에서 가져오거나 cloudflare에서 ssl을 설정을 추가해줘야했다.

nginx + cerbot ssl 이 가장 https 배포에 보안성을 챙기면서 가격이 낮은 간단한 방법 같다.
