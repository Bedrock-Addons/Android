package com.mcres.octarus.connection.response;


import com.mcres.octarus.model.News;
import com.mcres.octarus.model.Topic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResponseHome implements Serializable {

    public String status = "";
    public List<News> featured = new ArrayList<>();
    public List<Topic> topic = new ArrayList<>();

}
