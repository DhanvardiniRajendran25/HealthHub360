/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelChatController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import modelChatSupportDAO.ChatSessionDAO;
import modelChatSupportDAO.ChatMessageDAO;
import modelChatSupportDAO.PredefinedQuestionDAO;
import model.ChatSupport.ChatSession;
import model.ChatSupport.ChatMessage;
import model.ChatSupport.PredefinedQuestions;
import ui.ChatUI.ChatUI;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import ui.DatabaseUtil;

/**
 *
 * @author rdhan
 */
public class ChatController {
    
    
    
    public List<ChatMessage> getMessagesBySessionId(int sessionId) {
        List<ChatMessage> messages = new ArrayList<>();
        String query = "SELECT * FROM chat_messages WHERE session_id = ? ORDER BY message_id ASC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, sessionId);  // Set the sessionId parameter
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int senderId = rs.getInt("sender_id");
                    String messageText = rs.getString("message_text");
                    String messageType = rs.getString("message_type");

                    // Create a new ChatMessage object with sessionId
                    messages.add(new ChatMessage(sessionId, senderId, messageText, messageType));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    
}
