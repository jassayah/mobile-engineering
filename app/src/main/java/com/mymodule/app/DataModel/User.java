package com.mymodule.app.DataModel;

import com.mymodule.app.DataModel.Avatar;

/**
 * Created by jassayah on 12/8/14.
 * The data model of a User
 */
public class User {
    private String name;
    private Avatar avatar;
    private String username;

    public String getName() {
        return name;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public String getUsername() {
        return username;
    }
}
