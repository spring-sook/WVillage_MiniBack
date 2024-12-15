package com.wvillage.wvillageJdbc.controller;

import com.wvillage.wvillageJdbc.dao.ReviewDAO;
import com.wvillage.wvillageJdbc.vo.ReviewVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/review")
public class ReviewController {
    private final ReviewDAO reviewDAO;

    // 게시글 리뷰
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<ReviewVO>> inPostList(@PathVariable String postId) {
        List<ReviewVO> lst = reviewDAO.getPostReviewList(postId);
        return ResponseEntity.ok(lst);
    }

    // 사용자 리뷰
    @GetMapping("/userProfile/{email}")
    public ResponseEntity<List<ReviewVO>> getReviewRecord(@PathVariable String email) {
        List<ReviewVO> reviewVOList = reviewDAO.getReviewRecord(email);
        return ResponseEntity.ok(reviewVOList);
    }

    // 리뷰 작성
    @PostMapping("/write")
    public ResponseEntity<String> insertReview(@RequestParam String email,
                                               @RequestParam String reserve,
                                               @RequestParam String tags) {
        log.error(email);
        log.error(reserve);
        log.error(tags);
        boolean result = reviewDAO.insertReview(email, reserve, tags);

        if (result) {
            return ResponseEntity.ok("Review inserted successfully.");
        } else {
            return ResponseEntity.ok("Failed to insert review.");
        }
    }

    // 리뷰 태그 전체 반환
    @GetMapping("/reviewList")
    public ResponseEntity<List<ReviewVO>> getReviewList() {
        List<ReviewVO> lst = reviewDAO.getAllReview();
        return ResponseEntity.ok(lst);
    }

    @GetMapping("/isReview")
    public ResponseEntity<Boolean> isReview(@RequestParam String email, @RequestParam String reserve) {
        boolean result = reviewDAO.isReview(email, reserve);
        return ResponseEntity.ok(result);
    }
}
