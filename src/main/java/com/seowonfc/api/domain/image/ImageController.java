package com.seowonfc.api.domain.image;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.image.dto.ImageUploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "[회원] Image", description = "이미지 업로드 API (로그인 회원 전체 사용 가능)")
@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageUploadService imageUploadService;

    @Operation(summary = "이미지 업로드 (뉴스/선수/스폰서/선수등록신청 등 공용, folder로 용도 구분)")
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<ImageUploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String folder) {
        return ApiResponse.success(imageUploadService.upload(file, folder));
    }
}
