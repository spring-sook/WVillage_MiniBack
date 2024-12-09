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

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<ReviewVO>> inPostList(@PathVariable String postId) {
        List<ReviewVO> lst = reviewDAO.getPostReviewList(postId);
        return ResponseEntity.ok(lst);
    }

    @GetMapping("/otherProfile/{email}")
    public ResponseEntity<List<ReviewVO>> getReviewRecord(@PathVariable String email) {
        List<ReviewVO> reviewVOList = reviewDAO.getReviewRecord(email);
        return ResponseEntity.ok(reviewVOList);
    }

    @PostMapping("/write")
    public ResponseEntity<String> insertReview(@RequestParam String email,
                                               @RequestParam String reserve,
                                               @RequestParam String tags) {
        boolean result = reviewDAO.insertReview(email, reserve, tags);

        if (result) {
            return ResponseEntity.ok("Review inserted successfully.");
        } else {
            return ResponseEntity.ok("Failed to insert review.");
        }
    }
}
