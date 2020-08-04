package com.mcres.octarus.data;

public class AppConfig {

    // if you not use ads you can set this to false
    public static final boolean ENABLE_GDPR = true;

    // force rtl layout direction
    public static final boolean RTL_LAYOUT = false;

    // notification topic for FCM
    public static final String NOTIFICATION_TOPIC = "ALL-DEVICE";

    // disable ads during development
    private static final boolean ADS_ENABLE = false;

    // flag for open link in app browser
    public static final boolean OPEN_IN_APP_BROWSER = false;

    // flag for display ads (change true & false ant the end only )

    public static final boolean ADS_MAIN_ALL = ADS_ENABLE && true;
    public static final boolean ADS_MAIN_BANNER = ADS_ENABLE && ADS_MAIN_ALL && true;
    public static final boolean ADS_MAIN_INTERS = ADS_ENABLE && ADS_MAIN_ALL && true;
    public static final int ADS_INTERS_MAIN_INTERVAL = 5; // in second

    public static final boolean ADS_DETAILS_ALL = ADS_ENABLE && true;
    public static final boolean ADS_DETAILS_BANNER = ADS_ENABLE && ADS_DETAILS_ALL && true;
    public static final boolean ADS_DETAILS_INTERS = ADS_ENABLE && ADS_DETAILS_ALL && true;
    public static final int ADS_INTERS_DETAILS_FIRST_INTERVAL = 8; // in second
    public static final int ADS_INTERS_DETAILS_NEXT_INTERVAL = 10; // in second

    public static final boolean ADS_NOTIFICATION_PAGE = ADS_ENABLE && true;
    public static final boolean ADS_SEARCH_PAGE = ADS_ENABLE && true;

    // text size configuration
    public static final int TEXT_SIZE_INCREMENT = 1;
    public static final int TEXT_SIZE_MIN = 10;
    public static final int TEXT_SIZE_MAX = 20;

}
