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
        log.info("로그인 시도: {}", memberVo.getEmail());
        MemberVO member = authDao.login(memberVo.getEmail(), memberVo.getPassword());

        if (member != null) {
            log.info("로그인 성공: {}", member.getEmail());
            return ResponseEntity.ok(member);
        } else {
            log.warn("로그인 실패: {}", memberVo.getEmail());
            return ResponseEntity.status(401).body(null); // 401 : 인증 자격 없음
        }
    }


    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody MemberVO memberVo) {
        log.info("회원가입 시도: {}", memberVo.getEmail());

        boolean isSignupSuccess = authDao.signup(memberVo);

        if (isSignupSuccess) {
            log.info("회원가입 성공: {}", memberVo.getEmail());
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } else {
            log.error("회원가입 실패: {}", memberVo.getEmail());
            return ResponseEntity.status(400).body("회원가입 중 오류가 발생했습니다.");
        }
    }
}