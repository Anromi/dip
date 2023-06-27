package com.example.sweater.util;

import com.example.sweater.domain.User;

public abstract class MessHelp {
    public static String getAuthorName(User author) {
        return author != null ? author.getUsername() : "<none>";
    }
}
