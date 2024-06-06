package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Optional<Comment> getCommentById(Long id);
    List<Comment> getCommentsByArticleId(Long articleId);
    Comment saveComment(Comment comment);
}