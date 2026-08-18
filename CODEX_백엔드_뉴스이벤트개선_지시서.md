# 서원 FC 백엔드 — 뉴스/이벤트 기능 개선 지시서 (Codex용)

> 이 문서는 아래 두 가지를 `seowonfc-api` 프로젝트에 적용하기 위한 지시서입니다.
> 1. 뉴스 등록/수정 시 이미지 URL 대신 **이미지 파일을 직접 업로드**할 수 있도록 변경
> 2. 이벤트 등록/수정 시 이미지 파일 업로드 추가, `startDate`/`endDate` → **단일 날짜(`eventDate`)**로 변경, **등록일 기준 3일 경과 시 자동 삭제 배치** 추가

---

## 공통 원칙

- 기존 `POST /api/v1/images` (로그인 회원 누구나 사용 가능한 업로드 API, `ImageUploadService`)를 **그대로 재사용**합니다. 새로 만들지 않습니다.
- 뉴스/이벤트 등록·수정 API는 기존 `application/json` 방식에서 **`multipart/form-data`** 방식으로 바뀝니다. JSON 데이터와 파일을 한 번의 요청으로 함께 보냅니다.

---

## 1. 뉴스 — 이미지 파일 직접 업로드

### 1-1. NewsRequest 수정

`thumbnailUrl` 필드는 그대로 두되(파일을 안 넣고 외부 URL만 쓰고 싶은 경우를 위한 fallback), **필수가 아니게(nullable)** 유지합니다. 구조 자체는 변경 없습니다.

```java
package com.seowonfc.api.domain.news.dto;

import com.seowonfc.api.domain.news.NewsCategory;
import jakarta.validation.constraints.NotBlank;

public record NewsRequest(
        @NotBlank String title,
        @NotBlank String content,
        NewsCategory category,
        String thumbnailUrl   // 파일 첨부 시 서버가 이 값을 덮어씀. 파일 없으면 그대로 사용(선택)
) {}
```

### 1-2. NewsService — ImageUploadService 주입 및 파일 처리 로직 추가

```java
package com.seowonfc.api.domain.news;

import com.seowonfc.api.common.CustomException;
import com.seowonfc.api.common.ErrorCode;
import com.seowonfc.api.domain.image.ImageUploadService;
import com.seowonfc.api.domain.news.dto.NewsRequest;
import com.seowonfc.api.domain.news.dto.NewsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {

    private final NewsRepository newsRepository;
    private final ImageUploadService imageUploadService;

    public Page<NewsResponse> getList(NewsCategory category, Pageable pageable) {
        Page<News> page = (category == null)
                ? newsRepository.findAll(pageable)
                : newsRepository.findByCategory(category, pageable);
        return page.map(NewsResponse::from);
    }

    public NewsResponse getDetail(Long id) {
        News news = findById(id);
        news.increaseView();
        return NewsResponse.from(news);
    }

    @Transactional
    public Long create(NewsRequest request, MultipartFile file) {
        String thumbnailUrl = resolveThumbnailUrl(request.thumbnailUrl(), file, "news");
        News news = News.builder()
                .title(request.title())
                .content(request.content())
                .category(request.category())
                .thumbnailUrl(thumbnailUrl)
                .build();
        return newsRepository.save(news).getId();
    }

    @Transactional
    public void update(Long id, NewsRequest request, MultipartFile file) {
        News news = findById(id);
        String thumbnailUrl = resolveThumbnailUrl(request.thumbnailUrl(), file, "news");
        news.update(request.title(), request.content(), request.category(), thumbnailUrl);
    }

    @Transactional
    public void delete(Long id) {
        newsRepository.delete(findById(id));
    }

    /** 파일이 있으면 업로드해서 새 URL을 쓰고, 없으면 기존/요청받은 URL을 그대로 유지 */
    private String resolveThumbnailUrl(String requestedUrl, MultipartFile file, String folder) {
        if (file != null && !file.isEmpty()) {
            return imageUploadService.upload(file, folder).url();
        }
        return requestedUrl;
    }

    private News findById(Long id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
    }
}
```

### 1-3. AdminNewsController — multipart로 변경

