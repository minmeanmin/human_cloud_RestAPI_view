package com.example.project_v2.user;

import com.example.project_v2._core.errors.exception.Exception401;
import com.example.project_v2._core.errors.exception.Exception404;
import com.example.project_v2.notice.Notice;
import com.example.project_v2.notice.NoticeJPARepository;
import com.example.project_v2.notice.NoticeResponse;
import com.example.project_v2.resume.Resume;
import com.example.project_v2.resume.ResumeJPARepository;
import com.example.project_v2.resume.ResumeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserJPARepository userJPARepository;
    private final ResumeJPARepository resumeJPARepository;
    private final NoticeJPARepository noticeJPARepository;

    public User sameCheck(String username) {
        User user = userJPARepository.findByUsername(username)
                .orElseThrow(() -> new Exception401("사용할 수 있는 아이디입니다"));
        return user;
    }

    @Transactional
    public User join(UserRequest.JoinDTO reqDTO) {
        return userJPARepository.save(reqDTO.toEntity());
    }

    public User login(UserRequest.LoginDTO reqDTO) {
        User sessionUser = userJPARepository.findByUsernameAndPassword(reqDTO.getUsername(), reqDTO.getPassword())
                .orElseThrow(() -> new Exception401("인증되지 않았습니다"));
        return sessionUser;
    }

    @Transactional
    public User update(Integer id, UserRequest.UpdateDTO reqDTO) {
        User user = userJPARepository.findById(id)
                .orElseThrow(() -> new Exception404("회원정보를 찾을 수 없습니다"));

        user.setUsername(reqDTO.getUsername());
        user.setPassword(reqDTO.getPassword());
        user.setTel(reqDTO.getTel());
        user.setEmail(reqDTO.getEmail());
        user.setAddress(reqDTO.getAddress());

        // 이미지 파일 넣기
        MultipartFile imgFile = reqDTO.getImage(); // 이미지 파일 데이터 저장
        String imgFilename = UUID.randomUUID() + "_" + imgFile.getOriginalFilename(); // 이미지 파일 오리지널 이름

        // 파일 저장 위치 설정
        Path imgPath = Paths.get("./src/main/resources/static/images/" + imgFilename);

        try {
            Files.write(imgPath, imgFile.getBytes());
            user.setImage(imgFilename);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    // 사용자의 role 에 따라 메인페이지 화면 변경
    public List<?> getMainPageByUserRole(User sessionUser) {
        List<?> resultList = new ArrayList<>();
        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        if (sessionUser != null) { // 로그인시
            if (sessionUser.getRole() == 1) {
                // Role이 1인 경우 Resume 리스트 반환
                resultList = resumeJPARepository.findAll(sort).stream()
                        .map(resume -> new ResumeResponse.ResumeListDTO((Resume) resume))
                        .toList();
            } else {
                // Role이 0인 경우 Notice 리스트 반환
                resultList = noticeJPARepository.findAll(sort).stream()
                        .map(notice -> new NoticeResponse.NoticeListDTO((Notice) notice))
                        .toList();
            }
        } else { // 로그인하지 않은 경우 Notice 리스트 반환
            resultList = noticeJPARepository.findAll(sort).stream()
                    .map(notice -> new NoticeResponse.NoticeListDTO((Notice) notice))
                    .toList();
        }
        return resultList;
    }
}
