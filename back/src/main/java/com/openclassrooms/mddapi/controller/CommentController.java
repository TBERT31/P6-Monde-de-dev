package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.exception.NotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.openclassrooms.mddapi.exception.ForbiddenException;
import com.openclassrooms.mddapi.security.jwt.JwtUtils;
import com.openclassrooms.mddapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.openclassrooms.mddapi.service.CommentService;
import com.openclassrooms.mddapi.mapper.CommentMapper;
import com.openclassrooms.mddapi.dto.CommentDto;
import com.openclassrooms.mddapi.model.Comment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour les opérations liées aux commentaires.
 */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
@Tag(name = "Comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentMapper commentMapper;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    /**
     * Récupère un commentaire par son identifiant.
     * @param id l'identifiant du commentaire.
     * @return le commentaire.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id) {
        try {
            // Récupère le commentaire par son identifiant.
            Optional<Comment> optionalComment = commentService.getCommentById(id);

            // Vérifie si le commentaire existe.
            if (optionalComment.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Récupère le commentaire.
            Comment comment = optionalComment.get();

            return ResponseEntity.ok().body(commentMapper.toDto(comment));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Récupère les commentaires par l'identifiant de l'article.
     * @param articleId l'identifiant de l'article.
     * @return une liste de commentaires.
     */
    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<CommentDto>> getCommentByArticleId(
            @PathVariable Long articleId
    ) {
        try{
            List<Comment> comments = commentService.getCommentsByArticleId(articleId);
            return ResponseEntity.ok(commentMapper.toDto(comments));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Crée un nouveau commentaire.
     * @param commentDto les données du commentaire à créer.
     * @param token le jeton d'authentification de l'utilisateur.
     * @return le commentaire créé.
     */
    @PostMapping("")
    public ResponseEntity<CommentDto> createComment(
            @Valid @RequestBody CommentDto commentDto,
            @RequestHeader("Authorization") String token
    ) {
        // Récupère l'utilisateur à partir du jeton d'authentification.
        String jwt = token.substring(7);
        String emailJwt = jwtUtils.getUserNameFromJwtToken(jwt);

        // Vérifie si l'auteur de l'article existe.
        userService.getUserByUsername(commentDto.getUsername())
                //(A méditer, car cela fournit bcp d'information sur nos users...)
                //.orElseThrow(() -> new NotFoundException("User not found with username: " + commentDto.getUsername()));

                // Permet de duper le hacker qui se croit malin
                .orElseThrow(() -> new ForbiddenException("You are not allowed to create a comment for another user"));

        Comment comment = commentService.createComment(commentMapper.toEntity(commentDto), emailJwt);
        return ResponseEntity.ok(commentMapper.toDto(comment));
    }

}