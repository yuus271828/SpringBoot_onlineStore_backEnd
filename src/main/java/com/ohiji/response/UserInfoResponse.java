package com.ohiji.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoResponse {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
}
