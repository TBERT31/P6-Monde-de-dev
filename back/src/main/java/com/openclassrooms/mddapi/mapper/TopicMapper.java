package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.TopicDto;
import com.openclassrooms.mddapi.model.Article;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.service.ArticleService;
import com.openclassrooms.mddapi.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring",
        uses = {ArticleService.class, UserService.class},
        imports = {Arrays.class, Collectors.class, Article.class,
                Topic.class, User.class, Collections.class, Optional.class}
)
public abstract class TopicMapper implements EntityMapper<TopicDto, Topic>   {

    @Autowired
    ArticleService articleService;

    @Autowired
    UserService userService;


    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "description", target = "description"),
            @Mapping(target = "articles", expression = "java(mapToArticles(topicDto.getArticles()))"),
            @Mapping(target = "users_subscribed", expression = "java(mapToUsers(topicDto.getUsers_subscribed()))"),
            @Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy/MM/dd"),
            @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = "yyyy/MM/dd")
    })
    public abstract Topic toEntity(TopicDto topicDto);

    /**
     * Convertit une liste d'identifiants d'articles en une liste d'Articles.
     * @param articleIds la liste d'identifiants d'articles à convertir.
     * @return la liste d'Articles convertie.
     */
    public List<Article> mapToArticles(List<Long> articleIds) {
        return Optional.ofNullable(articleIds)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(articleService::getArticleById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une liste d'identifiants d'utilisateurs en une liste d'Users.
     * @param userIds la liste d'identifiants d'utilisateurs à convertir.
     * @return la liste d'Users convertie.
     */
    public List<User> mapToUsers(List<Long> userIds) {
        return Optional.ofNullable(userIds)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(userService::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "title", target = "title"),
            @Mapping(source = "description", target = "description"),
            @Mapping(target = "articles", expression = "java(mapToArticleIds(topic.getArticles()))"),
            @Mapping(target = "users_subscribed", expression = "java(mapToUserIds(topic.getUsers_subscribed()))"),
            @Mapping(source = "createdAt", target = "createdAt", dateFormat = "yyyy/MM/dd"),
            @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = "yyyy/MM/dd")
    })
    public abstract TopicDto toDto(Topic topic);

    /**
     * Convertit une liste d'Articles en une liste d'identifiants d'articles.
     * @param articles la liste d'Articles à convertir.
     * @return la liste d'identifiants d'articles convertie.
     */
    public List<Long> mapToArticleIds(List<Article> articles) {
        return Optional.ofNullable(articles)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(Article::getId)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une liste d'Users en une liste d'identifiants d'utilisateurs.
     * @param users la liste d'Users à convertir.
     * @return la liste d'identifiants d'utilisateurs convertie.
     */
    public List<Long> mapToUserIds(List<User> users) {
        return Optional.ofNullable(users)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

}