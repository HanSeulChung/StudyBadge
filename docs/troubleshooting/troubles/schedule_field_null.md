# 테이블에 NULL 값 설정 관련 고민

프론트와 상의했을때 특정 필드들을 null 값으로 주고받기로 했는데 테이블에 nullable을 잦게 허용하는게 괜찮은지 고민 중

## Problem

프론트와 상의 후 월간 반복일 경우 repeat situation 요청 필드값을 null 로 받고 조회 시 응답 값도 null로 줄 예정

```json
{
    "type": "repeat",
    "scheduleName": "Monthly Team Meeting",
    "scheduleContent": "Discussion on project progress and planning",
    "scheduleDate": "2024-07-05",
    "scheduleStartTime": "10:00:00",
    "scheduleEndTime": "11:00:00",
    "repeatCycle": "MONTHLY",
    "repeatSituation": null,
    "repeatEndDate": "2024-12-31"
}
```

왜 월간 반복일 때 null값을 두었는지? 

→ 초기에 repeatSituation 의 enum 값에 MONTHLY_ONE, MONTHLY_TWO, …, MONTHLY_THIRTY와 같이 지정하고 저장할 수 있는 값을 만들었지만 프론트 쪽에서는 별도의 값을 주는 것이 조금 번거롭다고 하여 지정하지 않고 null값으로 주고받는 것이 어떠냐고 제안이 왔음

## Reason

사실 문제된다기보다 어떤 게 좋을지 현재 고민 중

1. 요청 값을 null로 받아도 서버에서 db 테이블에 null이 아닌 특정 enum값을 저장하고 월간 반복일 경우 null값으로 응답 값을 대체하거나 프론트에서 월간 반복일 경우 repeat situation값을 무시하는 방법
2. 서버 db 테이블에 그대로 null으로 저장해 대체하는 로직 또한 쓰지 않는 방법 

## Try to solve

(멘토님의 조언을 토대로)

**→ null 값은 null exception등 관리해야하는 수고가 있으므로 최대한 null 값을 지양하기로 함.** 

**프론트에서 값을 주면 적절한 enum 값으로 변환되도록 json 역직렬화 과정을 추가함.**

 repeatSituation 의 enum의 description 필드를 Objbect로 두고 String, int 로 두고 json 역직렬화를 만듦.
