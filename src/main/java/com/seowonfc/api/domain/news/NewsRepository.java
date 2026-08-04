package com.seowonfc.api.domain.news;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
    Page<News> findByCategory(NewsCategory category, Pageable pageable);
    Page<News> findByTitleContaining(String keyword, Pageable pageable);
}