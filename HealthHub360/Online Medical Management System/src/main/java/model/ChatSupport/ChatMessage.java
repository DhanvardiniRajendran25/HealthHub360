/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.ChatSupport;

import java.sql.Timestamp;

/**
 *
 * @author rdhan
 */
public class ChatMessage {

    private int sessionId;
    private int senderId;
    private String messageText;
    private String messageType;

    // Constructor including sessionId
    public ChatMessage(int sessionId, int senderId, String messageText, String messageType) {
        this.sessionId = sessionId;
        this.senderId = senderId;
        this.messageText = messageText;
        this.messageType = messageType;
    }

    // Getters
    public int getSessionId() {
        return sessionId;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageType() {
        return messageType;
    }
    
}
