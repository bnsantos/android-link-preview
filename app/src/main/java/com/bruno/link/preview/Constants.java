package com.bruno.link.preview;

/**
 * Created by bruno on 05/05/15.
 */
public class Constants {
    public static final String TITLE = "title";
    public static final String TYPE = "type";
    public static final String DESCRIPTION = "description";
    public static final String IMAGE = "image";
    public static final String AUTHOR = "author";

    public static class OpenGraphProtocol{
        public static final String OG = "og:";
        public static final String OG_TITLE = OG + TITLE;
        public static final String OG_TYPE = OG + TYPE;
        public static final String OG_DESCRIPTION = OG + DESCRIPTION;
        public static final String OG_IMAGE = OG + IMAGE;
    }
}
