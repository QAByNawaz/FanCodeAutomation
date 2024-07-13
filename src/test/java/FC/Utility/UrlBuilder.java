package FC.Utility;

import FC.Utility.fancodeutility.BaseUrlManager;

public class UrlBuilder {
    // Base URL from BaseUrlManager
    private static final String BASE_URL = BaseUrlManager.JSON_PLACEHOLDER_BASE_URL;

    // Method to construct URL with a dynamic path
    public static String buildUrl(String path) {
        return BASE_URL + path;
    }

    // Builds URL for todos endpoint
    public static String buildTodosUrl() {
        return buildUrl("/todos");
    }

    // Builds URL for users endpoint
    public static String buildUserUrl() {
        return buildUrl("/users");
    }

    // Builds URL for posts endpoint
    public static String buildPostUrl() {
        return buildUrl("/posts");
    }

    // Builds URL for comments endpoint
    public static String buildCommentUrl() {
        return buildUrl("/comments");
    }

    // Builds URL for albums endpoint
    public static String buildAlbumUrl() {
        return buildUrl("/albums");
    }

    // Builds URL for photos endpoint
    public static String buildPhotoUrl() {
        return buildUrl("/photos");
    }
}
