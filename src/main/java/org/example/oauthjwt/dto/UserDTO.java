package org.example.oauthjwt.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDTO {

    private String role;
    private String name;
    private String username;
}
