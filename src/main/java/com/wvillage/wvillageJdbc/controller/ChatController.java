package com.wvillage.wvillageJdbc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.wvillage.wvillageJdbc.config.Common.CORS_ORIGIN;

@Slf4j
@RestController
@CrossOrigin(origins = CORS_ORIGIN)
@RequiredArgsConstructor
@RequestMapping("/userProfile")
public class ChatController {
}
