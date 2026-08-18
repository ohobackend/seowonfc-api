# 서원 FC 백엔드 — 스폰서 신청/승인 기능 지시서 (Codex용)

> 스폰서 등록을 "관리자가 직접 입력"하는 방식에서, **누구나(비로그인 포함) 신청 → 관리자 승인 시 실제 등록**되는 방식으로 변경합니다.
> 선수 등록 신청(`PlayerApplication`)과 구조는 비슷하지만, **신청자가 로그인한 회원일 필요가 없다는 점**이 가장 큰 차이입니다.

---

## 1. 설계 방향

- 스폰서 신청은 외부 기업/후원자가 하는 것이므로, 서원 FC 홈페이지에 **회원가입 없이도** 신청할 수 있어야 합니다.
- 따라서 신청 API는 `SecurityConfig`에서 `permitAll()`로 열어둡니다 (인증 불필요).
- 이미지(로고) 업로드도 기존 `/api/v1/images`(로그인 필요)를 쓸 수 없으므로, **신청 API 자체에서 파일을 함께 받아 처리**합니다 (뉴스/이벤트 등록과 같은 multipart 패턴).
- 승인되면 기존 `Sponsor` 엔티티로 실제 데이터가 생성되어 `GET /api/v1/sponsors`(회원용 공개 API)에 노출됩니다. 기존 `Sponsor`, `SponsorController`, `SponsorService`는 그대로 유지합니다.

---

## 2. 패키지 구조

```
domain/sponsor/
 ├─ (기존) Sponsor.java, SponsorTier.java, SponsorRepository.java, SponsorService.java
 ├─ (기존) SponsorController.java, AdminSponsorController.java
 ├─ SponsorApplication.java              ← 신규
 ├─ SponsorApplicationStatus.java        ← 신규
 ├─ SponsorApplicationRepository.java    ← 신규
 ├─ SponsorApplicationService.java       ← 신규
 ├─ SponsorApplicationController.java    ← 신규 (공개 API)
 ├─ AdminSponsorApplicationController.java ← 신규 (관리자 승인/반려)
 └─ dto/
     ├─ (기존) SponsorRequest.java, SponsorResponse.java
     ├─ SponsorApplicationRequest.java   ← 신규
     ├─ SponsorApplicationResponse.java  ← 신규
     └─ RejectRequest.java (이미 event 패키지에 있다면 공용으로 옮겨도 무방, 없으면 새로 생성)
```

---

## 3. SponsorApplicationStatus (enum)

```java
package com.seowonfc.api.domain.sponsor;

public enum SponsorApplicationStatus { PENDING, APPROVED, REJECTED }
```

---

## 4. SponsorApplication (Entity)

로그인 회원이 아니므로 `User`를 참조하지 않고, 신청자 정보를 필드로 직접 저장합니다.

```java
package com.seowonfc.api.domain.sponsor;

import com.seowonfc.api.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SponsorApplication extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false)
    private String contactName;

    @Column(nullable = false)
    private String contactEmail;

    private String contactPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SponsorTier desiredTier;

    private String logoUrl;

    @Lob
    private String message;

    private String linkUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SponsorApplicationStatus status;

    private String rejectReason;

    @Builder
    public SponsorApplication(String companyName, String contactName, String contactEmail,
                               String contactPhone, SponsorTier desiredTier, String logoUrl,
                               String message, String linkUrl) {
        this.companyName = companyName;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.desiredTier = desiredTier;
        this.logoUrl = logoUrl;
        this.message = message;
        this.linkUrl = linkUrl;
        this.status = SponsorApplicationStatus.PENDING;
    }

    public void approve() {
        this.status = SponsorApplicationStatus.APPROVED;
    }

    public void reject(String reason) {
        this.status = SponsorApplicationStatus.REJECTED;
        this.rejectReason = reason;
    }
}
```

---

## 5. SponsorApplicationRepository

```java
package com.seowonfc.api.domain.sponsor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SponsorApplicationRepository extends JpaRepository<SponsorApplication, Long> {
    Page<SponsorApplication> findByStatus(SponsorApplicationStatus status, Pageable pageable);
}
```

---

## 6. DTO

```java
package com.seowonfc.api.domain.sponsor.dto;

import com.seowonfc.api.domain.sponsor.SponsorTier;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SponsorApplicationRequest(
        @NotBlank String companyName,
        @NotBlank String contactName,
        @Email @NotBlank String contactEmail,
        String contactPhone,
        @NotNull SponsorTier desiredTier,
        String message,
        String linkUrl
) {}
```

