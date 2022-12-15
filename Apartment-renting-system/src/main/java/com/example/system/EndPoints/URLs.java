package com.example.system.EndPoints;

public final class URLs {
    private URLs() {}
    // base urls
    public static final String ADMIN_BASE_URL = "/admin";
    public static final String AUTHENTICATION_BASE_URL = "/auth";
    public static final String USER_BASE_URL = "/user";
    public static final String MEDIA_BASE_URL = "/media";

    // non base urls
    // media
    public static final String PHOTOS = "/photos";

    // authentication
    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";
    public static final String FORGOT_PASSWORD = "/forgot-password";
    public static final String RESET_PASSWORD = "/reset-password";

    // admin and user

    public static final String USERS = "/users"; //
    public static final String ADS = "/ads"; //
    public static final String HOUSE_TYPES = "/house-types";
    public static final String PROMOTIONS = "/promotions";

    // user
    public static final String PROFILE = "/profile";
    public static final String PROMOTION = "/promotion";
    public static final String SAVED_LIST = "/saved-list";

}
