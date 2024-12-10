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
    private BookmarkDAO bookmarkDAO;

    @GetMapping("/isBookmarking")
    public boolean isBookmarking(@RequestParam String email,
                                 @RequestParam String postId) {
        return 1 == bookmarkDAO.isBookmarking(email, postId);
    }
}