```java
package com.seowonfc.api.domain.sponsor.dto;

import com.seowonfc.api.domain.sponsor.SponsorApplication;
import com.seowonfc.api.domain.sponsor.SponsorApplicationStatus;
import com.seowonfc.api.domain.sponsor.SponsorTier;
import java.time.LocalDateTime;

public record SponsorApplicationResponse(
        Long id, String companyName, String contactName, String contactEmail, String contactPhone,
        SponsorTier desiredTier, String logoUrl, String message, String linkUrl,
        SponsorApplicationStatus status, String rejectReason, LocalDateTime createdAt
) {
    public static SponsorApplicationResponse from(SponsorApplication a) {
        return new SponsorApplicationResponse(a.getId(), a.getCompanyName(), a.getContactName(),
                a.getContactEmail(), a.getContactPhone(), a.getDesiredTier(), a.getLogoUrl(),
                a.getMessage(), a.getLinkUrl(), a.getStatus(), a.getRejectReason(), a.getCreatedAt());
    }
}
```

> `RejectRequest`는 이벤트 기능에서 이미 만드셨다면 (`record RejectRequest(String reason) {}`) 그대로 재사용합니다. 없다면 `domain/sponsor/dto`에 동일하게 하나 만듭니다.

---

## 7. SponsorApplicationService

```java
package com.seowonfc.api.domain.sponsor;

import com.seowonfc.api.common.CustomException;
import com.seowonfc.api.common.ErrorCode;
import com.seowonfc.api.domain.image.ImageUploadService;
import com.seowonfc.api.domain.sponsor.dto.SponsorApplicationRequest;
import com.seowonfc.api.domain.sponsor.dto.SponsorApplicationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SponsorApplicationService {

    private final SponsorApplicationRepository applicationRepository;
    private final SponsorRepository sponsorRepository;
    private final ImageUploadService imageUploadService;

    @Transactional
    public Long apply(SponsorApplicationRequest request, MultipartFile file) {
        String logoUrl = null;
        if (file != null && !file.isEmpty()) {
            logoUrl = imageUploadService.upload(file, "sponsor-applications").url();
        }

        SponsorApplication application = SponsorApplication.builder()
                .companyName(request.companyName())
                .contactName(request.contactName())
                .contactEmail(request.contactEmail())
                .contactPhone(request.contactPhone())
                .desiredTier(request.desiredTier())
                .logoUrl(logoUrl)
                .message(request.message())
                .linkUrl(request.linkUrl())
                .build();

        return applicationRepository.save(application).getId();
    }

    // ---- 관리자용 ----
    public Page<SponsorApplicationResponse> getPending(Pageable pageable) {
        return applicationRepository.findByStatus(SponsorApplicationStatus.PENDING, pageable)
                .map(SponsorApplicationResponse::from);
    }

    @Transactional
    public Long approve(Long applicationId) {
        SponsorApplication application = findById(applicationId);
        application.approve();

        Sponsor sponsor = Sponsor.builder()
                .name(application.getCompanyName())
                .logoUrl(application.getLogoUrl())
                .tier(application.getDesiredTier())
                .linkUrl(application.getLinkUrl())
                .build();

        return sponsorRepository.save(sponsor).getId();
    }

    @Transactional
    public void reject(Long applicationId, String reason) {
        findById(applicationId).reject(reason);
    }

    private SponsorApplication findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }
}
```

---

## 8. SponsorApplicationController — 공개 API (로그인 불필요)

```java
package com.seowonfc.api.domain.sponsor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.sponsor.dto.SponsorApplicationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "[공개] Sponsor Application", description = "스폰서 신청 API (로그인 불필요)")
@RestController
@RequestMapping("/api/v1/sponsor-applications")
@RequiredArgsConstructor
public class SponsorApplicationController {

    private final SponsorApplicationService applicationService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "스폰서 신청 (누구나 가능, 로그인 불필요)")
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<Long> apply(
            @RequestParam("data") String data,
            @RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
        SponsorApplicationRequest request = objectMapper.readValue(data, SponsorApplicationRequest.class);
        return ApiResponse.success(applicationService.apply(request, file));
    }
}
```

뉴스/이벤트와 동일하게 `data`(JSON 문자열)는 `@RequestParam` + `ObjectMapper` 방식을 씁니다 (Swagger/Blob 호환성 문제를 이미 겪으셨으니 동일 패턴 유지).

---

## 9. AdminSponsorApplicationController — 관리자 승인/반려

