package com.mymodule.app;

/**
 * Created by jassayah on 12/8/14.
 * The data model of a Item
 */
public class Item {
    private String attrib;
    private String desc;
    private String href;
    private String src;
    private User user;

    public String getAttrib() {
        return attrib;
    }

    public String getDesc() {
        return desc;
    }

    public String getHref() {
        return href;
    }

    public String getSrc() {
        return src;
    }

    public User getUser() {
        return user;
    }
}
