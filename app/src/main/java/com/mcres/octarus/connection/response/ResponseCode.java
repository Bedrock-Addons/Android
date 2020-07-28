package com.mcres.octarus.connection.response;

import com.mcres.octarus.model.User;

import java.io.Serializable;

public class ResponseCode implements Serializable {
    public String code = "";
    public User user = new User();
}
