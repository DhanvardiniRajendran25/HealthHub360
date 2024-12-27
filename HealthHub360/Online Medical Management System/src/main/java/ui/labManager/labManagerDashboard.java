/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.labManager;

import java.awt.CardLayout;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import model.DiagnosticLabEnterprise.LabReport;
import model.DiagnosticLabEnterprise.LabTechnician;
import model.DiagnosticLabEnterprise.TestRequest;
import model.DiagnosticLabEnterpriseDAO.LabManagerDAO;
import model.DiagnosticLabEnterpriseDAO.LabReportDAO;
import model.DiagnosticLabEnterpriseDAO.TestRequestDAO;
import model.DiagnosticLabEnterpriseDAO.LabTechnicianDAO;
import model.DiagnosticLabEnterpriseDAO.LabTestDAO;
import model.DiagnosticLabEnterpriseService.LabManagerService;
import ui.DatabaseUtil;
import ui.LoginScreen;

/**
 *
 * @author AnjanaSruthiR
 */
public class labManagerDashboard extends javax.swing.JPanel {

    private LabManagerService labManagerService;
    private int loggedInLabManagerId;

    /**
     * Creates new form labManagerWorkarea
     */
    public labManagerDashboard(int managerId, Connection connection) {
        this.loggedInLabManagerId = managerId;
        initComponents();
        populateTestRequests(null);
        populateTechnicianDropdown();
        populateTechnicianTable(null, null);
        populateTechnicianDropdownForFilter();
        populateReportsTable(null, null);
        populateTechnicianDropdownForReports();

        SwingUtilities.invokeLater(() -> notifyNewRequests(managerId));
    }

public void notifyNewRequests(int managerId) {
    try {
        LabManagerService labManagerService = new LabManagerService(
                new LabManagerDAO(DatabaseUtil.getConnection()),
                new TestRequestDAO(DatabaseUtil.getConnection()),
                new LabTechnicianDAO(DatabaseUtil.getConnection()),
                new LabTestDAO(DatabaseUtil.getConnection())
        );

        List<Map<String, Object>> newRequests = labManagerService.getRequestsByStatus(managerId, "Created");
        List<Map<String, Object>> updatedRequests = labManagerService.getRequestsByStatus(managerId, "Processed");

        StringBuilder message = new StringBuilder();

        if (!newRequests.isEmpty()) {
            message.append("You have new requests:\n");
            for (Map<String, Object> request : newRequests) {
                message.append("Request ID: ").append(request.get("ID")).append("\n")
                        .append("Status: ").append(request.get("Status")).append("\n")
                        .append("Assigned Date: ").append(request.get("RequestDate")).append("\n\n");
            }
        }

        if (!updatedRequests.isEmpty()) {
            if (!newRequests.isEmpty()) {
                message.append("\n");
            }
            message.append("You have updated requests with reports:\n");
            for (Map<String, Object> request : updatedRequests) {
                message.append("Request ID: ").append(request.get("ID")).append("\n")
                        .append("Type: ").append(request.get("Type")).append("\n")
                        .append("Description: ").append(request.get("Description")).append("\n")
                        .append("Assigned Date: ").append(request.get("Date")).append("\n\n");
            }
        }

        if (message.length() > 0) {
            SwingUtilities.invokeLater(() -> 
                JOptionPane.showMessageDialog(this, message.toString(), "Requests Notification", JOptionPane.INFORMATION_MESSAGE)
            );
        } else {
            JOptionPane.showMessageDialog(this, "No new or updated requests.", "Requests Notification", JOptionPane.INFORMATION_MESSAGE);
        }
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error checking for requests: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    private void populateTestRequests(String status) {
        try {
            LabManagerService labManagerService = new LabManagerService(
                    new LabManagerDAO(DatabaseUtil.getConnection()),
                    new TestRequestDAO(DatabaseUtil.getConnection()),
                    new LabTechnicianDAO(DatabaseUtil.getConnection()),
                    new LabTestDAO(DatabaseUtil.getConnection())
            );

            List<TestRequest> testRequests = labManagerService.getTestRequestsForManager(5, status);

            Object[][] data = new Object[testRequests.size()][5];
            for (int i = 0; i < testRequests.size(); i++) {
                TestRequest req = testRequests.get(i);
                try {
                    String testName = labManagerService.getLabTestName(req.getLabTestId());
                    String technicianName = labManagerService.getTechnicianName(req.getTechnicianId());
                    data[i] = new Object[]{
                        req.getId(),
                        req.getPatientId(),
                        testName,
                        req.getStatus(),
                        technicianName
                    };
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this,
                            "Error processing request ID " + req.getId() + ": " + e.getMessage());
                    data[i] = new Object[]{
                        req.getId(),
                        req.getPatientId(),
                        "Error fetching test name",
                        req.getStatus(),
                        "Error fetching technician name"
                    };
                }
            }

            TblRequests.setModel(new javax.swing.table.DefaultTableModel(
                    data,
                    new String[]{"Request ID", "Patient ID", "Test Type", "Status", "Assigned Technician"}
            ));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading test requests: " + e.getMessage());
        }
    }

    private void populateTechnicianDropdown() {
        try {
            LabTechnicianDAO labTechnicianDAO = new LabTechnicianDAO(DatabaseUtil.getConnection());
            List<LabTechnician> technicians = labTechnicianDAO.getAllLabTechnicians();
            CmbTech.removeAllItems();
            for (LabTechnician technician : technicians) {
                CmbTech.addItem(technician.getId() + " - " + technician.getName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading technicians: " + e.getMessage());
        }
    }

    private void populateTechnicianTable(Integer technicianId, String status) {
        try {
            LabTechnicianDAO labTechnicianDAO = new LabTechnicianDAO(DatabaseUtil.getConnection());
            Map<Integer, Integer> workloadMap = labTechnicianDAO.getTechnicianWorkloadFiltered(technicianId, status);

            System.out.println("Workload Map: " + workloadMap);

            List<LabTechnician> technicians = labTechnicianDAO.getAllLabTechnicians();

            Object[][] data = technicians.stream()
                    .filter(tech -> technicianId == null || tech.getId() == technicianId) 
                    .map(tech -> new Object[]{
                tech.getId(),
                tech.getName(),
                tech.getSpecialization(),
                workloadMap.getOrDefault(tech.getId(), 0) 
            }).toArray(Object[][]::new);

            TblTech.setModel(new javax.swing.table.DefaultTableModel(
                    data,
                    new String[]{"ID", "Name", "Specialization", "Workload"}
            ));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading technician data: " + e.getMessage());
        }
    }

    private void populateTechnicianDropdownForFilter() {
        try {
            LabTechnicianDAO labTechnicianDAO = new LabTechnicianDAO(DatabaseUtil.getConnection());
            List<LabTechnician> technicians = labTechnicianDAO.getAllLabTechnicians();

            CmbFilTech.removeAllItems(); 
            CmbFilTech.addItem("All"); 

            for (LabTechnician technician : technicians) {
                CmbFilTech.addItem(technician.getId() + " - " + technician.getName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading technicians for filter: " + e.getMessage());
        }
    }

    private void populateReportsTable(Integer technicianId, String statusFilter) {
        try {
            LabReportDAO labReportDAO = new LabReportDAO(DatabaseUtil.getConnection());
            TestRequestDAO testRequestDAO = new TestRequestDAO(DatabaseUtil.getConnection());

            List<LabReport> labReports = (technicianId == null || technicianId == -1)
                    ? labReportDAO.getAllLabReports()
                    : labReportDAO.getLabReportsByTechnician(technicianId);

            if (statusFilter != null && !"All".equals(statusFilter)) {
                labReports = labReports.stream().filter(report -> {
                    try {
                        TestRequest request = testRequestDAO.getTestRequestById(report.getTestRequestId());
                        if ("Sent".equals(statusFilter)) {
                            return "Sent to Doctor/Patient".equals(request.getStatus());
                        } else if ("Not Sent".equals(statusFilter)) {
                            return !"Sent to Doctor/Patient".equals(request.getStatus());
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return false;
                }).toList();
            }

            Object[][] data = new Object[labReports.size()][5]; 
            for (int i = 0; i < labReports.size(); i++) {
                LabReport report = labReports.get(i);
                data[i] = new Object[]{
                    report.getId(), 
                    report.getTestRequestId(), 
                    report.getLabTechnicianId(), 
                    report.getResult(), 
                    report.getGeneratedDate() 
                };
            }

            TblReports.setModel(new javax.swing.table.DefaultTableModel(
                    data,
                    new String[]{"Report ID", "Test Request ID", "Technician ID", "Result", "Generated Date"}
            ));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading reports: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTechnicianDropdownForReports() {
        try {
            LabTechnicianDAO labTechnicianDAO = new LabTechnicianDAO(DatabaseUtil.getConnection());
            List<LabTechnician> technicians = labTechnicianDAO.getAllLabTechnicians();

            CmbTech1.removeAllItems(); 
            CmbTech1.addItem("All"); 

            for (LabTechnician technician : technicians) {
                CmbTech1.addItem(technician.getId() + " - " + technician.getName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading technicians for reports filter: " + e.getMessage());
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
        ManageRequestsPanel = new javax.swing.JPanel();
        CmbTech = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        TblRequests = new javax.swing.JTable();
        BtnAssTech = new javax.swing.JButton();
        BtnRefresh = new javax.swing.JButton();
        CmbStatusMR = new javax.swing.JComboBox<>();
        TechnicianOverviewPanel = new javax.swing.JPanel();
        CmbStatusMS = new javax.swing.JComboBox<>();
        CmbFilTech = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        TblTech = new javax.swing.JTable();
        BtnApplyFilter = new javax.swing.JButton();
        ReportsPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        TblReports = new javax.swing.JTable();
        BtnRefresh1 = new javax.swing.JButton();
        CmbTech1 = new javax.swing.JComboBox<>();
        BtnAssDoc = new javax.swing.JButton();
        BtnViewDetailedReport = new javax.swing.JButton();
        CmbReportStatus = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        BtnLogOut1 = new javax.swing.JButton();

        setBackground(new java.awt.Color(247, 249, 252));

        jTabbedPane1.setBackground(new java.awt.Color(194, 217, 250));

        ManageRequestsPanel.setBackground(new java.awt.Color(247, 249, 252));

        CmbTech.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        TblRequests.setBackground(new java.awt.Color(187, 208, 237));
        TblRequests.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Request ID", "Patient ID", "Test Type", "Status", "Assigned Lab Technician"
            }
        ));
        jScrollPane1.setViewportView(TblRequests);

        BtnAssTech.setText("Assign Technician");
        BtnAssTech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAssTechActionPerformed(evt);
            }
        });

        BtnRefresh.setText("Refresh");
        BtnRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnRefreshActionPerformed(evt);
            }
        });

        CmbStatusMR.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Pending", "Assigned", "Completed" }));
        CmbStatusMR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbStatusMRActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ManageRequestsPanelLayout = new javax.swing.GroupLayout(ManageRequestsPanel);
        ManageRequestsPanel.setLayout(ManageRequestsPanelLayout);
        ManageRequestsPanelLayout.setHorizontalGroup(
            ManageRequestsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ManageRequestsPanelLayout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(ManageRequestsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(CmbStatusMR, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 809, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(ManageRequestsPanelLayout.createSequentialGroup()
                        .addComponent(BtnRefresh)
                        .addGap(462, 462, 462)
                        .addComponent(CmbTech, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(BtnAssTech)))
                .addGap(26, 26, 26))
        );
        ManageRequestsPanelLayout.setVerticalGroup(
            ManageRequestsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ManageRequestsPanelLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(CmbStatusMR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(ManageRequestsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnAssTech)
                    .addComponent(BtnRefresh)
                    .addComponent(CmbTech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(95, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Manage Requests", ManageRequestsPanel);

        TechnicianOverviewPanel.setBackground(new java.awt.Color(247, 249, 252));

        CmbStatusMS.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Assigned", "Processed" }));
        CmbStatusMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbStatusMSActionPerformed(evt);
            }
        });

        CmbFilTech.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All" }));
        CmbFilTech.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbFilTechActionPerformed(evt);
            }
        });

        TblTech.setBackground(new java.awt.Color(187, 208, 237));
        TblTech.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Specialization", "Workload"
            }
        ));
        jScrollPane2.setViewportView(TblTech);

