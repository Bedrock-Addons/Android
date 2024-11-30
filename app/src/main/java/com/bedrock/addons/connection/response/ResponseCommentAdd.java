package com.bedrock.addons.connection.response;

import com.bedrock.addons.model.Comment;

import java.io.Serializable;

public class ResponseCommentAdd implements Serializable {
    public String code = "";
    public Comment comment = new Comment();
}
