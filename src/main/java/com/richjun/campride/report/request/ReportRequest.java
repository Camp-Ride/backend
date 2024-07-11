package com.richjun.campride.report.request;


import com.richjun.campride.like.domain.type.ContentType;
import lombok.Getter;

@Getter
public class ReportRequest {

    private Long itemId;
    private String content;
    private ContentType communityType;

}
