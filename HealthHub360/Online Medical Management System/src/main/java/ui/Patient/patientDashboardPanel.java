/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.Patient;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import ui.DatabaseUtil;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import modelDAO.UserService;
import ui.LoginScreen;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.net.URL;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import modelPharmacyEnterpriseDAO.PrescriptionDAO;
import modelPharmacyEnterpriseService.PrescriptionService;
import ui.ChatUI.ChatUI;

/**
 *
 * @author rdhan
 */
public class patientDashboardPanel extends javax.swing.JPanel {

    private int patientId;
    private Connection connection;
    private boolean isUpdateMode = false; // Tracks if the user is in update mode
    private int fetchedPatientId;

    /**
     * Creates new form patientDashboardPanel
     */
    public patientDashboardPanel(int patientId, Connection connection) throws SQLException {
        this.patientId = patientId;
        this.connection = connection;
        System.out.println("Patient ID in Dashboard: " + patientId);
        initComponents();

        if (!isConnectionValid()) {
            JOptionPane.showMessageDialog(this, "Database connection is not available.");
            return;
        }

        populatePatientDetails(patientId);
        populateAppointmentTable();
        populateMedicalHistoryTable();
        populateDoctorComboBox();
        populateDateComboBox();
        populateTimeSlotComboBox(LocalDate.now().toString());

        //Claims
        if (!isConnectionValid()) {
            JOptionPane.showMessageDialog(this, "Database connection is not available.");
            return;
        }
        this.fetchedPatientId = fetchPatientId(patientId); // Initialize fetchedPatientId
        if (fetchedPatientId == -1) {
            JOptionPane.showMessageDialog(this, "Unable to load dashboard: Invalid patient ID.");
            return;
        }
        populatePolicyNumberAndCoverageAmount();
        populateClaimsTable(fetchedPatientId);
        populateStatusDropdown();
        populateLabReportsTable(fetchedPatientId);
        populatePrescription("All");
    }

