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

    // 일반 전체
    @GetMapping("/commonAllList/{region}")
    public ResponseEntity<List<PostVO>> commonAllList(@PathVariable String region) {
        List<PostVO> lst = postListDAO.getCommonAllPostList(region);
        return ResponseEntity.ok(lst);
    }

    // 일반 카테고리별
    @GetMapping("/commonCategoryList")
    public ResponseEntity<List<PostVO>> commonCategoryList(@RequestParam String region,
                                                           @RequestParam String category) {
        List<PostVO> lst = postListDAO.getCommonCategoryPostList(region, category);
        return ResponseEntity.ok(lst);
    }

    // 특정 사용자 게시물 목록
    @GetMapping("/userProfile/{email}")
    public ResponseEntity<List<PostVO>> userProfileList(@PathVariable String email) {
        List<PostVO> lst = postListDAO.getUserProfilePostList(email);
        return ResponseEntity.ok(lst);
    }


}

