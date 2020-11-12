package com.mcres.octarus.data;

public class Constant {

    // The server that you want to connect to
    public static String SERVER = "http://167.71.157.159/";

    // Leave this blank to make a general connection
    public static final String KEY = "";

    // The URL to use for the help page
    public static String HELP = "https://tenwan.octarus.dev/bedrock-addons/How%20To%20Download%20Addons.mp4";


    public static String TEXT_HELP = "http://167.71.157.159/uploads/pages/install-mcfile/";

    // The URL to use for the news page
    public static String NEWS = "https://tenwan.octarus.dev/bedrock-addons/News.html";

    // The URL to use for the github page
    public static String GITHUB = "http://addons.octarus.com/uploads/pages/news";

    // Limit requests to decrease server load
    public static int CONTENT_PER_REQUEST = 20;  //Default 20
    public static int CATEGORY_PER_REQUEST = 20;  //Default 20
    public static int COMMENT_PER_REQUEST = 25; //Default 25
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