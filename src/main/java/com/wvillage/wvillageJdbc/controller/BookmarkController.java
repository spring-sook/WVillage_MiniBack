package com.wvillage.wvillageJdbc.controller;

import com.wvillage.wvillageJdbc.dao.BookmarkDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    public boolean insertBookmark(@RequestParam String postId,
                                  @RequestParam String email) {
        return bookmarkDAO.insertBookmark(postId, email);
    }

    @PostMapping("/deleteBookmark")
    public boolean deleteBookmark(@RequestParam String postId,
                                  @RequestParam String email) {
        return bookmarkDAO.deleteBookmark(postId, email);
    }
}
