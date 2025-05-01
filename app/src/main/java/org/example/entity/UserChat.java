package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "users_chat")
@SuperBuilder
public class UserChat extends AuditableEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Chat chat;

    public void setUser(User user) {
        this.user = user;
        this.user.getUserChats().add(this);
    }

    public void setChat(Chat chat) {
        this.chat = chat;
        this.chat.getUserChats().add(this);
    }
}
