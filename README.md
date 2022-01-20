기상청_단기예보 ((구)_동네예보) 조회서비스

[바로가기](https://www.data.go.kr/data/15084084/openapi.do)

- 4가지의 상세 기능 중 단기 예보 조회 api 구현
- json-simple 라이브러리 활용해 JSON 파싱
- 10개 이상의 카테고리 변수 중에서 일부만 필터링을 거쳐 db에 저장
- JPA, mySQL 사용
- 현재는 좌표 값인 nx, ny를 임의의 수로 둔 상태
- localhost:8080/api/weather에 접속하면 db 쿼리까지 완료

