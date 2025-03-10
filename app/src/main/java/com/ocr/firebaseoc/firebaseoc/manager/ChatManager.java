package com.ocr.firebaseoc.firebaseoc.manager;

import com.google.firebase.firestore.Query;
import com.ocr.firebaseoc.firebaseoc.repository.ChatRepository;

public class ChatManager {

    private static volatile ChatManager instance;

    private final ChatRepository chatRepository;

    private ChatManager() {
        chatRepository = ChatRepository.getInstance();
    }

    public static ChatManager getInstance() {
        ChatManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized (ChatManager.class) {
            if (instance == null) {
                instance = new ChatManager();
            }
            return instance;
        }
    }

    public Query getAllMessageForChat(String chat) {
        return chatRepository.getAllMessageForChat(chat);
    }

}