        BtnApplyFilter.setText("Search");
        BtnApplyFilter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnApplyFilterActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout TechnicianOverviewPanelLayout = new javax.swing.GroupLayout(TechnicianOverviewPanel);
        TechnicianOverviewPanel.setLayout(TechnicianOverviewPanelLayout);
        TechnicianOverviewPanelLayout.setHorizontalGroup(
            TechnicianOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TechnicianOverviewPanelLayout.createSequentialGroup()
                .addGroup(TechnicianOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(TechnicianOverviewPanelLayout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CmbStatusMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(CmbFilTech, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(BtnApplyFilter))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, TechnicianOverviewPanelLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 817, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        TechnicianOverviewPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {CmbFilTech, CmbStatusMS});

        TechnicianOverviewPanelLayout.setVerticalGroup(
            TechnicianOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TechnicianOverviewPanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(TechnicianOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CmbStatusMS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CmbFilTech, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtnApplyFilter))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(114, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Manage Staff", TechnicianOverviewPanel);

        ReportsPanel.setBackground(new java.awt.Color(247, 249, 252));

        TblReports.setBackground(new java.awt.Color(187, 208, 237));
        TblReports.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(TblReports);

        BtnRefresh1.setText("Refresh");
        BtnRefresh1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnRefresh1ActionPerformed(evt);
            }
        });

