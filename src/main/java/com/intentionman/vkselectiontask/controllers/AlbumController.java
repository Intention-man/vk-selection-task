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
public class AlbumController {
    private static final String COMMON_PATH = "https://jsonplaceholder.typicode.com/albums/";

    @GetMapping("/albums")
    public RedirectView getAllAlbums() {
        return new RedirectView(COMMON_PATH);
    }

    @GetMapping("/albums/{id}")
    public RedirectView getAlbumById(@PathVariable("id") Long albumId) {
        String url = COMMON_PATH + albumId;
        return new RedirectView(url);
    }

    @GetMapping("/albums/{album_id}/photos")
    public RedirectView getAlbumPhotosByUserId(@PathVariable("album_id") Long albumId) {
        String url = COMMON_PATH + albumId + "/photos";
        return new RedirectView(url);
    }
}