/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelChatSupportDAO;

import model.ChatSupport.ChatMessage;
import ui.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rdhan
 */
public class ChatMessageDAO {
    
    public List<ChatMessage> getMessagesBySessionId(int sessionId) {
        List<ChatMessage> messages = new ArrayList<>();
        String query = "SELECT * FROM chat_messages WHERE session_id = ? ORDER BY message_id ASC";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, sessionId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int senderId = rs.getInt("sender_id");
                    String messageText = rs.getString("message_text");
                    String messageType = rs.getString("message_type");
                    messages.add(new ChatMessage(sessionId, senderId, messageText, messageType));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    
}
