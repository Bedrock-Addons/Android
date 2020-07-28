package com.mcres.octarus.data;

public class Constant {

    // The server that you want to connect to
    public static String SERVER = "http://addons.octarus.com/";

    // Leave this blank to make a general connection
    public static final String SECURITY_KEY = "";

    // The URL to use for the help page
    public static String HELP_PAGE = "http://storage.octarus.com/files/How%20to%20download%20addons%20Jul%2018%2C%202020%207.25.31%20PM.mp4";

    // The URL to use for the news page
    public static String NEWS_PAGE = "http://addons.octarus.com/uploads/pages/news";

    // Limit requests to decrease server load
    public static int NEWS_PER_REQUEST = 20;
    public static int TOPIC_PER_REQUEST = 20;
    public static int COMMENT_PER_REQUEST = 50;
    public static int NOTIFICATION_PAGE = 20;
    public static int SAVED_PAGE = 20;

    // Retry failed notification after time
    public static int LOAD_IMAGE_NOTIF_RETRY = 3;

    // Where images are stored on the server
    public static String getURLimgNews(String file_name) {
        return SERVER + "uploads/news/" + file_name;
    }

    // Where category data is stored on the server
    public static String getURLimgTopic(String file_name) {
        return SERVER + "uploads/topic/" + file_name;
    }

    // Where user data is stored on the server
    public static String getURLimgUser(String file_name) {
        return SERVER + "uploads/user/" + file_name;
    }

}
