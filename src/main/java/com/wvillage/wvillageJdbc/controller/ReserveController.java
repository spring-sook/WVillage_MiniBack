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

    // 내가 예약한 예약목록
    @GetMapping("/myReserveList/{email}")
    public ResponseEntity<List<CommonVo>> getMyReserveList(@PathVariable String email) {
        List<CommonVo> list = reserveDAO.getMyReserveList(email);
        return ResponseEntity.ok(list);
    }

    // 내 게시물의 예약 요청
    @GetMapping("/myReserveManagement/{email}")
    public ResponseEntity<List<CommonVo>> getMyReserveManagement(@PathVariable String email) {
        List<CommonVo> list = reserveDAO.getReserveListManagement(email);
        return ResponseEntity.ok(list);
    }

    // 예약 ID = reserveId;
    // 게시글 ID : reservePost;
    // 예약자 : reserveEmail;
    // 예약 시작 시간 : reserveStart;
    // 예약 종료 시간 : reserveEnd;
    // 총 지불 금액 : reserveTotalPrice;
    // 예약 상태 : reserveState;
    // 새 메시지 여부 : reserveNewMsg;
    // 예약 취소/거절 이유 : reserveReason;
    // 예약하기
    @PostMapping("/reservation")
    public ResponseEntity<ReserveVO> insertReserve(@RequestBody ReserveVO reserveVO) {
        log.warn(reserveVO.toString());
        boolean isSuccess = reserveDAO.insertReserve(reserveVO);
        if (isSuccess) {
            return ResponseEntity.ok(reserveVO);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    // 예약 승인
    @PostMapping("/reserveAccept")
    public ResponseEntity<ReserveVO> reserveAccept(@RequestBody ReserveVO reserveVO) {
        boolean isSuccess = reserveDAO.reserveAccept(reserveVO);
        if (isSuccess) {
            return ResponseEntity.ok(reserveVO);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    // 예약 취소
    @PostMapping("/reserveCancel")
    public ResponseEntity<ReserveVO> reserveCancel(@RequestBody ReserveVO reserveVO) {
        boolean isSuccess = reserveDAO.reserveCancel(reserveVO);
        if (isSuccess) {
            return ResponseEntity.ok(reserveVO);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    // 예약 거절, 완료
    @PostMapping("/reserveUpdate")
    public ResponseEntity<ReserveVO> reserveDeny(@RequestBody ReserveVO reserveVO) {
        boolean isSuccess = reserveDAO.reserveUpdate(reserveVO);
        if (isSuccess) {
            return ResponseEntity.ok(reserveVO);
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
