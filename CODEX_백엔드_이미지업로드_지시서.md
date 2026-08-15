# 서원 FC 백엔드 — 이미지 업로드 기능 개발 지시서 (Codex용)

> 이 문서는 Codex가 `seowonfc-api` 프로젝트에 이미지 업로드 기능을 추가할 때 참고하는 지시서입니다.
> 문서 전체를 읽고, 아래 코드를 그대로(또는 기존 코드 스타일에 맞춰) 적용해주세요.

---

## 1. 배경 및 설계 방향

- 현재 `News.thumbnailUrl`, `Player.profileImageUrl`, `Sponsor.logoUrl` 등은 전부 **이미지 주소(URL) 문자열**을 저장하는 구조입니다.
- PostgreSQL에 이미지 파일 자체를 저장하지 않고, **외부 이미지 호스팅 서비스(Cloudinary)**에 업로드한 뒤 그 URL만 DB에 저장합니다.
- 따라서 **기존 도메인(News/Player/Sponsor/Event)의 Entity, DTO, Service, Controller는 전혀 수정하지 않습니다.**
- 새로 추가하는 건 **"파일을 업로드하면 URL을 돌려주는" 공용 이미지 업로드 API 하나**뿐입니다.

### 관리자 작업 흐름 (프론트엔드 기준)

1. 관리자가 뉴스/선수/스폰서 등록 폼에서 이미지 파일 선택
2. 폼 제출 전, 이미지 업로드 API(`POST /api/v1/admin/images`)를 먼저 호출해 파일을 업로드
3. 응답으로 받은 URL을 `thumbnailUrl` / `profileImageUrl` / `logoUrl` 필드에 넣어서, 기존 등록 API(`POST /api/v1/admin/news` 등)를 그대로 호출

---

## 2. 사전 준비 — Cloudinary 계정

Codex는 코드만 작성하고, 실제 계정 생성과 키 발급은 사람이 진행합니다. 아래는 참고용 안내이며, Codex는 **환경변수로 값을 주입받는 코드**만 작성하면 됩니다.

