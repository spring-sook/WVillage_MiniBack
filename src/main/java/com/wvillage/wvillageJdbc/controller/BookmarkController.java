package com.wvillage.wvillageJdbc.controller;

import com.wvillage.wvillageJdbc.dao.BookmarkDAO;
import com.wvillage.wvillageJdbc.vo.PostVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/bookmark")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkDAO bookmarkDAO;

    @GetMapping("/isBookmarking")
    public boolean isBookmarking(@RequestParam String email,
                                 @RequestParam String postId) {
        return 1 == bookmarkDAO.isBookmarking(email, postId);
    }

    @PostMapping("/insertBookmark")
    public boolean insertBookmark(@RequestBody Map<String, String> requestData) {
        String postId = requestData.get("postId");
        String email = requestData.get("email");
        return bookmarkDAO.insertBookmark(postId, email);
    }

    @PostMapping("/deleteBookmark")
    public boolean deleteBookmark(@RequestBody Map<String, String> requestData) {
        String postId = requestData.get("postId");
        String email = requestData.get("email");
        return bookmarkDAO.deleteBookmark(postId, email);
    }

    @GetMapping("/bookmarkedList")
    public ResponseEntity<List<PostVO>> bookmarkedPostList(@RequestBody String email) {
        log.info("Received email: {}", email);
        List<PostVO> list = bookmarkDAO.getBookmarkedPostList(email);
        return ResponseEntity.ok(list);
    }
}
