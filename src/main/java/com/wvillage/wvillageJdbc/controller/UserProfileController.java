package com.wvillage.wvillageJdbc.controller;

import com.wvillage.wvillageJdbc.dao.ReviewDAO;
import com.wvillage.wvillageJdbc.dao.UserProfileDAO;
import com.wvillage.wvillageJdbc.vo.MemberVO;
import com.wvillage.wvillageJdbc.vo.ReserveVO;
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
@RequestMapping("/userProfile")
public class UserProfileController {
    private final UserProfileDAO userProfileDAO;

    // 유저 프로필 정보
    @GetMapping("/profile/{email}")
    public ResponseEntity<MemberVO> getUserProfile(@PathVariable String email) {
        MemberVO memberVO = userProfileDAO.getUserProfile(email);
        return ResponseEntity.ok(memberVO);
    }


    @GetMapping("/addr/{email}")
    public ResponseEntity<MemberVO> getAddr(@PathVariable String email) {
        MemberVO memberVo = userProfileDAO.getAddr(email);
        return ResponseEntity.ok(memberVo);
    }

    // 게시글 작성자 정보
    @GetMapping("/post/{postId}")
    public ResponseEntity<MemberVO> getPostUserProfile(@PathVariable String postId) {
        MemberVO memberVO = userProfileDAO.getPostedUserProfile(postId);
        return ResponseEntity.ok(memberVO);
    }

    // 새로운 알림 개수
    @GetMapping("/reserveMsg")
    public ResponseEntity<ReserveVO> getReserveMsg(@RequestParam String email) {
        ReserveVO reserveVO = userProfileDAO.getNewMsg(email);
        return ResponseEntity.ok(reserveVO);
    }
}
