package org.jboss.pnc.pvt.rest.endpoints.demo;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.core.JsonProcessingException;

@ServerEndpoint("/pvtStatus")
public class PVTStatusReceiver {

    @OnOpen
    public void onOpen(Session session) {
        StatusReveivorRegistry.INSTANCE.sessions.put(session.getId(), session);
    }

    @OnError
    public void onError(Session session, Throwable error) throws IOException {
        try {
            session.close();
        } finally {
            StatusReveivorRegistry.INSTANCE.sessions.remove(session.getId());
        }
    }

    @OnClose
    public void onClose(Session session) {
        StatusReveivorRegistry.INSTANCE.sessions.remove(session.getId());
    }

    @OnMessage
    public void onPVTStatus(Session session, String msg) throws JsonProcessingException {
        System.out.println("OK, got your message: " + msg);
    }

    public static class StatusReveivorRegistry {

        public static final StatusReveivorRegistry INSTANCE = new StatusReveivorRegistry();
        private Map<String, Session> sessions = new ConcurrentHashMap<>();

        private StatusReveivorRegistry(){}

        public void broadMessage(String msg) throws IOException {
            synchronized (sessions) {
                if (sessions.isEmpty()) {
                    System.err.println("No session to send message to!");
                    return;
                }
                for (Session session: sessions.values()) {
                    if (session.isOpen()) {
                        session.getBasicRemote().sendText(msg);
                        session.getBasicRemote().flushBatch();
                    }
                }
            }
        }
    }

}
