package com.richjun.campride.report.service;

import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_COMMENT_ID;
import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_CONTENT_TYPE;
import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_POST_ID;
import static com.richjun.campride.global.exception.ExceptionCode.NOT_FOUND_USER_ID;

import com.richjun.campride.comment.domain.Comment;
import com.richjun.campride.comment.domain.repository.CommentRepository;
import com.richjun.campride.global.auth.domain.CustomOAuth2User;
import com.richjun.campride.global.exception.BadRequestException;
import com.richjun.campride.post.domain.Post;
import com.richjun.campride.post.domain.repository.PostRepository;
import com.richjun.campride.report.domain.Report;
import com.richjun.campride.report.domain.repository.ReportRepository;
import com.richjun.campride.report.request.ReportRequest;
import com.richjun.campride.user.domain.User;
import com.richjun.campride.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public Long create(ReportRequest reportRequest, CustomOAuth2User oAuth2User) {

        User user = userRepository.findBySocialLoginId(oAuth2User.getName())
                .orElseThrow(() -> new BadRequestException(NOT_FOUND_USER_ID));

        Report report = createReport(reportRequest, user);
        reportRepository.save(report);

        return report.getId();
    }


    private Report createReport(ReportRequest reportRequest, User user) {
        switch (reportRequest.getCommunityType()) {
            case POST:
                Post post = postRepository.findById(reportRequest.getItemId())
                        .orElseThrow(() -> new BadRequestException(NOT_FOUND_POST_ID));
                return Report.of(user, reportRequest, post);
            case COMMENT:
                Comment comment = commentRepository.findById(reportRequest.getItemId())
                        .orElseThrow(() -> new BadRequestException(NOT_FOUND_COMMENT_ID));
                return Report.of(user, reportRequest, comment);
            default:
                throw new BadRequestException(NOT_FOUND_CONTENT_TYPE);
        }
    }


}
