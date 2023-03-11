package com.zwh.cart.to;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class UserInfoTo {
    private Long userId;
    private String userKey;
    private boolean tempUser = false;
}
