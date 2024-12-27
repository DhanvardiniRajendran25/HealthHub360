/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.Doctor;

import java.awt.CardLayout;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import ui.DatabaseUtil;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.JTable;
import modelDAO.UserService;
import ui.LoginScreen;

import ui.pharmacist.allPrescriptions;

import ui.pharmacyManager.prescriptionCreation;

/**
 *
 * @author rdhan
 */
public class doctorDashboard extends javax.swing.JPanel {

    private int doctorId; // Doctor's unique ID
    private Connection connection;

    /**
     * Creates new form doctorWorkarea
     */
    public doctorDashboard(int doctorId, Connection connection) {
        initComponents();
        this.doctorId = doctorId;
        this.connection = connection;

        if (!isConnectionValid()) {
            JOptionPane.showMessageDialog(this, "Database connection is not available.");
            return;
        }

//        populateScheduleTable();
        populateAppointmentTable();
        populatePatientHistoryTable(null);
        populateDoctorScheduleTable();

        populatePatientsDropdown();
        populateLabTestsDropdown();
        populateManagersDropdown();
        populateTestRequestsTable(null);
        populateCmbFilDate();

        java.util.Date currentDate = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        TxtDate.setText(sdf.format(currentDate));
        TxtDate.setEditable(false);
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

    private boolean validateForeignKey(int doctorId, int patientId) {
        String doctorQuery = "SELECT COUNT(*) FROM doctors WHERE id = ?";
        String patientQuery = "SELECT COUNT(*) FROM patients WHERE id = ?";
        try (PreparedStatement doctorStmt = connection.prepareStatement(doctorQuery); PreparedStatement patientStmt = connection.prepareStatement(patientQuery)) {

            doctorStmt.setInt(1, doctorId);
            patientStmt.setInt(1, patientId);

            try (ResultSet doctorResult = doctorStmt.executeQuery(); ResultSet patientResult = patientStmt.executeQuery()) {

                // Check if the doctor exists
                if (doctorResult.next() && doctorResult.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(this, "Doctor ID does not exist.");
                    System.out.println("Passed Doctor ID: " + doctorId);
                    return false;
                }

                // Check if the patient exists
                if (patientResult.next() && patientResult.getInt(1) == 0) {
                    JOptionPane.showMessageDialog(this, "Patient ID does not exist.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error validating foreign keys: " + e.getMessage());
            return false;
        }
        return true;
    }

    private void populateDoctorScheduleTable() {
        String sql = """
        SELECT s.id, s.date, s.time_slot, s.status
        FROM schedules s
        WHERE s.doctor_id = ?;
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            System.out.println("Doctor ID used for fetching schedules: " + doctorId);
            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();
            model.setRowCount(0); // Clear existing rows

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                System.out.println("Fetched Row - ID: " + rs.getInt("id")
                        + ", Date: " + rs.getDate("date")
                        + ", Time Slot: " + rs.getString("time_slot")
                        + ", Status: " + rs.getString("status"));

                Object[] row = {
                    rs.getInt("id"),
                    rs.getDate("date"),
                    rs.getString("time_slot"),
                    rs.getString("status")
                };
                model.addRow(row);
            }

            if (!hasData) {
                System.out.println("No schedule data found for doctor ID: " + doctorId);
                JOptionPane.showMessageDialog(this, "No schedule data found.");
            }

            scheduleTable.repaint(); // Force refresh of the table
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching schedule: " + e.getMessage());
        }
    }

    private void populateAppointmentTable() {
        String sql = "SELECT a.id, p.name AS patient_name, a.time_slot, a.status "
                + "FROM appointments a "
                + "JOIN patients pt ON a.patient_id = pt.id "
                + "JOIN persons p ON pt.person_id = p.id";

        try (Connection connection = DatabaseUtil.getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(sql)) {

            DefaultTableModel model = (DefaultTableModel) appointmentTable.getModel();
            model.setRowCount(0); // Clear existing rows

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String patientName = resultSet.getString("patient_name");
                String timeSlot = resultSet.getString("time_slot");
                String status = resultSet.getString("status"); // Correctly fetch status
                model.addRow(new Object[]{id, patientName, timeSlot, status});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error Fetching Appointments: " + e.getMessage());
        }
    }

    private void populatePatientHistoryTable(String searchQuery) {
        String sql = "SELECT ph.id, p.name AS patient_name, ph.date, ph.diagnosis, ph.treatment "
                + "FROM patient_history ph "
                + "JOIN patients pt ON ph.patient_id = pt.id "
                + "JOIN persons p ON pt.person_id = p.id";
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql += " WHERE p.name LIKE ?";
        }

        try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                statement.setString(1, "%" + searchQuery + "%");
            }

            ResultSet resultSet = statement.executeQuery();
            DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
            model.setRowCount(0); // Clear existing rows

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String patientName = resultSet.getString("patient_name");
                Date date = resultSet.getDate("date");
                String diagnosis = resultSet.getString("diagnosis");
                String treatment = resultSet.getString("treatment");
                model.addRow(new Object[]{id, patientName, date, diagnosis, treatment});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error Fetching Patient History: " + e.getMessage());
        }
    }

    //Test Request
    private void populatePatientsDropdown() {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT p.id, ps.name FROM patients p JOIN persons ps ON p.person_id = ps.id")) {
            ResultSet rs = stmt.executeQuery();
            CmbPatient.removeAllItems();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                CmbPatient.addItem(id + " - " + name);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading patients: " + e.getMessage());
        }
    }

    private void populateLabTestsDropdown() {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT id, name FROM lab_tests")) {
            ResultSet rs = stmt.executeQuery();
            CmbLabTest.removeAllItems(); // Clear dropdown
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                CmbLabTest.addItem(id + " - " + name); // Add test ID and name
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading lab tests: " + e.getMessage());
        }
    }

    private void populateManagersDropdown() {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT u.id, ps.name FROM users u JOIN persons ps ON u.person_id = ps.id "
                + "WHERE u.role_id = (SELECT id FROM roles WHERE role_name = 'Lab Manager')")) {
            ResultSet rs = stmt.executeQuery();
            CmbManager.removeAllItems(); // Clear dropdown
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                CmbManager.addItem(id + " - " + name); // Add manager ID and name
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading managers: " + e.getMessage());
        }
    }

    private void populateTestRequestsTable(String filterDate) {
        String sql = "SELECT tr.id, "
                + "ps.name AS patient_name, "
                + "lt.name AS test_name, "
                + "tr.status, "
                + "pm.name AS manager_name, "
                + "lr.result AS lab_result "
                + "FROM test_requests tr "
                + "JOIN patients p ON tr.patient_id = p.id "
                + "JOIN persons ps ON p.person_id = ps.id "
                + "JOIN lab_tests lt ON tr.lab_test_id = lt.id "
                + "JOIN users m ON tr.manager_id = m.id "
                + "JOIN persons pm ON m.person_id = pm.id "
                + "LEFT JOIN lab_reports lr ON tr.id = lr.test_request_id";
        if (filterDate != null) {
            sql += " WHERE CONVERT(DATE, tr.request_date) = ?";
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (filterDate != null) {
                stmt.setString(1, filterDate);
            }
            ResultSet rs = stmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0); // Clear existing rows
            while (rs.next()) {
                int id = rs.getInt("id");
                String patient = rs.getString("patient_name");
                String test = rs.getString("test_name");
                String status = rs.getString("status");
                String manager = rs.getString("manager_name");
                String result = rs.getString("lab_result");
                model.addRow(new Object[]{id, patient, test, status, manager, result});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading test requests: " + e.getMessage());
        }
    }

    private void populateCmbFilDate() {
        CmbFilDate.removeAllItems(); // Clear existing items
        CmbFilDate.addItem("All");
        CmbFilDate.addItem("Today");
        CmbFilDate.addItem("Last 7 Days");
        CmbFilDate.addItem("Last 1 Month");
        CmbFilDate.addItem("Last 6 Months");
        CmbFilDate.addItem("Last 1 Year");
    }

    private java.sql.Date calculateFilterDate(String filter) {
        java.util.Date today = new java.util.Date();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(today);

        switch (filter) {
            case "Today":
                return new java.sql.Date(today.getTime());

            case "Last 7 Days":
                calendar.add(java.util.Calendar.DAY_OF_YEAR, -7);
                break;

            case "Last 1 Month":
                calendar.add(java.util.Calendar.MONTH, -1);
                break;

            case "Last 6 Months":
                calendar.add(java.util.Calendar.MONTH, -6);
                break;

            case "Last 1 Year":
                calendar.add(java.util.Calendar.YEAR, -1);
                break;

            default:
                return null; // No filter applied
        }

        return new java.sql.Date(calendar.getTimeInMillis());
    }

    private int fetchDoctorId(int userId) {
        String sql = "SELECT d.id FROM doctors d "
                + "JOIN users u ON d.person_id = u.person_id "
                + "WHERE u.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id"); // Return the doctor_id
            } else {
                JOptionPane.showMessageDialog(this, "No doctor found for the given user ID.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching doctor ID: " + e.getMessage());
        }
        return -1; // Return -1 if no doctor_id is found
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        doctorTabbedPane = new javax.swing.JTabbedPane();
        scheduleManagementTab = new javax.swing.JPanel();
        scheduleScrollPane = new javax.swing.JScrollPane();
        scheduleTable = new javax.swing.JTable();
        buttonsPanel = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        refreshButton = new javax.swing.JButton();
        BtnLogOut = new javax.swing.JButton();
        appointmentManagementTab = new javax.swing.JPanel();
        appointmentScrollPane = new javax.swing.JScrollPane();
        appointmentTable = new javax.swing.JTable();
        btnPanel = new javax.swing.JPanel();
        approveButton = new javax.swing.JButton();
        rejectButton = new javax.swing.JButton();
        refreshBtn = new javax.swing.JButton();
        patientHistoryTab = new javax.swing.JPanel();
        searchField = new javax.swing.JTextField();
        searchButton = new javax.swing.JButton();
        historyScrollPane = new javax.swing.JScrollPane();
        historyTable = new javax.swing.JTable();
        prescriptionCreationTab = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        contorlPanel = new javax.swing.JPanel();
        btnPrescriptionCreation = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        WorkArea = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        CmbPatient = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        CmbLabTest = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        BtnSubmitTestRequest = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        CmbManager = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        TxtDate = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        BtnViewDetails = new javax.swing.JButton();
        CmbFilDate = new javax.swing.JComboBox<>();

        scheduleManagementTab.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        scheduleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Date", "Time Slot", "Status"
            }
        ));
        scheduleScrollPane.setViewportView(scheduleTable);

        scheduleManagementTab.add(scheduleScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 710, 500));

        addButton.setText("Add Slot");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        updateButton.setText("Update Slot");
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        deleteButton.setText("Delete Slot");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        refreshButton.setText("Refresh");
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        BtnLogOut.setText("Log Out!");
        BtnLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnLogOutActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout buttonsPanelLayout = new javax.swing.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addGap(107, 107, 107)
                .addGroup(buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(refreshButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(deleteButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(updateButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(addButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(BtnLogOut, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(84, Short.MAX_VALUE))
        );

        buttonsPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {addButton, deleteButton, refreshButton, updateButton});

        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buttonsPanelLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(BtnLogOut)
                .addGap(46, 46, 46)
                .addComponent(addButton)
                .addGap(75, 75, 75)
                .addComponent(updateButton)
                .addGap(74, 74, 74)
                .addComponent(deleteButton)
                .addGap(74, 74, 74)
                .addComponent(refreshButton)
                .addContainerGap(108, Short.MAX_VALUE))
        );

        buttonsPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {addButton, deleteButton, refreshButton, updateButton});

        scheduleManagementTab.add(buttonsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 0, 290, 530));

        doctorTabbedPane.addTab("Schedule Management", scheduleManagementTab);

        appointmentManagementTab.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        appointmentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Patient Name", "Time Slot", "Status"
            }
        ));
        appointmentScrollPane.setViewportView(appointmentTable);

        appointmentManagementTab.add(appointmentScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 680, 500));

        approveButton.setText("Approve");
        approveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                approveButtonActionPerformed(evt);
            }
        });

        rejectButton.setText("Reject");
        rejectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rejectButtonActionPerformed(evt);
            }
        });

        refreshBtn.setText("Refresh");
        refreshBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout btnPanelLayout = new javax.swing.GroupLayout(btnPanel);
        btnPanel.setLayout(btnPanelLayout);
        btnPanelLayout.setHorizontalGroup(
            btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnPanelLayout.createSequentialGroup()
                .addGap(117, 117, 117)
                .addGroup(btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(refreshBtn)
                    .addComponent(rejectButton)
                    .addComponent(approveButton))
                .addContainerGap(117, Short.MAX_VALUE))
        );

        btnPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {approveButton, refreshBtn, rejectButton});

        btnPanelLayout.setVerticalGroup(
            btnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btnPanelLayout.createSequentialGroup()
                .addGap(149, 149, 149)
                .addComponent(approveButton)
                .addGap(80, 80, 80)
                .addComponent(rejectButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 83, Short.MAX_VALUE)
                .addComponent(refreshBtn)
                .addGap(149, 149, 149))
        );

        btnPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {approveButton, refreshBtn, rejectButton});

        appointmentManagementTab.add(btnPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(700, 0, 310, 530));

        doctorTabbedPane.addTab("Appointment Management", appointmentManagementTab);

        patientHistoryTab.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        patientHistoryTab.add(searchField, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 30, 190, 30));

        searchButton.setText("Search");
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });
        patientHistoryTab.add(searchButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 30, -1, -1));

        historyTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Date", "Diagnosis", "Treatment"
            }
        ));
        historyScrollPane.setViewportView(historyTable);

        patientHistoryTab.add(historyScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 80, 740, -1));

        doctorTabbedPane.addTab("Patient History", patientHistoryTab);

        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        btnPrescriptionCreation.setText("Create Prescription");
        btnPrescriptionCreation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrescriptionCreationActionPerformed(evt);
            }
        });

        jButton1.setText(" Prescriptions List");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout contorlPanelLayout = new javax.swing.GroupLayout(contorlPanel);
        contorlPanel.setLayout(contorlPanelLayout);
        contorlPanelLayout.setHorizontalGroup(
            contorlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contorlPanelLayout.createSequentialGroup()
                .addGap(332, 332, 332)
                .addComponent(btnPrescriptionCreation)
                .addGap(32, 32, 32)
                .addComponent(jButton1)
                .addContainerGap(369, Short.MAX_VALUE))
        );
        contorlPanelLayout.setVerticalGroup(
            contorlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contorlPanelLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(contorlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPrescriptionCreation)
                    .addComponent(jButton1))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        jSplitPane1.setTopComponent(contorlPanel);

        WorkArea.setLayout(new java.awt.CardLayout());
        jSplitPane1.setRightComponent(WorkArea);

        javax.swing.GroupLayout prescriptionCreationTabLayout = new javax.swing.GroupLayout(prescriptionCreationTab);
        prescriptionCreationTab.setLayout(prescriptionCreationTabLayout);
        prescriptionCreationTabLayout.setHorizontalGroup(
            prescriptionCreationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, prescriptionCreationTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );
        prescriptionCreationTabLayout.setVerticalGroup(
            prescriptionCreationTabLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(prescriptionCreationTabLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );

        doctorTabbedPane.addTab("Prescription", prescriptionCreationTab);

        CmbPatient.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setText("Patient");

        CmbLabTest.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("Lab Test");

        BtnSubmitTestRequest.setText("Submit");
        BtnSubmitTestRequest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnSubmitTestRequestActionPerformed(evt);
            }
        });

        jLabel3.setText("Manager");

        CmbManager.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel4.setText("Date");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(TxtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(263, 263, 263))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(331, 331, 331)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(CmbManager, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel2))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(CmbPatient, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(CmbLabTest, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(390, 390, 390)
                        .addComponent(BtnSubmitTestRequest, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(471, Short.MAX_VALUE))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {CmbLabTest, CmbManager, CmbPatient});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(TxtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CmbPatient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CmbLabTest, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(CmbManager, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(56, 56, 56)
                .addComponent(BtnSubmitTestRequest)
                .addContainerGap(282, Short.MAX_VALUE))
        );

        doctorTabbedPane.addTab("Create Test Requests", jPanel1);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Request ID", "Patient Name", "Test", "Status", "Assigned Manager"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        BtnViewDetails.setText("View Details");
        BtnViewDetails.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnViewDetailsActionPerformed(evt);
            }
        });

        CmbFilDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbFilDateActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(BtnViewDetails)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 888, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CmbFilDate, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(117, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addComponent(CmbFilDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(BtnViewDetails)
                .addContainerGap(158, Short.MAX_VALUE))
        );

        doctorTabbedPane.addTab("View Test requests", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(doctorTabbedPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(doctorTabbedPane)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // TODO add your handling code here:
        String selectedDate = JOptionPane.showInputDialog(this, "Enter Date (YYYY-MM-DD):");
        String timeSlot = JOptionPane.showInputDialog(this, "Enter Time Slot (e.g., 9:00 AM):");

        if (selectedDate != null && timeSlot != null && !selectedDate.trim().isEmpty() && !timeSlot.trim().isEmpty()) {
            String sql = """
            INSERT INTO schedules (doctor_id, date, time_slot, status)
            VALUES (?, ?, ?, 'Available');
        """;

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, doctorId);
                stmt.setDate(2, java.sql.Date.valueOf(selectedDate));
                stmt.setString(3, timeSlot);
                stmt.executeUpdate();
                populateDoctorScheduleTable();
                JOptionPane.showMessageDialog(this, "Slot added successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding slot: " + e.getMessage());
            }
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        // TODO add your handling code here:
        // TODO add your handling code here:
        int selectedRow = scheduleTable.getSelectedRow();
        if (selectedRow >= 0) {
            int scheduleId = (int) scheduleTable.getValueAt(selectedRow, 0);
            String newStatus = JOptionPane.showInputDialog(this, "Enter New Status (Available, TBD, Booked, Unavailable):");

            if (newStatus != null && !newStatus.trim().isEmpty()) {
                String sql = "UPDATE schedules SET status = ? WHERE id = ?";

                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setString(1, newStatus);
                    stmt.setInt(2, scheduleId);
                    stmt.executeUpdate();
                    populateDoctorScheduleTable();
                    JOptionPane.showMessageDialog(this, "Slot updated successfully!");
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, "Error updating slot: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a slot to update.");
        }
    }//GEN-LAST:event_updateButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // TODO add your handling code here:
        int selectedRow = scheduleTable.getSelectedRow();
        if (selectedRow >= 0) {
            int slotId = (int) scheduleTable.getValueAt(selectedRow, 0); // Fetch slot ID from the table

            try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement statement = connection.prepareStatement("DELETE FROM schedules WHERE id = ?")) {

                statement.setInt(1, slotId);
                int rowsDeleted = statement.executeUpdate();

                if (rowsDeleted > 0) {
                    populateDoctorScheduleTable();
                    JOptionPane.showMessageDialog(this, "Time Slot Deleted Successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Slot not found.");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error Deleting Time Slot: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please Select a Row to Delete!");
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        // TODO add your handling code here:
        JOptionPane.showMessageDialog(this, "Schedule Refreshed!");
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void approveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_approveButtonActionPerformed
        // TODO add your handling code here:
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow >= 0) {
            int appointmentId = (int) appointmentTable.getValueAt(selectedRow, 0);

            try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE appointments SET status = ? WHERE id = ?")) {

                statement.setString(1, "Approved");
                statement.setInt(2, appointmentId);
                statement.executeUpdate();

                populateAppointmentTable(); // Refresh table
                JOptionPane.showMessageDialog(this, "Appointment Approved!");

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error Approving Appointment: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please Select an Appointment to Approve!");
        }
    }//GEN-LAST:event_approveButtonActionPerformed

    private void rejectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rejectButtonActionPerformed
        // TODO add your handling code here:  
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow >= 0) {
            int appointmentId = (int) appointmentTable.getValueAt(selectedRow, 0);

            try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement statement = connection.prepareStatement("UPDATE appointments SET status = ? WHERE id = ?")) {

                statement.setString(1, "Rejected");
                statement.setInt(2, appointmentId);
                statement.executeUpdate();

                populateAppointmentTable(); // Refresh table
                JOptionPane.showMessageDialog(this, "Appointment Rejected!");

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error Rejecting Appointment: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please Select an Appointment to Reject!");
        }
    }//GEN-LAST:event_rejectButtonActionPerformed

    private void refreshBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtnActionPerformed
        // TODO add your handling code here:
        populateAppointmentTable(); // Reload data
        JOptionPane.showMessageDialog(this, "Appointments Refreshed!");
    }//GEN-LAST:event_refreshBtnActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        // TODO add your handling code here:
        String searchQuery = searchField.getText().trim(); // Get search input

        String sql = "SELECT ph.id, p.name AS patient_name, ph.date, ph.diagnosis, ph.treatment "
                + "FROM patient_history ph "
                + "JOIN patients pt ON ph.patient_id = pt.id "
                + "JOIN persons p ON pt.person_id = p.id "
                + "WHERE p.name LIKE ?";

        try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, "%" + searchQuery + "%"); // Partial match
            ResultSet resultSet = statement.executeQuery();

            DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
            model.setRowCount(0); // Clear existing rows

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String patientName = resultSet.getString("patient_name");
                Date date = resultSet.getDate("date");
                String diagnosis = resultSet.getString("diagnosis");
                String treatment = resultSet.getString("treatment");
                model.addRow(new Object[]{id, patientName, date, diagnosis, treatment});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error Fetching Patient History: " + e.getMessage());
        }
    }//GEN-LAST:event_searchButtonActionPerformed

    private void CmbFilDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmbFilDateActionPerformed
        String selectedFilter = (String) CmbFilDate.getSelectedItem();

        // Base SQL query
        String sql = "SELECT tr.id, ps.name AS patient_name, lt.name AS test_name, tr.status, pm.name AS manager_name "
                + "FROM test_requests tr "
                + "JOIN patients p ON tr.patient_id = p.id "
                + "JOIN persons ps ON p.person_id = ps.id "
                + "JOIN lab_tests lt ON tr.lab_test_id = lt.id "
                + "JOIN users m ON tr.manager_id = m.id "
                + "JOIN persons pm ON m.person_id = pm.id ";

        // Apply filter based on selection
        if (!"All".equals(selectedFilter)) {
            sql += "WHERE tr.request_date >= ? ";
        }

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (!"All".equals(selectedFilter)) {
                // Determine the filter date based on the selection
                java.sql.Date filterDate = calculateFilterDate(selectedFilter);
                stmt.setDate(1, filterDate);
            }

            ResultSet rs = stmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0); // Clear existing rows

            while (rs.next()) {
                int id = rs.getInt("id");
                String patientName = rs.getString("patient_name");
                String testName = rs.getString("test_name");
                String status = rs.getString("status");
                String managerName = rs.getString("manager_name");
                model.addRow(new Object[]{id, patientName, testName, status, managerName});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching test requests: " + e.getMessage());
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_CmbFilDateActionPerformed

    private void BtnViewDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnViewDetailsActionPerformed
        int selectedRow = jTable2.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a test request to view details.");
            return;
        }

        // Get the selected test request ID
        int testRequestId = (int) jTable2.getValueAt(selectedRow, 0);

        // Query to fetch detailed information
        String sql = "SELECT tr.id, ps.name AS patient_name, lt.name AS test_name, tr.request_date, tr.status, "
                + "pm.name AS manager_name, tr.technician_id, d.id AS doctor_id, per.name AS doctor_name "
                + "FROM test_requests tr "
                + "JOIN patients p ON tr.patient_id = p.id "
                + "JOIN persons ps ON p.person_id = ps.id "
                + "JOIN lab_tests lt ON tr.lab_test_id = lt.id "
                + "JOIN users m ON tr.manager_id = m.id "
                + "JOIN persons pm ON m.person_id = pm.id "
                + "LEFT JOIN doctors d ON tr.doctor_id = d.id "
                + "LEFT JOIN persons per ON d.person_id = per.id "
                + "WHERE tr.id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, testRequestId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Extract details
                int id = rs.getInt("id");
                String patientName = rs.getString("patient_name");
                String testName = rs.getString("test_name");
                String requestDate = rs.getString("request_date");
                String status = rs.getString("status");
                String managerName = rs.getString("manager_name");
                int technicianId = rs.getInt("technician_id");
                int doctorId = rs.getInt("doctor_id");

                // Build the details string
                StringBuilder details = new StringBuilder();
                details.append("Test Request ID: ").append(id).append("\n");
                details.append("Patient Name: ").append(patientName).append("\n");
                details.append("Test Name: ").append(testName).append("\n");
                details.append("Request Date: ").append(requestDate).append("\n");
                details.append("Status: ").append(status).append("\n");
                details.append("Assigned Manager: ").append(managerName).append("\n");
                details.append("Technician ID: ").append(technicianId == 0 ? "Not Assigned" : technicianId).append("\n");
                details.append("Doctor ID: ").append(doctorId == 0 ? "Not Assigned" : doctorId).append("\n");

                // Show details in JOptionPane
                JOptionPane.showMessageDialog(this, details.toString(), "Test Request Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No details found for the selected test request.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching test request details: " + e.getMessage());
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnViewDetailsActionPerformed

    private void BtnSubmitTestRequestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnSubmitTestRequestActionPerformed
        try {
            String patient = (String) CmbPatient.getSelectedItem();
            String labTest = (String) CmbLabTest.getSelectedItem();
            String manager = (String) CmbManager.getSelectedItem();
            String date = TxtDate.getText().trim();

            if (patient == null || labTest == null || manager == null || date.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled!");
                return;
            }

            int patientId = Integer.parseInt(patient.split(" - ")[0]);
            int labTestId = Integer.parseInt(labTest.split(" - ")[0]);
            int managerId = Integer.parseInt(manager.split(" - ")[0]);

            // Fetch the actual doctor_id from the user_id
            int actualDoctorId = fetchDoctorId(doctorId); // Assuming doctorId is passed as user_id
            if (actualDoctorId == -1) {
                return; // Exit if no valid doctor_id is found
            }

            String sql = "INSERT INTO test_requests (patient_id, doctor_id, lab_test_id, manager_id, request_date, status) "
                    + "VALUES (?, ?, ?, ?, ?, 'Created')";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, patientId);
                stmt.setInt(2, actualDoctorId); // Use the fetched doctor_id
                stmt.setInt(3, labTestId);
                stmt.setInt(4, managerId);
                stmt.setDate(5, java.sql.Date.valueOf(date)); // Convert the date string to SQL Date
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Test request created successfully!");
                populateTestRequestsTable(null);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating test request: " + e.getMessage());
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnSubmitTestRequestActionPerformed

    private void btnPrescriptionCreationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrescriptionCreationActionPerformed
        // TODO add your handling code here:
        System.out.println("Docotr ID : " + doctorId);
        prescriptionCreation pc = new prescriptionCreation(WorkArea, doctorId);
        WorkArea.add("Presciption Creation", pc);
        CardLayout layout = (CardLayout) WorkArea.getLayout();
        layout.next(WorkArea);

    }//GEN-LAST:event_btnPrescriptionCreationActionPerformed


    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            // TODO add your handling code here:
            allPrescription ap = new allPrescription(WorkArea, doctorId);
            WorkArea.add("all Prescriptin ", ap);
            CardLayout layout = (CardLayout) WorkArea.getLayout();
            layout.next(WorkArea);
        } catch (SQLException ex) {
            Logger.getLogger(doctorDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void BtnLogOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnLogOutActionPerformed
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
        }        // TODO add your handling code here:
    }//GEN-LAST:event_BtnLogOutActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnLogOut;
    private javax.swing.JButton BtnSubmitTestRequest;
    private javax.swing.JButton BtnViewDetails;
    private javax.swing.JComboBox<String> CmbFilDate;
    private javax.swing.JComboBox<String> CmbLabTest;
    private javax.swing.JComboBox<String> CmbManager;
    private javax.swing.JComboBox<String> CmbPatient;
    private javax.swing.JTextField TxtDate;
    private javax.swing.JPanel WorkArea;
    private javax.swing.JButton addButton;
    private javax.swing.JPanel appointmentManagementTab;
    private javax.swing.JScrollPane appointmentScrollPane;
    private javax.swing.JTable appointmentTable;
    private javax.swing.JButton approveButton;
    private javax.swing.JPanel btnPanel;
    private javax.swing.JButton btnPrescriptionCreation;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JPanel contorlPanel;
    private javax.swing.JButton deleteButton;
    private javax.swing.JTabbedPane doctorTabbedPane;
    private javax.swing.JScrollPane historyScrollPane;
    private javax.swing.JTable historyTable;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable2;
    private javax.swing.JPanel patientHistoryTab;
    private javax.swing.JPanel prescriptionCreationTab;
    private javax.swing.JButton refreshBtn;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton rejectButton;
    private javax.swing.JPanel scheduleManagementTab;
    private javax.swing.JScrollPane scheduleScrollPane;
    private javax.swing.JTable scheduleTable;
    private javax.swing.JButton searchButton;
    private javax.swing.JTextField searchField;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
