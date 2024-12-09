package com.wvillage.wvillageJdbc.controller;

import com.wvillage.wvillageJdbc.dao.PostListDAO;
import com.wvillage.wvillageJdbc.vo.PostVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/board")
public class PostListController {
    private final PostListDAO postListDAO;

    @GetMapping("/commonAllList/{region}")
    public ResponseEntity<List<PostVO>> commonAllList(@PathVariable String region) {
        List<PostVO> lst = postListDAO.getCommonAllPostList(region);
        return ResponseEntity.ok(lst);
    }

    @GetMapping("/commonCategoryList")
    public ResponseEntity<List<PostVO>> commonCategoryList(@RequestParam String region,
                                                           @RequestParam String category) {
        List<PostVO> lst = postListDAO.getCommonCategoryPostList(region, category);
        return ResponseEntity.ok(lst);
    }


}

