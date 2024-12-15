package com.wvillage.wvillageJdbc.controller;

import com.wvillage.wvillageJdbc.dao.AuthDAO;
import com.wvillage.wvillageJdbc.vo.MemberVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
        log.info("회원가입 요청 데이터: {}", memberVo); // 요청 데이터 로그

        boolean isSignupSuccess = authDao.signup(memberVo);

        if (isSignupSuccess) {
            log.info("회원가입 성공: {}", memberVo.getEmail());
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } else {
            log.error("회원가입 실패: {}", memberVo.getEmail());
            return ResponseEntity.status(400).body("회원가입 중 오류가 발생했습니다.");
        }
    }


    @GetMapping("/find-email")
    public ResponseEntity<String> findEmail(
            @RequestParam String name,
            @RequestParam String phone
    ) {
        // 전화번호에서 하이픈 제거
        String sanitizedPhone = phone.replaceAll("-", ""); // "-" 제거
        log.info("이메일 찾기 요청: 이름={}, 전화번호(처리됨)={}", name, sanitizedPhone);

        String email = authDao.findEmailByNameAndPhone(name, sanitizedPhone);

        if (email != null) {
            log.info("이메일 찾기 성공: {}", email);
            return ResponseEntity.ok(email);
        } else {
            log.warn("이메일 찾기 실패: 이름={}, 전화번호(처리됨)={}", name, sanitizedPhone);
            return ResponseEntity.status(404).body("해당 정보를 가진 이메일을 찾을 수 없습니다.");
        }
    }

    @PostMapping("/password-reset-request")
    public ResponseEntity<Map<String, Object>> requestPasswordReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String phone = request.get("phone");
        log.info("비밀번호 재설정 인증 요청: email={}, phone={}", email, phone);

        boolean isUserValid = authDao.verifyUser(email, phone);

        Map<String, Object> response = new HashMap<>();
        if (isUserValid) {
            response.put("isValid", true);
            response.put("message", "사용자 인증 성공. 비밀번호 재설정 페이지로 이동하세요.");
            return ResponseEntity.ok(response);
        } else {
            response.put("isValid", false);
            response.put("message", "사용자 정보를 확인할 수 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    @PostMapping("/password-reset")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");

        log.info("비밀번호 재설정 요청: email={}", email);

        if (!newPassword.equals(confirmPassword)) {
            log.warn("비밀번호 불일치: 새 비밀번호와 확인 비밀번호가 다릅니다.");
            return ResponseEntity.badRequest().body("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        boolean isReset = authDao.updatePasswordWithoutEncryption(email, newPassword);
        if (isReset) {
            log.info("비밀번호 재설정 성공: email={}", email);
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } else {
            log.error("비밀번호 재설정 실패: email={}", email);
            return ResponseEntity.status(500).body("비밀번호 변경 중 오류가 발생했습니다.");
        }
    }


    @PostMapping("/edit-profile")
    public ResponseEntity<String> editProfile(@RequestBody MemberVO memberVo) {
        log.info("회원정보 수정 요청: {}", memberVo.getEmail());

        boolean isUpdated = authDao.updateMemberInfo(memberVo);

        if (isUpdated) {
            log.info("회원정보 수정 성공: {}", memberVo.getEmail());
            return ResponseEntity.ok("회원정보가 성공적으로 수정되었습니다.");
        } else {
            log.error("회원정보 수정 실패: {}", memberVo.getEmail());
            return ResponseEntity.status(500).body("회원정보 수정 중 오류가 발생했습니다.");
        }
    }
}
