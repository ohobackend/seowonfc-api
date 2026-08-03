# 서원 FC API

서원 FC 동호회 홈페이지를 위한 백엔드 REST API 프로젝트입니다.

## 기술 스택

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- Spring Security (JWT)
- PostgreSQL
- Gradle
- springdoc-openapi (Swagger)

## 프로젝트 구조

```
src/main/java/com/seowonfc/api
 ├─ config/       # Swagger, Security, JWT 설정
 ├─ common/       # 공통 응답 포맷, 에러 처리
 └─ domain/       # 도메인별 패키지 (user, news, player, match, community, sponsor, event, notification)
```

## 실행 방법

### 1. 사전 준비
- JDK 17 이상
- PostgreSQL 실행 중이어야 함 (DB명: `seowonfc`)

### 2. 환경 설정
`src/main/resources/application.yml` 에서 DB 접속 정보를 본인 환경에 맞게 수정합니다.

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/seowonfc
    username: postgres
    password: ${DB_PASSWORD}
```

### 3. 실행
```bash
./gradlew bootRun
```

### 4. API 문서 확인
서버 실행 후 아래 주소에서 Swagger 문서를 확인할 수 있습니다.

```
http://localhost:8080/swagger-ui.html
```

## API 구성

- 회원용 API: `/api/v1/**`
- 관리자용 API: `/api/v1/admin/**` (ADMIN 권한 필요)

주요 기능: 회원/인증, 구단 뉴스, 선수단, 경기 정보, 팬 커뮤니티, 스폰서, 이벤트, 알림

## 라이선스

이 프로젝트는 서원 FC 동호회 내부 사용을 위한 프로젝트입니다.