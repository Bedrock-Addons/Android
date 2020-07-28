package com.mcres.octarus.connection.response;

import com.mcres.octarus.model.Comment;

import java.io.Serializable;

public class ResponseCommentAdd implements Serializable {
    public String code = "";
    public Comment comment = new Comment();
}
