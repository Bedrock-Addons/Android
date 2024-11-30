package com.mcres.octarus.data;

public class Constant {

    // The server that you want to connect to
    public static String SERVER = "http://147.219.201.161/";

    // Leave this blank to make a general connection
    public static final String KEY = "";
    // The URL to use for the help page
    public static String HELP = "https://example.com";

    public static String PRIVACY_URL = "https://docs.google.com/document/d/1WrdDLyLxE5DQUb0WyrF1JTlcyUt5W7zGweoaPGnvW6I/edit?usp=sharing";

    public static String STREAMS = "https://example.com";

    public static String COMMUNITY = "https://example.com";

    public static String TEXT_HELP = "https://example.com";

    // The URL to use for the news page
    public static String NEWS = "https://example.com";

    // The URL to use for the github page
    public static String GITHUB = "https://example.com";

    // Limit requests to decrease server load
    public static int CONTENT_PER_REQUEST = 20;  //Default 20
    public static int CATEGORY_PER_REQUEST = 20;  //Default 20
    public static int COMMENT_PER_REQUEST = 20; //Default 20
    public static int NOTIFICATION_PAGE = 30;  //Default 30
    public static int BOOKMARKS_PAGE = 20;  //Default 20
    public static int LOAD_IMAGE_RETRY = 3;  //Default 3

    // Data request locations
    public static String getURLcontent(String file_name) {
        return SERVER + "uploads/news/" + file_name;
    }
    public static String getURLcategory(String file_name) {
        return SERVER + "uploads/topic/" + file_name;
    }
    public static String getURLuser(String file_name) {
        return SERVER + "uploads/user/" + file_name;
    }

}