```java
package com.seowonfc.api.domain.news;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.news.dto.NewsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "[관리자] News", description = "구단 뉴스 관리 API")
@RestController
@RequestMapping("/api/v1/admin/news")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminNewsController {

    private final NewsService newsService;

    @Operation(summary = "뉴스 등록 (이미지 파일 첨부 가능)")
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<Long> create(
            @RequestPart("data") @Valid NewsRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return ApiResponse.success(newsService.create(request, file));
    }

    @Operation(summary = "뉴스 수정 (이미지 파일 첨부 가능)")
    @PutMapping(value = "/{newsId}", consumes = "multipart/form-data")
    public ApiResponse<Void> update(
            @PathVariable Long newsId,
            @RequestPart("data") @Valid NewsRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        newsService.update(newsId, request, file);
        return ApiResponse.success(null);
    }

    @Operation(summary = "뉴스 삭제")
    @DeleteMapping("/{newsId}")
    public ApiResponse<Void> delete(@PathVariable Long newsId) {
        newsService.delete(newsId);
        return ApiResponse.success(null);
    }
}
```

### 1-4. 프론트엔드에서 호출하는 방식 (참고)

`data` 파트는 JSON 문자열을 `Blob`으로 감싸서 `application/json` 타입으로 보내야 서버가 `@RequestPart`로 정상 파싱합니다.

```ts
const formData = new FormData();
formData.append('data', new Blob([JSON.stringify({
  title, content, category, thumbnailUrl: null
})], { type: 'application/json' }));
if (file) formData.append('file', file);

await client.post('/admin/news', formData, {
  headers: { 'Content-Type': 'multipart/form-data' },
});
```

Swagger UI에서 테스트할 때도 `data` 파트에는 JSON을, `file` 파트에는 실제 이미지 파일을 넣으면 됩니다.

---

## 2. 이벤트 — 이미지 업로드 + 단일 날짜 + 3일 후 자동 삭제

### 2-1. Event Entity 수정

`startDate`, `endDate`를 지우고 `eventDate`(단일 날짜) 및 `imageUrl`을 추가합니다.

```java
package com.seowonfc.api.domain.event;

import com.seowonfc.api.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    private String content;

    @Column(nullable = false)
    private LocalDate eventDate;

    private String imageUrl;

    @Builder
    public Event(String title, String content, LocalDate eventDate, String imageUrl) {
        this.title = title;
        this.content = content;
        this.eventDate = eventDate;
        this.imageUrl = imageUrl;
    }

    public void update(String title, String content, LocalDate eventDate, String imageUrl) {
        this.title = title;
        this.content = content;
        this.eventDate = eventDate;
        this.imageUrl = imageUrl;
    }
}
```

> 기존에 `startDate`/`endDate`를 쓰던 코드(Service, DTO, Controller)에서 참조하는 부분을 전부 `eventDate` 하나로 교체합니다.

### 2-2. EventRepository — 삭제 대상 조회 메서드 추가

```java
package com.seowonfc.api.domain.event;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByEventDateBefore(LocalDate date);
}
```

### 2-3. EventRequest / EventResponse 수정

```java
package com.seowonfc.api.domain.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record EventRequest(
        @NotBlank String title,
        @NotBlank String content,
        @NotNull LocalDate eventDate,
        String imageUrl
) {}
```

```java
package com.seowonfc.api.domain.event.dto;

import com.seowonfc.api.domain.event.Event;
import java.time.LocalDate;

public record EventResponse(
        Long id, String title, String content, LocalDate eventDate, String imageUrl
) {
    public static EventResponse from(Event event) {
        return new EventResponse(event.getId(), event.getTitle(), event.getContent(),
                event.getEventDate(), event.getImageUrl());
    }
}
```

### 2-4. EventService 수정 — 이미지 처리 + create/update 시그니처 변경

기존 `EventService`에서 `create`, `update` 메서드만 아래처럼 바꿉니다 (나머지 `apply`, `getWinners`, `selectWinners` 등은 그대로 유지).

```java
private final ImageUploadService imageUploadService; // 필드 추가 (생성자 주입)

@Transactional
public Long create(EventRequest request, MultipartFile file) {
    String imageUrl = resolveImageUrl(request.imageUrl(), file);
    Event event = Event.builder()
            .title(request.title())
            .content(request.content())
            .eventDate(request.eventDate())
            .imageUrl(imageUrl)
            .build();
    return eventRepository.save(event).getId();
}

@Transactional
public void update(Long eventId, EventRequest request, MultipartFile file) {
    Event event = findEvent(eventId);
    String imageUrl = resolveImageUrl(request.imageUrl(), file);
    event.update(request.title(), request.content(), request.eventDate(), imageUrl);
}

private String resolveImageUrl(String requestedUrl, MultipartFile file) {
    if (file != null && !file.isEmpty()) {
        return imageUploadService.upload(file, "events").url();
    }
    return requestedUrl;
}
```

필요한 import: `com.seowonfc.api.domain.image.ImageUploadService`, `org.springframework.web.multipart.MultipartFile`

### 2-5. AdminEventController 수정 — multipart로 변경