    private boolean isConnectionValid() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("Reconnecting to the database...");
                connection = DatabaseUtil.getConnection(); // Attempt to reconnect
            }
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int fetchPatientId(int userId) {
        String sql = """
            SELECT p.id AS patient_id 
            FROM patients p 
            JOIN users u ON p.person_id = u.person_id 
            WHERE u.id = ?
        """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("patient_id");
            } else {
                JOptionPane.showMessageDialog(this, "No patient found for the given user ID.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching patient ID: " + e.getMessage());
        }
        return -1; // Return -1 if no valid patient_id is found
    }

    private void populatePolicyNumberAndCoverageAmount() {
        String sql = "SELECT ip.id, ip.coverage_amount FROM insurance_policies ip "
                + "JOIN patients p ON p.policy_id = ip.id WHERE p.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                TxtPolicyNumber.setText(String.valueOf(rs.getInt("id")));
                TxtCoverageAmount.setText(String.valueOf(rs.getDouble("coverage_amount")));
            } else {
                TxtPolicyNumber.setText("No Policy Assigned");
                TxtCoverageAmount.setText("");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching policy and coverage details: " + e.getMessage());
        }
    }

    private void filterClaimsByStatus(int fetchedPatientId, String status) {
        String sql = """
        SELECT TOP 10 ic.id, ip.name AS policy_name, ic.claim_amount, ic.status, ic.claim_date
        FROM insurance_claims ic
        JOIN insurance_policies ip ON ic.policy_id = ip.id
        WHERE ic.patient_id = ?
    """;

        if (!"All".equalsIgnoreCase(status)) {
            sql += " AND ic.status = ?";
        }
        sql += " ORDER BY ic.claim_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, fetchedPatientId);
            if (!"All".equalsIgnoreCase(status)) {
                stmt.setString(2, status);
            }

            ResultSet rs = stmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) TblInsuranceClaims.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                int id = rs.getInt("id");
                String policyName = rs.getString("policy_name");
                double claimAmount = rs.getDouble("claim_amount");
                String claimStatus = rs.getString("status");
                String claimDate = rs.getString("claim_date");
                model.addRow(new Object[]{id, policyName, claimAmount, claimStatus, claimDate});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error filtering insurance claims: " + e.getMessage());
        }
    }

    private void populateClaimsTable(int fetchedPatientId) {
        String sql = """
        SELECT TOP 10 ic.id, ip.name AS policy_name, ic.claim_amount, ic.status, ic.claim_date
        FROM insurance_claims ic
        JOIN insurance_policies ip ON ic.policy_id = ip.id
        WHERE ic.patient_id = ?
        ORDER BY ic.claim_date DESC
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, fetchedPatientId);
            ResultSet rs = stmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) TblInsuranceClaims.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("policy_name"),
                    rs.getDouble("claim_amount"),
                    rs.getString("status"),
                    rs.getString("claim_date")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching insurance claims: " + e.getMessage());
        }
    }

    private void populateStatusDropdown() {
        CmbFilStatus.removeAllItems();
        CmbFilStatus.addItem("All"); // Default option
        CmbFilStatus.addItem("Pending");
        CmbFilStatus.addItem("Approved");
        CmbFilStatus.addItem("Rejected");
    }

    private void populateLabReportsTable(int fetchedPatientId) {
        String sql = """
        SELECT lr.id AS report_id, 
               lt.name AS test_name, 
               lr.result AS test_result, 
               lr.generated_date AS report_date
        FROM lab_reports lr
        JOIN test_requests tr ON lr.test_request_id = tr.id
        JOIN lab_tests lt ON tr.lab_test_id = lt.id
        JOIN users u ON lr.lab_technician_id = u.id
        JOIN persons p ON u.person_id = p.id
        WHERE tr.patient_id = ?;
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, fetchedPatientId);
            System.out.println("Executing query with fetchedPatientId: " + fetchedPatientId);

            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = (DefaultTableModel) labRequestsTable.getModel();
            model.setRowCount(0); // Clear previous rows

            boolean dataFound = false;
            while (rs.next()) {
                dataFound = true;
                Object[] row = {
                    rs.getInt("report_id"),
                    rs.getString("test_name"),
                    rs.getString("test_result"),
                    rs.getDate("report_date")
                };
                model.addRow(row);
            }

            if (!dataFound) {
                System.out.println("No lab reports found for patient ID: " + fetchedPatientId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching lab reports: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        patientTabbedPane = new javax.swing.JTabbedPane();
        patientPanel = new javax.swing.JPanel();
        patientProfilePanel = new javax.swing.JPanel();
        btnLogOut = new javax.swing.JButton();
        lblName = new javax.swing.JLabel();
        txtName = new javax.swing.JTextField();
        txtDOB = new javax.swing.JTextField();
        txtContact = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        txtAddress = new javax.swing.JTextField();
        txtInsNo = new javax.swing.JTextField();
        lblDOB = new javax.swing.JLabel();
        lblContact = new javax.swing.JLabel();
        lblEmail = new javax.swing.JLabel();
        lblAddress = new javax.swing.JLabel();
        lblInsNo = new javax.swing.JLabel();
        btnUpdate = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnOpenChat = new javax.swing.JButton();
        patientAppointmentPanel = new javax.swing.JPanel();
        appointmentsPanel = new javax.swing.JPanel();
        appointmentsScrollPane = new javax.swing.JScrollPane();
        appointmentTable = new javax.swing.JTable();
        docAvalLbl = new javax.swing.JLabel();
        cntrlPanel2 = new javax.swing.JPanel();
        timeSlotLbl2 = new javax.swing.JLabel();
        timeSlotComboBox2 = new javax.swing.JComboBox<>();
        bookAppointmentBtn2 = new javax.swing.JButton();
        dateComboBox = new javax.swing.JComboBox<>();
        dateLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        doctorComboBox = new javax.swing.JComboBox<>();
        medicalHistoryPanel = new javax.swing.JPanel();
        medicalHistoryWorkArea = new javax.swing.JPanel();
        medicalHistoryScrollPane = new javax.swing.JScrollPane();
        medicalHistoryTable = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        labRequestsScrollPane = new javax.swing.JScrollPane();
        labRequestsTable = new javax.swing.JTable();
        bills = new javax.swing.JTabbedPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        contorlPanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        workArea = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        TxtPolicyNumber = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        TxtClaimAmount = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        TxtCoverageAmount = new javax.swing.JTextField();
        BtnSubmit = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TblInsuranceClaims = new javax.swing.JTable();
        CmbFilStatus = new javax.swing.JComboBox<>();
        BtnView = new javax.swing.JButton();
        presceiptions = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblPrescriptions = new javax.swing.JTable();
        cmbFilterPharmacistBasedPrescription = new javax.swing.JComboBox<>();

        btnLogOut.setText("Log Out!");
        btnLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogOutActionPerformed(evt);
            }
        });

        lblName.setText("Name");

        txtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtEmailActionPerformed(evt);
            }
        });

        txtInsNo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtInsNoActionPerformed(evt);
            }
        });

        lblDOB.setText("Date Of Birth");

        lblContact.setText("Contact Number");

        lblEmail.setText("Email");

        lblAddress.setText("Address");

        lblInsNo.setText("Insurance Number");

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnOpenChat.setText("Go to Chat Support!");
        btnOpenChat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOpenChatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout patientProfilePanelLayout = new javax.swing.GroupLayout(patientProfilePanel);
        patientProfilePanel.setLayout(patientProfilePanelLayout);
        patientProfilePanelLayout.setHorizontalGroup(
            patientProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(patientProfilePanelLayout.createSequentialGroup()
                .addGap(306, 306, 306)
                .addComponent(btnUpdate)
                .addGap(98, 98, 98)
                .addComponent(btnSave)
                .addContainerGap(452, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, patientProfilePanelLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(btnLogOut)
                .addGap(74, 74, 74))
            .addGroup(patientProfilePanelLayout.createSequentialGroup()
                .addGap(153, 153, 153)
                .addGroup(patientProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblContact, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDOB, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblName, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblInsNo, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(111, 111, 111)
                .addGroup(patientProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtEmail)
                    .addComponent(txtContact)
                    .addComponent(txtName)
                    .addComponent(txtDOB)
                    .addComponent(txtAddress)
                    .addComponent(txtInsNo, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnOpenChat, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64))
        );
        patientProfilePanelLayout.setVerticalGroup(
            patientProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(patientProfilePanelLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(btnLogOut)
                .addGap(25, 25, 25)
                .addGroup(patientProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblName))
                .addGap(40, 40, 40)
                .addGroup(patientProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDOB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDOB))
                .addGap(42, 42, 42)
                .addGroup(patientProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtContact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblContact))
                .addGroup(patientProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(patientProfilePanelLayout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addGroup(patientProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblEmail)))
                    .addGroup(patientProfilePanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOpenChat)))
                .addGap(39, 39, 39)
                .addGroup(patientProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(patientProfilePanelLayout.createSequentialGroup()
                        .addComponent(txtAddress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addGroup(patientProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtInsNo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblInsNo)))
                    .addComponent(lblAddress))
                .addGap(98, 98, 98)
                .addGroup(patientProfilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUpdate)
                    .addComponent(btnSave))
                .addContainerGap(130, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout patientPanelLayout = new javax.swing.GroupLayout(patientPanel);
        patientPanel.setLayout(patientPanelLayout);
        patientPanelLayout.setHorizontalGroup(
            patientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(patientPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(patientProfilePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        patientPanelLayout.setVerticalGroup(
            patientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(patientPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(patientProfilePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        patientTabbedPane.addTab("Profile", patientPanel);

        appointmentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Doctor Name", "Specialization", "Time Slots", "Availability"
            }
        ));
        appointmentsScrollPane.setViewportView(appointmentTable);

        docAvalLbl.setText("Available Doctors and Time Slots");

        timeSlotLbl2.setText("Select Time Slot:");

        timeSlotComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        timeSlotComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeSlotComboBox2ActionPerformed(evt);
            }
        });

        bookAppointmentBtn2.setText("Book Appointment");
        bookAppointmentBtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bookAppointmentBtn2ActionPerformed(evt);
            }
        });

        dateComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        dateComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateComboBoxActionPerformed(evt);
            }
        });

        dateLabel.setText("Select Date:");

        jLabel1.setText("Select Doctor:");

        doctorComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout cntrlPanel2Layout = new javax.swing.GroupLayout(cntrlPanel2);
        cntrlPanel2.setLayout(cntrlPanel2Layout);
        cntrlPanel2Layout.setHorizontalGroup(
            cntrlPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cntrlPanel2Layout.createSequentialGroup()
                .addGroup(cntrlPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(cntrlPanel2Layout.createSequentialGroup()
                        .addGap(78, 78, 78)
                        .addComponent(bookAppointmentBtn2))
                    .addGroup(cntrlPanel2Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(cntrlPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, cntrlPanel2Layout.createSequentialGroup()
                                .addGap(82, 82, 82)
                                .addGroup(cntrlPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(timeSlotComboBox2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dateComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(doctorComboBox, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(cntrlPanel2Layout.createSequentialGroup()
                                .addGroup(cntrlPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(timeSlotLbl2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dateLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))
                                .addGap(84, 84, 84)))))
                .addContainerGap(64, Short.MAX_VALUE))
        );

        cntrlPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {dateComboBox, doctorComboBox, timeSlotComboBox2});

        cntrlPanel2Layout.setVerticalGroup(
            cntrlPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cntrlPanel2Layout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addComponent(jLabel1)
                .addGap(14, 14, 14)
                .addComponent(doctorComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(dateLabel)
                .addGap(18, 18, 18)
                .addComponent(dateComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(45, 45, 45)
                .addComponent(timeSlotLbl2)
                .addGap(18, 18, 18)
                .addComponent(timeSlotComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(86, 86, 86)
                .addComponent(bookAppointmentBtn2)
                .addContainerGap(242, Short.MAX_VALUE))
        );

        cntrlPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {dateComboBox, doctorComboBox, timeSlotComboBox2});

        javax.swing.GroupLayout appointmentsPanelLayout = new javax.swing.GroupLayout(appointmentsPanel);
        appointmentsPanel.setLayout(appointmentsPanelLayout);
        appointmentsPanelLayout.setHorizontalGroup(
            appointmentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appointmentsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(appointmentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(appointmentsPanelLayout.createSequentialGroup()
                        .addComponent(docAvalLbl)
                        .addGap(522, 522, 522))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, appointmentsPanelLayout.createSequentialGroup()
                        .addComponent(appointmentsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 662, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addComponent(cntrlPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        appointmentsPanelLayout.setVerticalGroup(
            appointmentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, appointmentsPanelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(docAvalLbl)
                .addGap(18, 18, 18)
                .addComponent(appointmentsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 584, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 14, Short.MAX_VALUE))
            .addGroup(appointmentsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cntrlPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout patientAppointmentPanelLayout = new javax.swing.GroupLayout(patientAppointmentPanel);
        patientAppointmentPanel.setLayout(patientAppointmentPanelLayout);
        patientAppointmentPanelLayout.setHorizontalGroup(
            patientAppointmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(patientAppointmentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(appointmentsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        patientAppointmentPanelLayout.setVerticalGroup(
            patientAppointmentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(patientAppointmentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(appointmentsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        patientTabbedPane.addTab("Appointments", patientAppointmentPanel);

        medicalHistoryWorkArea.setLayout(new java.awt.BorderLayout());

        medicalHistoryTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Date", "Doctor Name", "Diagnosis", "Treatment", "Prescription"
            }
        ));
        medicalHistoryScrollPane.setViewportView(medicalHistoryTable);

        medicalHistoryWorkArea.add(medicalHistoryScrollPane, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout medicalHistoryPanelLayout = new javax.swing.GroupLayout(medicalHistoryPanel);
        medicalHistoryPanel.setLayout(medicalHistoryPanelLayout);
        medicalHistoryPanelLayout.setHorizontalGroup(
            medicalHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(medicalHistoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(medicalHistoryWorkArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        medicalHistoryPanelLayout.setVerticalGroup(
            medicalHistoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(medicalHistoryPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(medicalHistoryWorkArea, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        patientTabbedPane.addTab("Medical History", medicalHistoryPanel);

        labRequestsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Report ID", "Test Name", "Test Result", "Report Date"
            }
        ));
        labRequestsScrollPane.setViewportView(labRequestsTable);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(labRequestsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 847, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(123, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(labRequestsScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(358, Short.MAX_VALUE))
        );

        patientTabbedPane.addTab("Lab Requests", jPanel4);

        bills.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jButton1.setText("Medicine Bills");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Test Report Bills");

        javax.swing.GroupLayout contorlPanelLayout = new javax.swing.GroupLayout(contorlPanel);
        contorlPanel.setLayout(contorlPanelLayout);
        contorlPanelLayout.setHorizontalGroup(
            contorlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contorlPanelLayout.createSequentialGroup()
                .addGap(270, 270, 270)
                .addComponent(jButton1)
                .addGap(174, 174, 174)
                .addComponent(jButton2)
                .addContainerGap(348, Short.MAX_VALUE))
        );
        contorlPanelLayout.setVerticalGroup(
            contorlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contorlPanelLayout.createSequentialGroup()
                .addContainerGap(48, Short.MAX_VALUE)
                .addGroup(contorlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addGap(29, 29, 29))
        );

        jSplitPane1.setTopComponent(contorlPanel);

        workArea.setLayout(new java.awt.CardLayout());
        jSplitPane1.setRightComponent(workArea);

        bills.addTab("", jSplitPane1);

        patientTabbedPane.addTab("Bills", bills);

        jLabel2.setText("Policy Name");

        TxtPolicyNumber.setEnabled(false);

        jLabel3.setText("Claim Amount");

        jLabel4.setText("Coverage Amount");

        TxtCoverageAmount.setEnabled(false);
        TxtCoverageAmount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtCoverageAmountActionPerformed(evt);
            }
        });

        BtnSubmit.setText("Submit");
        BtnSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSubmitActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(396, 396, 396)
                        .addComponent(BtnSubmit))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(249, 249, 249)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(28, 28, 28)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(TxtCoverageAmount)
                            .addComponent(TxtClaimAmount)
                            .addComponent(TxtPolicyNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(394, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(109, 109, 109)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(TxtPolicyNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(TxtClaimAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(TxtCoverageAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(60, 60, 60)
                .addComponent(BtnSubmit)
                .addContainerGap(378, Short.MAX_VALUE))
        );

        patientTabbedPane.addTab("Create Claims", jPanel1);

        TblInsuranceClaims.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Claim ID", "Policy Name", "Claim Amount", "Status", "Claim Date"
            }
        ));
        jScrollPane1.setViewportView(TblInsuranceClaims);

        CmbFilStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        CmbFilStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbFilStatusActionPerformed(evt);
            }
        });

        BtnView.setText("View Details");
        BtnView.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnViewActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1019, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CmbFilStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(BtnView)
                                .addGap(24, 24, 24)))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(90, 90, 90)
                .addComponent(CmbFilStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BtnView)
                .addContainerGap(248, Short.MAX_VALUE))
        );

        patientTabbedPane.addTab("View Claims", jPanel2);

        tblPrescriptions.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Prescription ID", "Patient Name", "Doctor Name", "Assigned Date", "Status", "Payment Status"
            }
        ));
        jScrollPane3.setViewportView(tblPrescriptions);

        cmbFilterPharmacistBasedPrescription.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Assigned to pharmacist", "Completed" }));
        cmbFilterPharmacistBasedPrescription.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbFilterPharmacistBasedPrescriptionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout presceiptionsLayout = new javax.swing.GroupLayout(presceiptions);
        presceiptions.setLayout(presceiptionsLayout);
        presceiptionsLayout.setHorizontalGroup(
            presceiptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(presceiptionsLayout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(presceiptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cmbFilterPharmacistBasedPrescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 889, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(96, Short.MAX_VALUE))
        );
        presceiptionsLayout.setVerticalGroup(
            presceiptionsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(presceiptionsLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(cmbFilterPharmacistBasedPrescription, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 583, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        patientTabbedPane.addTab("Prescriptions", presceiptions);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(patientTabbedPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(patientTabbedPane)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void populatePatientDetails(int patientId) {
        String query = """
            SELECT p.name, p.contact_number, p.email, p.address,
                           pt.date_of_birth, pt.policy_id
                    FROM persons p
                    JOIN patients pt ON p.id = pt.person_id
                    WHERE pt.id = ?;
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                txtName.setText(rs.getString("name"));
                txtContact.setText(rs.getString("contact_number"));
                txtEmail.setText(rs.getString("email"));
                txtAddress.setText(rs.getString("address"));
                txtDOB.setText(rs.getString("date_of_birth"));
                txtInsNo.setText(rs.getString("policy_id"));
            } else {
                JOptionPane.showMessageDialog(this, "No patient details found for the provided ID.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching patient details: " + e.getMessage());
        }
    }

    private void setFieldsEditable(boolean editable) {
        txtName.setEditable(editable);
        txtContact.setEditable(editable);
        txtEmail.setEditable(editable);
        txtAddress.setEditable(editable);
        txtDOB.setEditable(editable);
        txtInsNo.setEditable(editable);
    }

    /*private void populateDateComboBox() {
    // Populate the date combo box with the next 7 days
    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
    LocalDate today = LocalDate.now();
    for (int i = 0; i < 7; i++) {
        model.addElement(today.plusDays(i).toString());
    }
    dateComboBox.setModel(model);
}
     */
    private void populateDateComboBox() {
        // Populate the date combo box with the next 7 days
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            model.addElement(today.plusDays(i).toString());
        }
        dateComboBox.setModel(model);

        // Add action listener to update time slots when a date is selected
        dateComboBox.addActionListener(evt -> {
            String selectedDate = dateComboBox.getSelectedItem().toString();
            populateTimeSlotComboBox(selectedDate);
        });
    }

    /*
private void populateTimeSlotComboBox(String selectedDate) {
    String query = """
        SELECT time_slot
        FROM schedules
        WHERE doctor_id = ? AND status = 'Available' AND date = ?
    """;

    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
       // pstmt.setInt(1, selectedDoctorId); // Replace with actual selected doctor ID
        pstmt.setString(2, selectedDate);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            model.addElement(rs.getString("time_slot"));
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error fetching available time slots: " + e.getMessage());
    }
    timeSlotComboBox2.setModel(model);
}*/
    private void populateTimeSlotComboBox(String selectedDate) {
        String query = """
        SELECT time_slot 
        FROM schedules 
        WHERE doctor_id = ? AND date = ? AND status = 'Available';
    """;

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            int doctorId = getSelectedDoctorId();
            pstmt.setInt(1, doctorId);
            pstmt.setString(2, selectedDate);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                model.addElement(rs.getString("time_slot"));
            }
            timeSlotComboBox2.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching available time slots: " + e.getMessage());
        }
    }

    private void populateDoctorComboBox() {
        String query = "SELECT d.id, p.name FROM doctors d JOIN persons p ON d.person_id = p.id";

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        try (PreparedStatement pstmt = connection.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int doctorId = rs.getInt("id");
                String doctorName = rs.getString("name");
                model.addElement(doctorId + " - " + doctorName);
            }
            doctorComboBox.setModel(model);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching doctor list: " + e.getMessage());
        }
    }

    /*}

    
    private void populateDoctorComboBox() {
    String query = "SELECT d.id, p.name FROM doctors d JOIN persons p ON d.person_id = p.id";

    DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
    try (PreparedStatement pstmt = connection.prepareStatement(query);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            int doctorId = rs.getInt("id");
            String doctorName = rs.getString("name");
            model.addElement(doctorId + " - " + doctorName);
        }
        doctorComboBox.setModel(model);
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error fetching doctor list: " + e.getMessage());
    }
}*/
    private int getSelectedDoctorId() {
        if (doctorComboBox.getSelectedItem() != null) {
            String selectedDoctor = doctorComboBox.getSelectedItem().toString();
            return Integer.parseInt(selectedDoctor.split(" - ")[0]); // Extract ID
        }
        JOptionPane.showMessageDialog(this, "Please select a doctor.");
        throw new IllegalStateException("No doctor selected.");
    }

    private void populateAppointmentTable() {
        String query = """
            SELECT a.id, p.name AS doctor_name, a.time_slot, a.status, a.date
            FROM appointments a
            JOIN doctors d ON a.doctor_id = d.id
            JOIN persons p ON d.person_id = p.id
            WHERE a.patient_id = ?;
        """;
        DefaultTableModel model = (DefaultTableModel) appointmentTable.getModel();
        model.setRowCount(0);
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("doctor_name"),
                    rs.getString("time_slot"),
                    rs.getString("status"),
                    rs.getDate("date")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching appointments: " + e.getMessage());
        }
    }

    private void populateMedicalHistoryTable() {
        String query = """
            SELECT m.created_date, p.name AS doctor_name, m.diagnosis, m.treatment, m.notes 
            FROM medical_records m
            JOIN appointments a ON m.appointment_id = a.id
            JOIN doctors d ON a.doctor_id = d.id
            JOIN persons p ON d.person_id = p.id
            JOIN prescriptions pr ON m.patient_id = pr.patient_id AND d.id = pr.doctor_id
            WHERE m.patient_id = ?;
        """;

        DefaultTableModel model = (DefaultTableModel) medicalHistoryTable.getModel();
        model.setRowCount(0); // Clear existing rows

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getDate("created_date"),
                    rs.getString("doctor_name"),
                    rs.getString("diagnosis"),
                    rs.getString("treatment"),
                    rs.getString("notes")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching medical history: " + e.getMessage());
        }
    }

    private void btnLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogOutActionPerformed
        // TODO add your handling code here:

        try {
            DatabaseUtil.closeConnection();
            JOptionPane.showMessageDialog(this, "Logged out successfully!");

            JPanel mainPanel = (JPanel) this.getParent();
            CardLayout layout = (CardLayout) mainPanel.getLayout();
            mainPanel.add(new LoginScreen(mainPanel), "LoginScreen");
            layout.show(mainPanel, "LoginScreen");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while logging out: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnLogOutActionPerformed

    private void txtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEmailActionPerformed

    private void txtInsNoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtInsNoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtInsNoActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        isUpdateMode = true; // Enable update mode
        setFieldsEditable(true); // Make fields editable
        JOptionPane.showMessageDialog(this, "You can now edit your details.");
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        if (!isUpdateMode) {
            JOptionPane.showMessageDialog(this, "Click 'Update' before saving changes.");
            return;
        }

        if (!isConnectionValid()) {
            JOptionPane.showMessageDialog(this, "Database connection is not available. Please try again later.");
            return;
        }

        String updatePersonsQuery = """
        UPDATE persons
        SET name = ?, contact_number = ?, email = ?, address = ?
        WHERE id = (SELECT person_id FROM patients WHERE id = ?);
    """;

        String updatePatientsQuery = """
        UPDATE patients
        SET date_of_birth = ?, medical_history = ?, insurance_number = ?
        WHERE id = ?;
    """;

        try {
            // Update 'persons' table
            try (PreparedStatement pstmtPersons = connection.prepareStatement(updatePersonsQuery)) {
                pstmtPersons.setString(1, txtName.getText().trim());
                pstmtPersons.setString(2, txtContact.getText().trim());
                pstmtPersons.setString(3, txtEmail.getText().trim());
                pstmtPersons.setString(4, txtAddress.getText().trim());
                pstmtPersons.setInt(5, patientId);

                pstmtPersons.executeUpdate();
            }

            // Update 'patients' table
            try (PreparedStatement pstmtPatients = connection.prepareStatement(updatePatientsQuery)) {
                pstmtPatients.setDate(1, Date.valueOf(txtDOB.getText().trim()));
                pstmtPatients.setString(2, txtInsNo.getText().trim());
                pstmtPatients.setString(3, ""); // Add medical history input if needed
                pstmtPatients.setInt(4, patientId);

                pstmtPatients.executeUpdate();
            }

            JOptionPane.showMessageDialog(this, "Details updated successfully.");
            isUpdateMode = false;
            setFieldsEditable(false); // Exit update mode and disable editing
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating patient details: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSaveActionPerformed

    private void timeSlotComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeSlotComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_timeSlotComboBox2ActionPerformed

    private void bookAppointmentBtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bookAppointmentBtn2ActionPerformed
        // TODO add your handling code here:
        if (timeSlotComboBox2.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a time slot.");
            return;
        }
        String[] slotData = timeSlotComboBox2.getSelectedItem().toString().split(" - ");
        int scheduleId = Integer.parseInt(slotData[0]);
        String timeSlot = slotData[1];
        String date = (String) dateComboBox.getSelectedItem();
        int doctorId = Integer.parseInt(doctorComboBox.getSelectedItem().toString().split(" - ")[0]);

        String insertAppointment = """
            INSERT INTO appointments (patient_id, doctor_id, time_slot, status, date)
            SELECT ?, doctor_id, ?, 'Pending', date
            FROM schedules WHERE id = ?;
        """;
        String updateSchedule = "UPDATE schedules SET status = 'Booked' WHERE id = ?";

        try (PreparedStatement appointmentStmt = connection.prepareStatement(insertAppointment); PreparedStatement scheduleStmt = connection.prepareStatement(updateSchedule)) {
            appointmentStmt.setInt(1, patientId);
            appointmentStmt.setString(2, timeSlot);
            appointmentStmt.setInt(3, scheduleId);
            appointmentStmt.executeUpdate();

            scheduleStmt.setInt(1, scheduleId);
            scheduleStmt.executeUpdate();

            populateTimeSlotComboBox(date);
            populateAppointmentTable();
            JOptionPane.showMessageDialog(this, "Appointment booked successfully!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error booking appointment: " + e.getMessage());
        }
    }//GEN-LAST:event_bookAppointmentBtn2ActionPerformed

    private void dateComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateComboBoxActionPerformed
        // TODO add your handling code here:
        String selectedDate = dateComboBox.getSelectedItem().toString();
        populateTimeSlotComboBox(selectedDate);
    }//GEN-LAST:event_dateComboBoxActionPerformed


    private void btnOpenChatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOpenChatActionPerformed
        try {
            // TODO add your handling code here:
            JPanel mainPanel = (JPanel) this.getParent();  // Get the parent container
            CardLayout layout = (CardLayout) mainPanel.getLayout();  // Get the CardLayout manager

            // Create and switch to the Chat Panel
            ChatUI chatPanel = new ChatUI();  // ChatPanel contains the Chat UI functionality
            mainPanel.add(chatPanel, "ChatPanel");  // Add the ChatPanel to the main panel

            layout.show(mainPanel, "ChatPanel");  // Switch to the ChatPanel
        } catch (SQLException ex) {
            Logger.getLogger(patientDashboardPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnOpenChatActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        medicineBilling mb = new medicineBilling(workArea, patientId);
        workArea.add("prescriptionBilling", mb);
        CardLayout layout = (CardLayout) workArea.getLayout();
        layout.next(workArea);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void TxtCoverageAmountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtCoverageAmountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtCoverageAmountActionPerformed

    private void BtnSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSubmitActionPerformed
        if (fetchedPatientId == -1) {
            JOptionPane.showMessageDialog(this, "Unable to submit claim: Invalid patient ID.");
            return;
        }

        String policyNumberText = TxtPolicyNumber.getText().trim();
        String claimAmountText = TxtClaimAmount.getText().trim();
        String coverageAmountText = TxtCoverageAmount.getText().trim();

        if (policyNumberText.equals("No Policy Assigned") || claimAmountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please ensure a policy is assigned and claim amount is entered.");
            return;
        }

        try {
            int policyId = Integer.parseInt(policyNumberText);
            double claimAmount = Double.parseDouble(claimAmountText);
            double coverageAmount = Double.parseDouble(coverageAmountText);

            if (claimAmount > coverageAmount) {
                JOptionPane.showMessageDialog(this, "Claim amount cannot exceed the coverage amount.");
                return;
            }

            String sql = """
            INSERT INTO insurance_claims (patient_id, policy_id, claim_amount, status, assigned_manager_id)
            VALUES (?, ?, ?, 'Pending',
                (SELECT TOP 1 id FROM users WHERE role_id =
                    (SELECT id FROM roles WHERE role_name = 'Insurance Manager')))
            """;

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, fetchedPatientId);
                stmt.setInt(2, policyId);
                stmt.setDouble(3, claimAmount);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Insurance claim submitted successfully!");
                TxtClaimAmount.setText("");
                populateClaimsTable(fetchedPatientId); // Refresh claims table
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid claim amount. Please enter a valid number.");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error submitting insurance claim: " + e.getMessage());
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnSubmitActionPerformed

    private void CmbFilStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmbFilStatusActionPerformed
        if (fetchedPatientId == -1) {
            JOptionPane.showMessageDialog(this, "Unable to fetch claims: Invalid patient ID.");
            return;
        }
        String selectedStatus = (String) CmbFilStatus.getSelectedItem();
        filterClaimsByStatus(fetchedPatientId, selectedStatus);
        // TODO add your handling code here:
    }//GEN-LAST:event_CmbFilStatusActionPerformed

    private void BtnViewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnViewActionPerformed
        int selectedRow = TblInsuranceClaims.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a claim to view details.");
            return;
        }

        int claimId = (int) TblInsuranceClaims.getValueAt(selectedRow, 0);

        String sql = """
        SELECT ic.id, ip.name AS policy_name, ip.coverage_amount, ic.claim_amount, ic.status,
        ic.claim_date, ic.assigned_manager_id, u.username AS manager_name
        FROM insurance_claims ic
        JOIN insurance_policies ip ON ic.policy_id = ip.id
        LEFT JOIN users u ON ic.assigned_manager_id = u.id
        WHERE ic.id = ?
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, claimId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                StringBuilder details = new StringBuilder();
                details.append("Claim ID: ").append(rs.getInt("id")).append("\n");
                details.append("Policy Name: ").append(rs.getString("policy_name")).append("\n");
                details.append("Coverage Amount: $").append(rs.getDouble("coverage_amount")).append("\n");
                details.append("Claim Amount: $").append(rs.getDouble("claim_amount")).append("\n");
                details.append("Status: ").append(rs.getString("status")).append("\n");
                details.append("Claim Date: ").append(rs.getString("claim_date")).append("\n");
                details.append("Assigned Manager: ").append(
                        rs.getString("manager_name") == null ? "Unassigned" : rs.getString("manager_name"));

                JOptionPane.showMessageDialog(this, details.toString(), "Claim Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No details found for the selected claim.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching claim details: " + e.getMessage());
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnViewActionPerformed

    private void cmbFilterPharmacistBasedPrescriptionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbFilterPharmacistBasedPrescriptionActionPerformed
        // TODO add your handling code here:
        String selectedStatus = (String) cmbFilterPharmacistBasedPrescription.getSelectedItem();
        try {
            populatePrescription(selectedStatus);
        } catch (SQLException ex) {
            Logger.getLogger(patientDashboardPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_cmbFilterPharmacistBasedPrescriptionActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnSubmit;
    private javax.swing.JButton BtnView;
    private javax.swing.JComboBox<String> CmbFilStatus;
    private javax.swing.JTable TblInsuranceClaims;
    private javax.swing.JTextField TxtClaimAmount;
    private javax.swing.JTextField TxtCoverageAmount;
    private javax.swing.JTextField TxtPolicyNumber;
    private javax.swing.JTable appointmentTable;
    private javax.swing.JPanel appointmentsPanel;
    private javax.swing.JScrollPane appointmentsScrollPane;
    private javax.swing.JTabbedPane bills;
    private javax.swing.JButton bookAppointmentBtn2;
    private javax.swing.JButton btnLogOut;
    private javax.swing.JButton btnOpenChat;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JComboBox<String> cmbFilterPharmacistBasedPrescription;
    private javax.swing.JPanel cntrlPanel2;
    private javax.swing.JPanel contorlPanel;
    private javax.swing.JComboBox<String> dateComboBox;
    private javax.swing.JLabel dateLabel;
    private javax.swing.JLabel docAvalLbl;
    private javax.swing.JComboBox<String> doctorComboBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JScrollPane labRequestsScrollPane;
    private javax.swing.JTable labRequestsTable;
    private javax.swing.JLabel lblAddress;
    private javax.swing.JLabel lblContact;
    private javax.swing.JLabel lblDOB;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblInsNo;
    private javax.swing.JLabel lblName;
    private javax.swing.JPanel medicalHistoryPanel;
    private javax.swing.JScrollPane medicalHistoryScrollPane;
    private javax.swing.JTable medicalHistoryTable;
    private javax.swing.JPanel medicalHistoryWorkArea;
    private javax.swing.JPanel patientAppointmentPanel;
    private javax.swing.JPanel patientPanel;
    private javax.swing.JPanel patientProfilePanel;
    private javax.swing.JTabbedPane patientTabbedPane;
    private javax.swing.JPanel presceiptions;
    private javax.swing.JTable tblPrescriptions;
    private javax.swing.JComboBox<String> timeSlotComboBox2;
    private javax.swing.JLabel timeSlotLbl2;
    private javax.swing.JTextField txtAddress;
    private javax.swing.JTextField txtContact;
    private javax.swing.JTextField txtDOB;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtInsNo;
    private javax.swing.JTextField txtName;
    private javax.swing.JPanel workArea;
    // End of variables declaration//GEN-END:variables

    private void populatePrescription(String filterStatus) throws SQLException {
        // Fetch prescriptions for the logged-in pharmacist
        PrescriptionService prescriptionService = new PrescriptionService(new PrescriptionDAO());
        List<Object[]> prescriptions = prescriptionService.getPrescriptionsByPaitentId(patientId);
        System.out.println("Fetched prescriptions: " + prescriptions);
        // Get the table model
        DefaultTableModel model = (DefaultTableModel) tblPrescriptions.getModel();
        model.setRowCount(0); // Clear any previous rows
        // Filter prescriptions based on status
        List<Object[]> filteredPrescriptions = new ArrayList<>();
        // If "All" is selected, show all prescriptions; otherwise filter by status
        for (Object[] row : prescriptions) {
            String status = (String) row[4];  // Assuming the status is the 5th element (index 4)

            // If the filterStatus is "All", add all rows; otherwise, filter by status
            if ("All".equals(filterStatus) || filterStatus.equals(status)) {
                filteredPrescriptions.add(row);
            }
        }
        // Add each filtered row of data to the table
        for (Object[] row : filteredPrescriptions) {
            model.addRow(new Object[]{
                row[0], // Prescription ID (Integer)
                row[1], // Patient Name (String)
                row[2], // Doctor Name (String)
                row[4], // Status (String)
                row[5] // Payment Status (String)
            });
        }
    }

}
