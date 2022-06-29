package com.example.everythingeverywherehere;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("UserLoginDetails")
public class UserLoginDetails extends ParseObject {
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USER = "user";
    public static final String KEY_SEARCHES = "searches";

    public String getEmail() {
        return getString(KEY_EMAIL);
    }

    public void setEmail(String email) {
        put(KEY_EMAIL, email);
    }

    public String getPassword() {
        return getString(KEY_PASSWORD);
    }

    public void setPassword(String password) {
        put(KEY_EMAIL, password);
    }

    public List<String> getSearches() {
        return getList(KEY_SEARCHES);
    }

    public void setSearches(List<String> searches) {
        put(KEY_SEARCHES, searches);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);

    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

}
