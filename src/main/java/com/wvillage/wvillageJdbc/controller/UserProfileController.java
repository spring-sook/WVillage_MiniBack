package com.wvillage.wvillageJdbc.controller;

import com.wvillage.wvillageJdbc.dao.ReviewRecordDAO;
import com.wvillage.wvillageJdbc.dao.UserProfileDAO;
import com.wvillage.wvillageJdbc.vo.ReviewRecordVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/userProfile")
public class UserProfileController {
    private final UserProfileDAO userProfileDAO;
    private final ReviewRecordDAO reviewRecordDAO;

    @GetMapping("/reviews/{email}")
    public ResponseEntity<List<ReviewRecordVO>> getReviewRecord(@PathVariable String email) {
        List<ReviewRecordVO> reviewRecordVOList = reviewRecordDAO.getReviewRecord(email);
        return ResponseEntity.ok(reviewRecordVOList);
    }


}
