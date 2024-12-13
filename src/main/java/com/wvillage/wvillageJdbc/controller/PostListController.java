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

    // 메인화면 상위 8개 게시물
    @GetMapping("/mainTopEight")
    public ResponseEntity<List<PostVO>> mainTopEight() {
        List<PostVO> lst = postListDAO.getTopEightPostList();
        return ResponseEntity.ok(lst);
    }

    // 검색기능 통합
    @GetMapping("/postList")
    public ResponseEntity<List<PostVO>> getPostList(@RequestParam String region,
                                                    @RequestParam(required = false) String category,
                                                    @RequestParam(required = false) String keyword ) {
        List<PostVO> lst = postListDAO.getPostList(region, category, keyword);
        return ResponseEntity.ok(lst);
    }

    // 특정 사용자 게시물 목록
    @GetMapping("/userProfile/{email}")
    public ResponseEntity<List<PostVO>> userProfileList(@PathVariable String email) {
        List<PostVO> lst = postListDAO.getUserProfilePostList(email);
        return ResponseEntity.ok(lst);
    }


}

