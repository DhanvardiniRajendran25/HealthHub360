/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelChatSupportDAO;

import model.ChatSupport.ChatSession;
import ui.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rdhan
 */
public class ChatSessionDAO {
    
    public static int startChatSession(int patientId, int agentId) throws SQLException {
        String query = "INSERT INTO chat_sessions (patient_id, agent_id) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, patientId);
            stmt.setInt(2, agentId);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);  // Return the generated session_id
            }
        }
        return -1;
    }

    // Fetch all messages for a session
    public static List<ChatSession> getAllChatSessions() throws SQLException {
        List<ChatSession> sessions = new ArrayList<>();
        String query = "SELECT * FROM chat_sessions";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                ChatSession session = new ChatSession();
                session.setSessionId(rs.getInt("session_id"));
                session.setPatientId(rs.getInt("patient_id"));
                session.setAgentId(rs.getInt("agent_id"));
                session.setStatus(rs.getString("status"));
                session.setCreatedAt(rs.getTimestamp("created_at"));
                session.setClosedAt(rs.getTimestamp("closed_at"));
                sessions.add(session);
            }
        }
        return sessions;
    }

    // Close a chat session
    public static void closeChatSession(int sessionId) throws SQLException {
        String query = "UPDATE chat_sessions SET status = 'Closed', closed_at = GETDATE() WHERE session_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, sessionId);
            stmt.executeUpdate();
        }
    }
    
}
