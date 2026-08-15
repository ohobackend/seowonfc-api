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
    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "image/webp", "image/gif");
    private final Cloudinary cloudinary;

    public ImageUploadResponse upload(MultipartFile file, String folder) {
        validate(file);
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "seowonfc/" + (folder == null ? "misc" : folder)));
            return new ImageUploadResponse((String) result.get("secure_url"));
        } catch (IOException e) {
            throw new CustomException(ErrorCode.INTERNAL_ERROR);
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty() || !ALLOWED_TYPES.contains(file.getContentType())) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
    }
}
