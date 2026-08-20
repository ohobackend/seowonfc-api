# 서원 FC 백엔드 — 선수 등록 신청 상태 정합성 수정 지시서 (Codex용)

> 문제: 관리자가 `PlayerApplication`을 승인하면 `Player`가 새로 생성되지만, 이 둘 사이에 연결고리가 없어서
> 이후 관리자가 그 `Player`를 삭제해도 `PlayerApplication`은 여전히 `APPROVED` 상태로 남아있습니다.
> 회원의 "내 신청 목록"에는 실제로는 취소된 승인이 계속 "승인됨"으로 잘못 표시됩니다.
>
> 해결: `PlayerApplication`에 생성된 `Player`의 id를 저장해 연결하고, 관리자가 `Player`를 삭제할 때
> 연결된 신청이 있으면 자동으로 `REJECTED` 상태 + 사유("관리자에 의해 선수 등록이 취소되었습니다.")로 전환합니다.

---

## 1. PlayerApplication Entity 수정 — playerId 필드 추가

기존 `PlayerApplication.java`에 `playerId` 필드와 관련 메서드를 추가합니다. (기존 필드/메서드는 그대로 유지)

```java
// 필드 추가
private Long playerId;

// approve() 메서드를, playerId를 함께 저장하도록 수정
public void approve(Long playerId) {
    this.status = ApplicationStatus.APPROVED;
    this.playerId = playerId;
}

// 관리자가 승인된 선수를 삭제했을 때 호출할 메서드 추가
public void cancelApproval() {
    this.status = ApplicationStatus.REJECTED;
    this.rejectReason = "관리자에 의해 선수 등록이 취소되었습니다.";
    this.playerId = null;
}
```

기존에 `approve()`가 인자 없는 형태(`public void approve() { this.status = ApplicationStatus.APPROVED; }`)였다면, 위처럼 `Long playerId`를 받는 형태로 시그니처를 변경합니다.

---

## 2. PlayerApplicationRepository — playerId로 조회하는 메서드 추가

```java
package com.seowonfc.api.domain.player;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PlayerApplicationRepository extends JpaRepository<PlayerApplication, Long> {
    // 기존 메서드들 유지
    Optional<PlayerApplication> findByPlayerId(Long playerId);
}
```

---

## 3. PlayerApplicationService 수정

### 3-1. approve() — Player 생성 후 id를 신청 기록에 저장

기존 `approve(Long applicationId)` 메서드를 아래처럼 수정합니다.

```java
@Transactional
public Long approve(Long applicationId) {
    PlayerApplication application = findById(applicationId);

    Player player = Player.builder()
            .name(application.getName())
            .backNumber(application.getBackNumber())
            .position(application.getPosition())
            .nationality(application.getNationality())
            .profileImageUrl(application.getProfileImageUrl())
            .build();
    Long playerId = playerRepository.save(player).getId();

    application.approve(playerId);   // ← 생성된 playerId를 함께 저장

    return playerId;
}
```

### 3-2. cancelApprovalByPlayerId() — 선수 삭제 시 호출할 메서드 추가

```java
@Transactional
public void cancelApprovalByPlayerId(Long playerId) {
    applicationRepository.findByPlayerId(playerId)
            .ifPresent(PlayerApplication::cancelApproval);
}
```

`Optional`이라 신청 기록이 없는 선수(관리자가 예전 방식으로 직접 등록한 선수 등)를 삭제해도 에러 없이 안전하게 넘어갑니다.

---

## 4. PlayerService 수정 — 선수 삭제 시 연동 신청도 함께 정리

기존 `PlayerService`에 `PlayerApplicationService`를 주입받아, `delete()` 메서드에서 함께 호출합니다.

```java
private final PlayerApplicationService playerApplicationService; // 필드 추가 (생성자 주입)

@Transactional
public void delete(Long id) {
    playerApplicationService.cancelApprovalByPlayerId(id); // 먼저 신청 상태 정리
    playerRepository.delete(findById(id));
}
```

> **순환 참조 주의**: `PlayerApplicationService`가 `PlayerService`를 참조하지 않는 구조라면(현재 `PlayerApplicationService`는 `PlayerRepository`를 직접 쓰고 있어 `PlayerService`를 참조하지 않을 가능성이 높음) 순환 참조 문제는 없습니다. 혹시 `PlayerApplicationService`가 `PlayerService`를 주입받고 있다면, 이 경우 `PlayerRepository`를 직접 쓰도록 리팩터링하거나 `@Lazy`를 사용해야 합니다 — Codex가 기존 코드를 보고 판단해서 순환 참조가 없는 방향으로 처리해주세요.

---

## 5. 확인해야 할 기존 코드 — DTO 응답에 playerId 노출 (선택, 권장)

`PlayerApplicationResponse`에 `playerId` 필드를 추가하면, 관리자 화면에서 "이 신청이 몇 번 선수로 등록됐는지" 추적하기 쉬워집니다. 필수는 아니지만 권장합니다.

```java
public record PlayerApplicationResponse(
        Long id, String applicantName, String name, Integer backNumber, Position position,
        String nationality, String profileImageUrl, ApplicationStatus status,
        String rejectReason, Long playerId, LocalDateTime createdAt   // playerId 추가
) {
    public static PlayerApplicationResponse from(PlayerApplication app) {
        return new PlayerApplicationResponse(app.getId(), app.getApplicant().getName(),
                app.getName(), app.getBackNumber(), app.getPosition(), app.getNationality(),
                app.getProfileImageUrl(), app.getStatus(), app.getRejectReason(),
                app.getPlayerId(), app.getCreatedAt());
    }
}
```

---

## 6. 적용 및 확인 순서

1. `PlayerApplication` Entity 수정 (`playerId` 필드, `approve(Long)`, `cancelApproval()`)
2. `PlayerApplicationRepository`에 `findByPlayerId` 추가
3. `PlayerApplicationService`의 `approve()` 수정 + `cancelApprovalByPlayerId()` 추가
4. `PlayerService`의 `delete()`에 신청 정리 로직 추가 (생성자 주입 필드 추가 필요)
5. (선택) `PlayerApplicationResponse`에 `playerId` 추가
6. 재실행 → 시나리오 테스트
   - 회원 계정으로 선수 등록 신청 2건 생성 (신청 A, 신청 B)
   - ADMIN으로 신청 A 승인 → `GET /api/v1/players`에서 생성된 선수 id 확인
   - ADMIN으로 해당 선수를 `DELETE /api/v1/admin/players/{playerId}`로 삭제
   - 회원 계정으로 `GET /api/v1/player-applications/me` 조회 → **신청 A의 status가 `REJECTED`로, rejectReason이 "관리자에 의해 선수 등록이 취소되었습니다."로 바뀌어 있는지 확인**
   - 신청 B는 영향 없이 그대로 `PENDING` 상태인지 확인
7. 커밋

```bash
git add .
git commit -m "fix: sync player application status when admin deletes approved player"
git push
```

---

## 7. 프론트엔드 반영 필요 여부

**기본적으로는 프론트엔드 수정이 필요 없습니다.** 이미 만들어진 "내 신청 내역" 화면(`/players/my-applications`)이 `status`와 `rejectReason`을 그대로 표시하고 있다면, 백엔드가 데이터를 정확하게 바로잡아 주는 것만으로 화면에도 올바르게 반영됩니다.

다만 반려 사유가 화면에서 잘려 보이거나 안 보이는 상태라면, 그 부분만 프론트에서 점검이 필요할 수 있습니다. 백엔드 배포 후 실제 화면에서 확인해보고 필요하면 별도로 요청해주세요.
