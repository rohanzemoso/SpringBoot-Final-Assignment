package com.assignment.user.service;

import com.assignment.user.config.FeignClientConfig;
import com.assignment.user.model.Post;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;

@FeignClient(name = "post-service", url = "http://localhost:8081", configuration = FeignClientConfig.class)
public interface PostServiceClient {

    @GetMapping("/post/user/{userId}")
    List<Post> getPostsByUserId(@PathVariable("userId") Integer userId);
}
