package com.example.sweater.domain;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/notification")
public class NotificationEndpoint {

    private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // handle error
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        // handle message
    }

    public static void sendNotification(String notification) throws IOException {
        for (Session session : sessions) {
            session.getBasicRemote().sendText(notification);
        }
    }
}
