package com.seowonfc.api.domain.sponsor;

import com.seowonfc.api.common.ApiResponse;
import com.seowonfc.api.domain.sponsor.dto.SponsorApplicationRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;

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
            @RequestParam(value = "file", required = false) MultipartFile file) {
        SponsorApplicationRequest request = objectMapper.readValue(data, SponsorApplicationRequest.class);
        return ApiResponse.success(applicationService.apply(request, file));
    }
}
