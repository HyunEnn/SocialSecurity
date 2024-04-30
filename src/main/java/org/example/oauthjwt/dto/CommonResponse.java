package org.example.oauthjwt.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
@Builder
public class CommonResponse {

    @Builder.Default
    private Date timeStamp = new Date();
    @Builder.Default
    private HttpStatus status = HttpStatus.OK;
    private String message;
    private Object data;

}