```java
package com.seowonfc.api.domain.sponsor;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.sponsor.dto.SponsorApplicationResponse;
import com.seowonfc.api.domain.event.dto.RejectRequest; // 이미 있는 RejectRequest 재사용 시 (패키지 경로는 실제 위치에 맞게 조정)
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[관리자] Sponsor Application", description = "스폰서 신청 승인/반려 API")
@RestController
@RequestMapping("/api/v1/admin/sponsor-applications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminSponsorApplicationController {

    private final SponsorApplicationService applicationService;

    @Operation(summary = "대기중인 스폰서 신청 목록")
    @GetMapping
    public ApiResponse<Page<SponsorApplicationResponse>> getPending(
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(applicationService.getPending(pageable));
    }

    @Operation(summary = "스폰서 신청 승인 (Sponsor로 등록)")
    @PostMapping("/{applicationId}/approve")
    public ApiResponse<Long> approve(@PathVariable Long applicationId) {
        return ApiResponse.success(applicationService.approve(applicationId));
    }

    @Operation(summary = "스폰서 신청 반려")
    @PostMapping("/{applicationId}/reject")
    public ApiResponse<Void> reject(@PathVariable Long applicationId, @RequestBody RejectRequest request) {
        applicationService.reject(applicationId, request.reason());
        return ApiResponse.success(null);
    }
}
```

> `RejectRequest`가 `domain.event.dto`에 있다면 import 경로 그대로 재사용하되, 패키지 간 참조가 어색하다고 판단되면 `domain.sponsor.dto`에 동일한 record를 하나 더 만들어도 무방합니다 (Codex 판단에 맡깁니다).

---

## 10. SecurityConfig 수정 — 신청 API를 공개(permitAll)로 열기

```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
    .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
    .requestMatchers("/api/v1/sponsor-applications").permitAll()   // ← 이 줄 추가
    .requestMatchers(HttpMethod.GET, "/api/v1/news/**", "/api/v1/players/**",
        "/api/v1/matches/**", "/api/v1/standings/**", "/api/v1/sponsors/**", "/api/v1/events/**"
    ).permitAll()
    .requestMatchers("/api/v1/auth/**").permitAll()
    .anyRequest().authenticated()
)
```

**중요**: `/api/v1/admin/**`(ADMIN 전용 매처)가 먼저 걸려 있어야 하고, `/api/v1/sponsor-applications`는 그 아래에 별도로 추가합니다. 순서가 바뀌면 안 됩니다 — Spring Security는 위에서부터 순서대로 매칭하기 때문에, 관리자 승인/반려 경로(`/api/v1/admin/sponsor-applications/**`)가 먼저 ADMIN 매처에 걸려서 보호되고, 공개 신청 경로(`/api/v1/sponsor-applications`, admin 없음)만 permitAll이 적용됩니다.

---

## 11. 적용 및 확인 순서

1. 위 파일들 순서대로(Status → Entity → Repository → DTO → Service → Controller → SecurityConfig) 적용
2. 재실행
3. **로그인 없이** Swagger에서 `POST /api/v1/sponsor-applications` 테스트 (Authorize 안 하고 바로 호출해도 성공해야 정상)
   - `data`: `{"companyName":"테스트 상사","contactName":"홍길동","contactEmail":"test@test.com","contactPhone":"010-0000-0000","desiredTier":"PARTNER","message":"후원하고 싶습니다","linkUrl":"https://example.com"}`
   - `file`: 로고 이미지 선택 (선택사항)
4. ADMIN 토큰으로 `GET /api/v1/admin/sponsor-applications`에서 방금 신청이 보이는지 확인
5. `POST /api/v1/admin/sponsor-applications/{id}/approve` 호출
6. `GET /api/v1/sponsors`에서 승인된 스폰서가 실제로 노출되는지 확인
7. 커밋

```bash
git add .
git commit -m "feat: add public sponsor application and approval workflow"
git push
```

---

## 12. 프론트엔드 반영 참고 (별도 지시 필요)

- **공개 페이지** (`/sponsors/apply`) — 로그인 여부와 무관하게 접근 가능한 스폰서 신청 폼. 회사명/담당자명/이메일/연락처/희망등급/로고파일/소개메시지/링크 입력
- 스폰서 목록 페이지(`/sponsors`) 하단에 "스폰서 신청하기" 버튼 추가
- **관리자 페이지** (`/admin/sponsor-applications`) — 대기중인 신청 목록 + 승인/반려 버튼 (선수 등록 신청 관리 화면과 동일한 UI 패턴 재사용 권장)
- API 호출 방식은 뉴스/이벤트와 동일한 `multipart/form-data` + `JSON.stringify` 패턴(Blob으로 감싸지 않고 문자열 그대로)을 사용합니다.

이 부분은 프론트엔드 지시서(`CODEX_개발_지시서.md`)에도 반영해서 별도로 Codex에게 지시하는 것을 권장합니다.
