package com.ohiji.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class APIResponse {
    private boolean success;
    private String message;
}
