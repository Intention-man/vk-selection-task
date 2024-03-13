package com.intentionman.vkselectiontask.controllers;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@CrossOrigin
@AllArgsConstructor
public class ProxyController {
    private final RestTemplate restTemplate;

    @Cacheable(value = "proxyCache", key = "#url")
    @GetMapping(value = "**")
    public String getProxy(@RequestAttribute String url) {
        return restTemplate.getForObject(url, String.class);
    }

    @CachePut(value = "proxyCache", key = "#url")
    @PostMapping(value = "**")
    public ResponseEntity<String> postProxy(@RequestAttribute String url, @RequestBody Object dto) {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, dto, String.class);
        return ResponseEntity.ok(responseEntity.getBody());
    }

    @CachePut(value = "proxyCache", key = "#url")
    @PutMapping(value = "**")
    public ResponseEntity<String> putProxy(@RequestAttribute String url, @RequestBody Object dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<Object> requestEntity = new HttpEntity<>(dto, headers);
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
        return ResponseEntity.ok(responseEntity.getBody());
    }

    @CacheEvict(value = "proxyCache", key = "#url")
    @DeleteMapping(value = "**")
    public ResponseEntity<HttpStatus> deleteProxy(@RequestAttribute String url) {
        restTemplate.delete(url);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
