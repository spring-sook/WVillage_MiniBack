package com.wvillage.wvillageJdbc.controller;

import com.wvillage.wvillageJdbc.dao.AuthDAO;
import com.wvillage.wvillageJdbc.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthDAO authDao;

    @PostMapping("/login")
    public ResponseEntity<MemberVO> login(@RequestBody MemberVO memberVo) {
        MemberVO member = authDao.login(memberVo.getEmail(), memberVo.getPassword());
        if (member != null) {
            return ResponseEntity.ok(member);
        } else {
            return ResponseEntity.status(401).body(null); // 401 : 인증 자격 없음
        }
    }
}