        CmbTech1.setBackground(new java.awt.Color(215, 191, 204));
        CmbTech1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbTech1ActionPerformed(evt);
            }
        });

        BtnAssDoc.setText("Send Reports");
        BtnAssDoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnAssDocActionPerformed(evt);
            }
        });

        BtnViewDetailedReport.setText("View Details");
        BtnViewDetailedReport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnViewDetailedReportActionPerformed(evt);
            }
        });

        CmbReportStatus.setBackground(new java.awt.Color(215, 191, 204));
        CmbReportStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Sent", "Not Sent" }));
        CmbReportStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbReportStatusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout ReportsPanelLayout = new javax.swing.GroupLayout(ReportsPanel);
        ReportsPanel.setLayout(ReportsPanelLayout);
        ReportsPanelLayout.setHorizontalGroup(
            ReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReportsPanelLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(ReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(ReportsPanelLayout.createSequentialGroup()
                        .addComponent(BtnRefresh1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BtnViewDetailedReport)
                        .addGap(18, 18, 18)
                        .addComponent(BtnAssDoc))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 809, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ReportsPanelLayout.createSequentialGroup()
                        .addComponent(CmbTech1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(33, 33, 33)
                        .addComponent(CmbReportStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        ReportsPanelLayout.setVerticalGroup(
            ReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ReportsPanelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(ReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CmbTech1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CmbReportStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(ReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BtnAssDoc)
                    .addComponent(BtnRefresh1)
                    .addComponent(BtnViewDetailedReport))
                .addContainerGap(105, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Lab Reports", ReportsPanel);

        jPanel1.setBackground(new java.awt.Color(247, 249, 252));

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 3, 24)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Welcome Lab Manager");

        BtnLogOut1.setBackground(new java.awt.Color(199, 180, 127));
        BtnLogOut1.setFont(new java.awt.Font("Helvetica Neue", 3, 13)); // NOI18N
        BtnLogOut1.setText("<- Log Out");
        BtnLogOut1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnLogOut1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(BtnLogOut1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(224, 224, 224))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BtnLogOut1)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 482, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BtnAssTechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAssTechActionPerformed
        int selectedRow = TblRequests.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a test request to assign.");
            return;
        }

        String requestId = TblRequests.getValueAt(selectedRow, 0).toString();
        String selectedTechnician = (String) CmbTech.getSelectedItem();

        if (selectedTechnician == null || selectedTechnician.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a technician.");
            return;
        }

        int technicianId = Integer.parseInt(selectedTechnician.split(" - ")[0]);

        try {
            LabManagerService labManagerService = new LabManagerService(
                    new LabManagerDAO(DatabaseUtil.getConnection()),
                    new TestRequestDAO(DatabaseUtil.getConnection()),
                    new LabTechnicianDAO(DatabaseUtil.getConnection()), // Add LabTechnicianDAO
                    new LabTestDAO(DatabaseUtil.getConnection())
            );
            labManagerService.assignTechnician(Integer.parseInt(requestId), technicianId);
            JOptionPane.showMessageDialog(this, "Technician assigned successfully.");
            populateTestRequests(null);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error assigning technician: " + e.getMessage());
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnAssTechActionPerformed

    private void BtnRefreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnRefreshActionPerformed
        populateTestRequests(null);
        populateTechnicianDropdown();
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnRefreshActionPerformed

    private void CmbStatusMRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmbStatusMRActionPerformed
        try {
            String selectedStatus = (String) CmbStatusMR.getSelectedItem();
            String status = "All".equals(selectedStatus) ? null : selectedStatus;

            // Fetch requests filtered by status
            populateTestRequests(status);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error applying status filter: " + e.getMessage());
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_CmbStatusMRActionPerformed

    private void CmbStatusMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmbStatusMSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CmbStatusMSActionPerformed

    private void CmbFilTechActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmbFilTechActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CmbFilTechActionPerformed

    private void BtnApplyFilterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnApplyFilterActionPerformed
        try {
            String selectedTechnician = (String) CmbFilTech.getSelectedItem();
            String selectedStatus = (String) CmbStatusMS.getSelectedItem();

            Integer technicianId = null;
            if (selectedTechnician != null && !"All".equals(selectedTechnician)) {
                technicianId = Integer.parseInt(selectedTechnician.split(" - ")[0]);
            }

            String status = null;
            if (selectedStatus != null && !"All".equals(selectedStatus)) {
                status = selectedStatus;
            }

            // Populate the technician table based on filters
            populateTechnicianTable(technicianId, status);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error applying filters: " + e.getMessage());
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnApplyFilterActionPerformed

    private void BtnLogOut1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnLogOut1ActionPerformed
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
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnLogOut1ActionPerformed

    private void BtnRefresh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnRefresh1ActionPerformed
        populateReportsTable(null, null);
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnRefresh1ActionPerformed

    private void BtnViewDetailedReportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnViewDetailedReportActionPerformed
        int selectedRow = TblReports.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a report to view details.");
            return;
        }

        int reportId = (int) TblReports.getValueAt(selectedRow, 0); // Assuming the first column is the report ID

        try {
            LabReportDAO labReportDAO = new LabReportDAO(DatabaseUtil.getConnection());
            LabReport labReport = labReportDAO.getLabReportById(reportId);

            if (labReport != null) {
                JOptionPane.showMessageDialog(this,
                        "Report ID: " + labReport.getId()
                        + "\nTest Request ID: " + labReport.getTestRequestId()
                        + "\nLab Technician ID: " + labReport.getLabTechnicianId()
                        + "\nResult: " + labReport.getResult()
                        + "\nGenerated Date: " + labReport.getGeneratedDate(),
                        "Lab Report Details",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Report not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error retrieving report details: " + e.getMessage());
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnViewDetailedReportActionPerformed

    private void CmbTech1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmbTech1ActionPerformed
        try {
            String selectedTechnician = (String) CmbTech1.getSelectedItem();
            Integer technicianId = null;
            if (selectedTechnician != null && !"All".equals(selectedTechnician)) {
                technicianId = Integer.parseInt(selectedTechnician.split(" - ")[0]);
            }
            populateReportsTable(technicianId, null); // Pass statusFilter as null
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error applying technician filter: " + e.getMessage());
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_CmbTech1ActionPerformed

    private void BtnAssDocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnAssDocActionPerformed
        int selectedRow = TblReports.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a report to send.");
            return;
        }

        int reportId = (int) TblReports.getValueAt(selectedRow, 0); // Assuming the first column contains the report ID

        try {
            // Initialize necessary DAOs
            LabReportDAO labReportDAO = new LabReportDAO(DatabaseUtil.getConnection());
            TestRequestDAO testRequestDAO = new TestRequestDAO(DatabaseUtil.getConnection());

            // Retrieve the lab report
            LabReport report = labReportDAO.getLabReportById(reportId);
            if (report == null) {
                JOptionPane.showMessageDialog(this, "Report not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Retrieve the associated test request
            TestRequest testRequest = testRequestDAO.getTestRequestById(report.getTestRequestId());
            if (testRequest == null) {
                JOptionPane.showMessageDialog(this, "Associated test request not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Simulate sending the report
            String message = "Report sent successfully!\n\nReport Details:\n"
                    + "Report ID: " + report.getId()
                    + "\nTest Request ID: " + testRequest.getId()
                    + "\nPatient ID: " + testRequest.getPatientId()
                    + "\nDoctor ID: " + testRequest.getDoctorId();
            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);

            // Update the test request status
            testRequest.setStatus("Sent to Doctor/Patient");
            testRequestDAO.updateTestRequest(testRequest);

            // Refresh the table
            populateReportsTable(null, null);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error sending report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnAssDocActionPerformed

    private void CmbReportStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmbReportStatusActionPerformed
        try {
            String selectedTechnician = (String) CmbTech1.getSelectedItem();
            Integer technicianId = null;
            if (selectedTechnician != null && !"All".equals(selectedTechnician)) {
                technicianId = Integer.parseInt(selectedTechnician.split(" - ")[0]);
            }

            String statusFilter = (String) CmbReportStatus.getSelectedItem();
            statusFilter = "All".equals(statusFilter) ? null : statusFilter;

            populateReportsTable(technicianId, statusFilter); // Pass both arguments
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error applying status filter: " + e.getMessage());
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_CmbReportStatusActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnApplyFilter;
    private javax.swing.JButton BtnAssDoc;
    private javax.swing.JButton BtnAssTech;
    private javax.swing.JButton BtnLogOut1;
    private javax.swing.JButton BtnRefresh;
    private javax.swing.JButton BtnRefresh1;
    private javax.swing.JButton BtnViewDetailedReport;
    private javax.swing.JComboBox<String> CmbFilTech;
    private javax.swing.JComboBox<String> CmbReportStatus;
    private javax.swing.JComboBox<String> CmbStatusMR;
    private javax.swing.JComboBox<String> CmbStatusMS;
    private javax.swing.JComboBox<String> CmbTech;
    private javax.swing.JComboBox<String> CmbTech1;
    private javax.swing.JPanel ManageRequestsPanel;
    private javax.swing.JPanel ReportsPanel;
    private javax.swing.JTable TblReports;
    private javax.swing.JTable TblRequests;
    private javax.swing.JTable TblTech;
    private javax.swing.JPanel TechnicianOverviewPanel;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
