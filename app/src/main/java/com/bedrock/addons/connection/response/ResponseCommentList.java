package com.bedrock.addons.connection.response;

import com.bedrock.addons.model.Comment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResponseCommentList implements Serializable {

    public String status = "";
    public int count = -1;
    public int count_total = -1;
    public int pages = -1;
    public List<Comment> comments = new ArrayList<>();

}
