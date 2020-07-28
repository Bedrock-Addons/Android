package com.mcres.octarus.connection.response;

import com.mcres.octarus.model.News;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResponseNewsDetails implements Serializable {

    public String status = "";
    public News news = new News();
    public List<String> topics = new ArrayList<>();
    public List<String> gallery = new ArrayList<>();

}
