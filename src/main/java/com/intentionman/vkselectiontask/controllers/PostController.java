package com.intentionman.vkselectiontask.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@CrossOrigin
@AllArgsConstructor
public class PostController {
    private static final String COMMON_PATH = "https://jsonplaceholder.typicode.com/posts/";
    @GetMapping("/posts")
    public RedirectView getAllPosts() {
        return new RedirectView(COMMON_PATH);
    }

    @GetMapping("/posts/{posts_id}")
    public RedirectView getPostById(@PathVariable("posts_id") Long postId) {
        String url = COMMON_PATH + postId;
        return new RedirectView(url);
    }

    @GetMapping("/posts/{posts_id}/comments")
    public RedirectView getPostComments(@PathVariable("posts_id") Long postId) {
        String url = COMMON_PATH + postId + "/comments";
        return new RedirectView(url);
    }
}