- [https://cloudinary.com](https://cloudinary.com) 무료 가입 후 대시보드에서 아래 3개 값을 확인:
  - `Cloud name`
  - `API Key`
  - `API Secret`
- 로컬 개발 시 `application.yaml`에, 배포 시 Render 환경변수에 아래 3개를 등록:
  ```
  CLOUDINARY_CLOUD_NAME=xxxxx
  CLOUDINARY_API_KEY=xxxxx
  CLOUDINARY_API_SECRET=xxxxx
  ```

---

## 3. build.gradle 의존성 추가

```groovy
dependencies {
    // ... 기존 의존성 전부 유지 ...

    // 이미지 업로드 (Cloudinary)
    implementation 'com.cloudinary:cloudinary-http5:2.0.0'
}
```

---

## 4. application.yaml / application-prod.yaml 설정 추가

### application.yaml (로컬)

```yaml
cloudinary:
  cloud-name: ${CLOUDINARY_CLOUD_NAME}
  api-key: ${CLOUDINARY_API_KEY}
  api-secret: ${CLOUDINARY_API_SECRET}

spring:
  servlet:
    multipart:
      max-file-size: 5MB
      max-request-size: 5MB
```

### application-prod.yaml (배포)

```yaml
cloudinary:
  cloud-name: ${CLOUDINARY_CLOUD_NAME}
  api-key: ${CLOUDINARY_API_KEY}
  api-secret: ${CLOUDINARY_API_SECRET}

spring:
  servlet:
    multipart:
      max-file-size: 5MB
      max-request-size: 5MB
```

기존 `spring:` 블록이 이미 있다면, `servlet.multipart` 항목만 그 안에 추가합니다(중복 `spring:` 키를 만들지 않도록 주의).

---

## 5. 패키지 구조

```
domain/image/
 ├─ ImageUploadController.java
 ├─ ImageUploadService.java
 └─ dto/
     └─ ImageUploadResponse.java

config/
 └─ CloudinaryConfig.java
```

---

## 6. CloudinaryConfig

```java
package com.seowonfc.api.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }
}
```

---

## 7. DTO

```java
package com.seowonfc.api.domain.image.dto;

public record ImageUploadResponse(String url) {}
```

---

## 8. ImageUploadService

```java
package com.seowonfc.api.domain.image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.seowonfc.api.common.CustomException;
import com.seowonfc.api.common.ErrorCode;
import com.seowonfc.api.domain.image.dto.ImageUploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private static final List<String> ALLOWED_TYPES =
            List.of("image/jpeg", "image/png", "image/webp", "image/gif");

    private final Cloudinary cloudinary;

    public ImageUploadResponse upload(MultipartFile file, String folder) {
        validate(file);

        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("folder", "seowonfc/" + (folder == null ? "misc" : folder))
            );
            String url = (String) result.get("secure_url");
            return new ImageUploadResponse(url);
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
    }
}
```

---

## 9. ImageUploadController — 관리자 전용

```java
package com.seowonfc.api.domain.image;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.image.dto.ImageUploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "[관리자] Image", description = "이미지 업로드 API")
@RestController
@RequestMapping("/api/v1/admin/images")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ImageUploadController {

    private final ImageUploadService imageUploadService;

    @Operation(summary = "이미지 업로드 (뉴스/선수/스폰서 등 공용, folder로 용도 구분)")
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<ImageUploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String folder) {
        return ApiResponse.success(imageUploadService.upload(file, folder));
    }
}
```

`folder` 파라미터는 Cloudinary 안에서 이미지를 용도별로 정리하기 위한 값입니다. 프론트엔드에서 호출할 때 아래처럼 구분해서 넘기도록 안내합니다.

| 용도 | folder 값 예시 |
|---|---|
| 뉴스 썸네일 | `news` |
| 선수 프로필 | `players` |
| 스폰서 로고 | `sponsors` |
| 이벤트 이미지 | `events` |

---

## 10. SecurityConfig 수정

`/api/v1/admin/**`는 이미 `hasRole("ADMIN")`으로 전체 차단되어 있으므로, 별도 매처 추가 없이 **컨트롤러의 `@PreAuthorize`만으로 충분**합니다. 다만 아래를 확인합니다.

- `@EnableMethodSecurity`가 `SecurityConfig`에 이미 붙어있는지 확인 (없으면 추가)
- multipart 요청도 `/api/v1/admin/**` 패턴에 포함되므로 별도 예외 처리 불필요

---

## 11. 프론트엔드 연동 참고 (Codex가 프론트엔드 작업도 겸한다면)

프론트엔드(`seowonfc-frontend`)의 관리자 등록/수정 폼(뉴스/선수/스폰서)에 아래 흐름을 추가합니다.

1. 폼에 `<input type="file" accept="image/*" />` 추가
2. 파일 선택 시 즉시 또는 "저장" 클릭 시, `FormData`로 아래 API 호출

```ts
// src/api/images.ts
import client from './client';

export async function uploadImage(file: File, folder: string): Promise<string> {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('folder', folder);

  const res = await client.post('/admin/images', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return res.data.data.url;
}
```

3. 반환된 URL을 폼 상태의 `thumbnailUrl`(또는 `profileImageUrl`, `logoUrl`)에 넣고, 기존 등록/수정 API를 그대로 호출

이 부분은 백엔드 작업이 끝난 뒤, 프론트엔드 지시서(`CODEX_개발_지시서.md`)에도 동일 내용을 추가해서 별도로 지시하는 것을 권장합니다.

---

## 12. 적용 및 확인 순서

1. `build.gradle`에 Cloudinary 의존성 추가 → Gradle Sync
2. `application.yaml` / `application-prod.yaml`에 `cloudinary.*`, `multipart` 설정 추가
3. `CloudinaryConfig`, `ImageUploadService`, `ImageUploadController`, DTO 파일 생성
4. 로컬 `.env` 또는 `application.yaml`에 Cloudinary 3개 값 입력 (사람이 직접 값 채워야 함 — Codex는 플레이스홀더로 남겨둠)
5. 로컬 실행 → Swagger에서 `[관리자] Image` 그룹의 `POST /api/v1/admin/images` 확인
6. ADMIN 토큰으로 Authorize → 실제 이미지 파일 하나 업로드 테스트 → 응답 `url` 값이 `https://res.cloudinary.com/...` 형태로 오는지 확인
7. 그 URL을 `POST /api/v1/admin/news`의 `thumbnailUrl`에 넣어서 뉴스 등록 → `GET /api/v1/news`에서 이미지가 정상 표시되는지(URL 접속 시 이미지 뜨는지) 확인
8. 커밋

```bash
git add .
git commit -m "feat: add admin image upload feature via Cloudinary"
git push
```

9. 배포 시 Render 환경변수에 `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`, `CLOUDINARY_API_SECRET` 3개 추가 등록

---

## 13. 하지 말아야 할 것

- ❌ 이미지 파일을 PostgreSQL(BYTEA/LOB)에 직접 저장하지 않는다
- ❌ 업로드 API를 회원용(`/api/v1/**`)에 노출하지 않는다 — 반드시 `/api/v1/admin/**`, ADMIN 전용
- ❌ 파일 크기/타입 검증 없이 무제한 업로드를 허용하지 않는다 (위 코드의 `ALLOWED_TYPES`, `max-file-size` 유지)
- ❌ 기존 News/Player/Sponsor/Event의 Entity·DTO·Controller를 이미지 업로드 때문에 수정하지 않는다 (URL 문자열 필드는 이미 있으므로 그대로 재사용)
