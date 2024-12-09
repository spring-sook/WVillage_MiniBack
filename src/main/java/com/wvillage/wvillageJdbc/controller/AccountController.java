package com.wvillage.wvillageJdbc.controller;


import com.wvillage.wvillageJdbc.dao.AccountDAO;
import com.wvillage.wvillageJdbc.vo.AccountVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
    private final AccountDAO accountDao;

//    계좌 조회
    @GetMapping("/findByEmail")
    public ResponseEntity<List<AccountVO>> findAccountsByEmail(@RequestParam String email) {
        List<AccountVO> accounts = accountDao.findAccountsByEmail(email);
        if (accounts != null && !accounts.isEmpty()) {
            return ResponseEntity.ok(accounts);
        } else {
            log.warn("No accounts found for email: {}", email);
            return ResponseEntity.status(404).body(null);
        }
    }

// 계좌 등록
@PostMapping("/add")
public ResponseEntity<Boolean> addAccount(@RequestBody AccountVO accountVO) {
    log.info("Received account data: {}", accountVO);
    boolean isSuccess = accountDao.addAccount(accountVO);
    return ResponseEntity.ok(isSuccess);
}

    //    계좌 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<Boolean> deleteAccount(
            @RequestParam int accountNo,
            @RequestParam String accountBank) {
        boolean isSuccess = accountDao.deleteAccount(accountNo, accountBank);
        if (isSuccess) {
            log.info("Account deleted successfully: accountNo={}, accountBank={}", accountNo, accountBank);
        } else {
            log.error("Failed to delete account: accountNo={}, accountBank={}", accountNo, accountBank);
        }
        return ResponseEntity.ok(isSuccess);
    }
}