```java
@Operation(summary = "이벤트 등록 (이미지 파일 첨부 가능)")
@PostMapping(consumes = "multipart/form-data")
public ApiResponse<Long> create(
        @RequestPart("data") @Valid EventRequest request,
        @RequestPart(value = "file", required = false) MultipartFile file) {
    return ApiResponse.success(eventService.create(request, file));
}

@Operation(summary = "이벤트 수정 (이미지 파일 첨부 가능)")
@PutMapping(value = "/{eventId}", consumes = "multipart/form-data")
public ApiResponse<Void> update(
        @PathVariable Long eventId,
        @RequestPart("data") @Valid EventRequest request,
        @RequestPart(value = "file", required = false) MultipartFile file) {
    eventService.update(eventId, request, file);
    return ApiResponse.success(null);
}
```

(`delete`, `selectWinners`는 기존 그대로 유지)

### 2-6. 자동 삭제 배치 — EventCleanupScheduler

**"3일 지나면 자동 삭제"**는 이벤트 날짜(`eventDate`) 기준으로, 그 날짜로부터 3일이 지난 이벤트를 매일 새벽 자동으로 지우는 방식으로 구현합니다.

```java
package com.seowonfc.api.domain.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventCleanupScheduler {

    private final EventRepository eventRepository;
    private final EventEntryRepository eventEntryRepository;

    /** 매일 새벽 3시에 실행 */
    @Scheduled(cron = "0 0 3 * * *")
    public void deleteExpiredEvents() {
        LocalDate cutoff = LocalDate.now().minusDays(3);
        List<Event> expired = eventRepository.findByEventDateBefore(cutoff);

        for (Event event : expired) {
            eventEntryRepository.deleteAll(eventEntryRepository.findByEventId(event.getId()));
            eventRepository.delete(event);
        }

        if (!expired.isEmpty()) {
            log.info("Deleted {} expired events (eventDate before {})", expired.size(), cutoff);
        }
    }
}
```

### 2-7. 스케줄링 활성화

메인 애플리케이션 클래스(`ApiApplication.java`)에 `@EnableScheduling`을 추가합니다.

```java
package com.seowonfc.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
```

---

## 3. 적용 및 확인 순서

1. 뉴스 관련 파일 4개 수정 (`NewsRequest`, `NewsService`, `AdminNewsController`) — 위 코드 그대로 반영
2. 이벤트 관련 파일 수정 (`Event`, `EventRepository`, `EventRequest`, `EventResponse`, `EventService`, `AdminEventController`, `EventCleanupScheduler`)
3. `ApiApplication`에 `@EnableScheduling` 추가
4. 재실행 → 기존 `Event` 테이블에 `start_date`/`end_date` 컬럼이 있다면, `ddl-auto: update`가 새 컬럼(`event_date`, `image_url`)을 추가는 하지만 **기존 컬럼은 자동으로 안 지워집니다.** 개발 단계이므로 필요하면 pgAdmin에서 `event` 테이블을 통째로 지우고 재생성해도 무방합니다 (`DROP TABLE event;` 후 재실행하면 새 스키마로 자동 생성됨)
5. Swagger에서 확인:
   - `[관리자] News`의 `POST /api/v1/admin/news` — `data`(JSON), `file`(이미지) 두 파트로 요청 구성해서 등록 테스트
   - `[관리자] Event`의 `POST /api/v1/admin/events` — 동일하게 `eventDate`(단일 날짜, 예: `2026-08-20`) 포함해서 등록 테스트
6. 배치 테스트(선택): `eventDate`를 오늘로부터 4일 전으로 설정한 이벤트를 하나 만들고, `EventCleanupScheduler`의 cron을 잠깐 `"0 * * * * *"`(매분 실행)로 바꿔서 실제로 삭제되는지 확인 후 다시 원래 cron으로 되돌리기
7. 커밋

```bash
git add .
git commit -m "feat: add image upload to news/events, change event to single date, add auto-cleanup batch"
git push
```

8. Render 재배포 확인

---

## 4. 프론트엔드 반영 필요 사항 (참고, 별도 지시 필요)

- 뉴스/이벤트 등록·수정 폼: 요청 방식을 JSON → multipart(`FormData`)로 변경, 파일 입력(`<input type="file">`) 추가
- 이벤트 등록 폼: "시작일시"/"종료일시" 2개 입력 필드 → **날짜 선택 1개**(`<input type="date">` 또는 date picker)로 변경
- 이 부분은 프론트엔드 지시서(`CODEX_개발_지시서.md`)에도 별도로 반영해서 Codex에게 지시하는 것을 권장합니다.
