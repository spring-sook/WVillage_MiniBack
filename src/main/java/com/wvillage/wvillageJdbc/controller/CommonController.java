package com.wvillage.wvillageJdbc.controller;

import com.wvillage.wvillageJdbc.dao.RegionDAO;
import com.wvillage.wvillageJdbc.vo.RegionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/common")
@RequiredArgsConstructor
public class CommonController {
    private final RegionDAO regionDAO;

    @GetMapping("/sidoChoice")
    public ResponseEntity<List<RegionVO>> sidoChoice() {
        List<RegionVO> list = regionDAO.filterSido();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/regionFilter/{regionCode}")
    public ResponseEntity<List<RegionVO>> regionFilter(@PathVariable String regionCode) {
        List<RegionVO> list = regionDAO.regionFilterList(regionCode);
        return ResponseEntity.ok(list);
    }
}
