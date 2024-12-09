package com.wvillage.wvillageJdbc.controller;

import com.wvillage.wvillageJdbc.dao.PostDAO;
import com.wvillage.wvillageJdbc.dao.RegionDAO;
import com.wvillage.wvillageJdbc.vo.PostVO;
import com.wvillage.wvillageJdbc.vo.RegionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostDAO postDao;
    private final RegionDAO regionDao;

    @PostMapping("/postWrite")
    public ResponseEntity<Boolean> postWrite(@RequestBody PostRequest postRequest) {
        String postId = postDao.postWrite(postRequest.getPostVo(), postRequest.getImgUrls());
        if (postId != null) {
            return ResponseEntity.ok(true); // 게시글 ID를 반환
        } else {
            return ResponseEntity.status(500).body(false);
        }
    }

    @GetMapping("/getRegion/{areaCode}")
    public ResponseEntity<List<RegionVO>> getRegion(@PathVariable String areaCode) {
        List<RegionVO> list = regionDao.getRegion(areaCode);
        return ResponseEntity.ok(list);
    }

    public static class PostRequest {
        private PostVO postVo;
        private List<String> imgUrls;

        // Getter, Setter, 생성자
        public PostVO getPostVo() {
            return postVo;
        }

        public void setPostVo(PostVO postVo) {
            this.postVo = postVo;
        }

        public List<String> getImgUrls() {
            return imgUrls;
        }

        public void setImgUrls(List<String> imgUrls) {
            this.imgUrls = imgUrls;
        }
    }

}

