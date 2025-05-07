package org.example.listener;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import org.example.entity.Chat;
import org.example.entity.UserChat;

public class UserChatListener {
    @PostPersist
    void postPersist(UserChat userChat) {
        Chat chat = userChat.getChat();
        chat.setCount(chat.getCount() + 1);
    }

    @PostRemove
    void postRemove(UserChat userChat) {
        Chat chat = userChat.getChat();
        if (chat.getCount() > 0)
            chat.setCount(chat.getCount() - 1);
    }
}
