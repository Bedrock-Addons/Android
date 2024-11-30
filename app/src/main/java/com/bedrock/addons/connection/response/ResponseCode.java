package com.bedrock.addons.connection.response;

import com.bedrock.addons.model.User;

import java.io.Serializable;

public class ResponseCode implements Serializable {
    public String code = "";
    public User user = new User();
}
