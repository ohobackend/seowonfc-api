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
