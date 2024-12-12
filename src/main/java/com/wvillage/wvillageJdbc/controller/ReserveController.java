package com.wvillage.wvillageJdbc.controller;

import com.wvillage.wvillageJdbc.dao.ReserveDAO;
import com.wvillage.wvillageJdbc.vo.CommonVo;
import com.wvillage.wvillageJdbc.vo.ReserveVO;
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
@RequestMapping("/reserve")
public class ReserveController {
    private final ReserveDAO reserveDAO;

    // 게시글의 예약정보 리스트 반환
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<ReserveVO>> getPostReserveList(@PathVariable String postId) {
        List<ReserveVO> list = reserveDAO.getPostReserveList(postId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/myReserveList/{email}")
    public ResponseEntity<List<CommonVo>> getMyReserveList(@PathVariable String email) {
        List<CommonVo> list = reserveDAO.getMyReserveList(email);
        return ResponseEntity.ok(list);
    }
}
