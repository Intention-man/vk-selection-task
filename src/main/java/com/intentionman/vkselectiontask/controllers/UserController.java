package com.intentionman.vkselectiontask.controllers;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@CrossOrigin
public class UserController {
    private static final String COMMON_PATH = "https://jsonplaceholder.typicode.com/users/";

    @GetMapping("/users")
    public RedirectView getAllUsers() {
        return new RedirectView(COMMON_PATH);
    }

    @GetMapping("/users/{user_id}")
    public RedirectView getUserById(@PathVariable("user_id") Long userId) {
        String url = COMMON_PATH + userId;
        return new RedirectView(url);
    }

    @GetMapping("/users/{user_id}/albums")
    public RedirectView getUserAlbums(@PathVariable("user_id") Long userId) {
        String url = COMMON_PATH + userId + "/albums";
        return new RedirectView(url);
    }

    @GetMapping("/users/{user_id}/posts")
    public RedirectView getUserPosts(@PathVariable("user_id") Long userId) {
        String url = COMMON_PATH + userId + "/posts";
        return new RedirectView(url);
    }
}