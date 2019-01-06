package com.example.sparsh.blogging_app;

// modal class for Json Parsing


import java.util.Date;

public class Blog extends  BlogPostId{

    private String content,img_uri,user_id,time;



    private int likes;



    public Blog()
    {

    }
    public Blog(String content, String img_uri, String user_id,String time,int likes)
    {
        this.content = content;
        this.img_uri = img_uri;
        this.user_id = user_id;
        this.time = time;
        this.likes = likes;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImg_uri() {
        return img_uri;
    }

    public void setImg_uri(String img_uri) {
        this.img_uri = img_uri;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
