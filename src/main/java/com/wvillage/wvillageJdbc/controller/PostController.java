package com.wvillage.wvillageJdbc.controller;

import com.wvillage.wvillageJdbc.dao.PostDAO;
import com.wvillage.wvillageJdbc.vo.PostVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostDAO postDao;
    @PostMapping("/postWrite")
    public ResponseEntity<Boolean> postWrite(@RequestBody PostVO postVo) {
        boolean isSuccess = postDao.postWrite(postVo);
        return ResponseEntity.ok(isSuccess);
    }
}
