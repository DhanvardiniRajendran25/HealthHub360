/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelChatSupportDAO;

import model.ChatSupport.PredefinedQuestions;
import ui.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rdhan
 */
public class PredefinedQuestionDAO {
    
    private Connection connection;

    public PredefinedQuestionDAO() {
        try {
            connection = DatabaseUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<PredefinedQuestions> getPredefinedQuestions() throws SQLException {
        List<PredefinedQuestions> questionsList = new ArrayList<>();
        String query = "SELECT question_text FROM predefined_questions"; // Adjust table/column name as needed

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            // Iterate through the result set and add the questions to the list
            while (rs.next()) {
                String questionText = rs.getString("question_text");
                PredefinedQuestions question = new PredefinedQuestions(questionText);
                questionsList.add(question);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLException("Error fetching predefined questions from the database.");
        }

        return questionsList;  // Return the list of questions
    }
}
