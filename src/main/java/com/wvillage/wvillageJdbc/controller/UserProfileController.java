package com.wvillage.wvillageJdbc.controller;

import com.wvillage.wvillageJdbc.dao.ReviewRecordDAO;
import com.wvillage.wvillageJdbc.dao.UserProfileDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@RequestMapping("/userProfile")
public class UserProfileController {
    private final UserProfileDAO userProfileDAO;
    private final ReviewRecordDAO reviewRecordDAO;




}
