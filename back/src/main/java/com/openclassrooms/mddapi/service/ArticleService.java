package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.model.Article;

import java.util.List;
import java.util.Optional;

/**
 * Service pour gérer les articles.
 */
public interface ArticleService {
    List<Article> getAllArticles(String sortBy, String order);
    List<Article> getArticlesByTopicId(Long topicId);
    Optional<Article> getArticleById(Long id);
    Article saveArticle(Article article);
}
