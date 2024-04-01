package com.example.project_v2.love;

import com.example.project_v2._core.util.ApiUtil;
import com.example.project_v2.user.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class LoveController {

    private final LoveService loveService;
    private final HttpSession session;

    // 좋아요 입력
    @PostMapping("/api/loves")
    public ResponseEntity<?> save(@RequestBody LoveRequest.SaveDTO reqDTO){
        User sessionUser = (User) session.getAttribute("sessionUser");
        loveService.save(reqDTO, sessionUser);
        return ResponseEntity.ok(new ApiUtil<>(null));
    }

    // 좋아요 삭제
    @DeleteMapping("/api/loves/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id){
        User sessionUser = (User) session.getAttribute("sessionUser");
        loveService.delete(id, sessionUser);
        return ResponseEntity.ok(new ApiUtil<>(null));
    }
}
