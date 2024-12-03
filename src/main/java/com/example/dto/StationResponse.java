package com.example.dto;

import com.example.dto.StationItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class StationResponse {
    private Response response;
    
    @Getter
    @NoArgsConstructor
    public static class Response {
        private Body body;
        private Header header;
    }
    
    @Getter
    @NoArgsConstructor
    public static class Body {
        private List<StationItem> items;
        private int pageNo;
        private int numOfRows;
        private int totalCount;
    }
    
    @Getter
    @NoArgsConstructor
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }
} 