package com.seowonfc.api.domain.image;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.image.dto.ImageUploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "[관리자] Image", description = "이미지 업로드 API")
@RestController
@RequestMapping("/api/v1/admin/images")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ImageUploadController {
    private final ImageUploadService imageUploadService;

    @Operation(summary = "공용 이미지 업로드 (folder로 용도 구분)")
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<ImageUploadResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String folder) {
        return ApiResponse.success(imageUploadService.upload(file, folder));
    }
}
