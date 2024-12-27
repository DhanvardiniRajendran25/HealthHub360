/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package ui.insuranceManager;

import java.awt.CardLayout;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import model.InsuranceEnterpriseService.InsuranceManagerService;
import ui.DatabaseUtil;
import ui.LoginScreen;

/**
 *
 * @author AnjanaSruthiR
 */
public class insuranceManagerDashboard extends javax.swing.JPanel {

    private InsuranceManagerService insuranceManagerService;
    private int loggedInManagerId;

    /**
     * Creates new form insuranceManagerWorkarea
     */
    public insuranceManagerDashboard(int managerId, Connection connection) {
        this.loggedInManagerId = managerId;
        this.insuranceManagerService = new InsuranceManagerService(connection, managerId);
        initComponents();
        populatePoliciesTable();
        populateClaimsTable();
    }

    public void populatePoliciesTable() {
        try {
            // Fetch all policies instead of filtering by manager ID
            List<Map<String, Object>> policies = insuranceManagerService.getAllPolicies();
            DefaultTableModel model = (DefaultTableModel) TblPolicies.getModel();
            model.setRowCount(0); // Clear existing rows
            for (Map<String, Object> policy : policies) {
                model.addRow(new Object[]{
                    policy.get("PolicyID"),
                    policy.get("PolicyName"),
                    policy.get("CoverageAmount"),
                    policy.get("Premium"),
                    policy.get("Description")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading policies: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void populateClaimsTable() {
        try {
            List<Map<String, Object>> claims = insuranceManagerService.getClaimsByManagerId(loggedInManagerId);
            updateClaimsTable(claims);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading claims.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyFilters() {
        String statusFilter = (String) CmbStatus.getSelectedItem();
        String dateFilter = (String) CmbDate.getSelectedItem();

        try {
            List<Map<String, Object>> filteredClaims = insuranceManagerService.getFilteredClaims(
                    loggedInManagerId, statusFilter, dateFilter);
            updateClaimsTable(filteredClaims);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error applying filters: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateClaimsTable(List<Map<String, Object>> claims) {
        DefaultTableModel model = (DefaultTableModel) TblClaims.getModel();
        model.setRowCount(0); // Clear existing rows

        for (Map<String, Object> claim : claims) {
            model.addRow(new Object[]{
                claim.get("ClaimID"),
                claim.get("PatientName"),
                claim.get("PolicyName"),
                claim.get("ClaimDate"),
                claim.get("ClaimAmount"),
                claim.get("Status"),
                claim.get("Remarks")
            });
        }
    }

    private void handleClaimUpdate(String newStatus) {
        int selectedRow = TblClaims.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a claim to " + newStatus.toLowerCase() + "!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String currentStatus = (String) TblClaims.getValueAt(selectedRow, 5); // Assuming the "Status" column is at index 5
        if ("Approved".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This claim is already approved and cannot be " + newStatus.toLowerCase() + "!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String remarks = TxtRemarks.getText().trim();
        if (remarks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide remarks for the " + newStatus.toLowerCase() + " action!", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int claimId = (int) TblClaims.getValueAt(selectedRow, 0); // Assuming the "ClaimID" column is at index 0

        try {
            boolean success = insuranceManagerService.updateClaimStatus(claimId, newStatus, remarks);
            if (success) {
                JOptionPane.showMessageDialog(this, "Claim " + newStatus.toLowerCase() + " successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                populateClaimsTable(); // Refresh table
            } else {
                JOptionPane.showMessageDialog(this, "Failed to " + newStatus.toLowerCase() + " claim.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TblPolicies = new javax.swing.JTable();
        BtnRefresh = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TblClaims = new javax.swing.JTable();
        CmbStatus = new javax.swing.JComboBox<>();
        CmbDate = new javax.swing.JComboBox<>();
        BtnApprove = new javax.swing.JButton();
        BtnDecline = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        TxtRemarks = new javax.swing.JTextArea();
        LblRemarks = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        BtnLogOut = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        jTabbedPane1.setBackground(new java.awt.Color(192, 213, 245));

        jPanel1.setBackground(new java.awt.Color(247, 249, 252));

        TblPolicies.setBackground(new java.awt.Color(187, 208, 237));
        TblPolicies.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Policy ID", "Name", "Coverage Amount", "Premium", "Description"
            }
        ));
        jScrollPane1.setViewportView(TblPolicies);

        BtnRefresh.setText("Refresh");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BtnRefresh)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 877, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(BtnRefresh)
                .addContainerGap(101, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Manage Policies", jPanel1);

        jPanel2.setBackground(new java.awt.Color(247, 249, 252));

        TblClaims.setBackground(new java.awt.Color(187, 208, 237));
        TblClaims.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Claim ID", "Patient Name", "Policy Name", "Claim Date", "Claim Amount", "Status", "Remarks"
            }
        ));
        jScrollPane2.setViewportView(TblClaims);

        CmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Pending", "Approved", "Rejected" }));
        CmbStatus.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbStatusActionPerformed(evt);
            }
        });

        CmbDate.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Today", "Last 7 days", "Last 30 days" }));
        CmbDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CmbDateActionPerformed(evt);
            }
        });

        BtnApprove.setText("Approve");
        BtnApprove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnApproveActionPerformed(evt);
            }
        });

        BtnDecline.setText("Decline");
        BtnDecline.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnDeclineActionPerformed(evt);
            }
        });

        TxtRemarks.setColumns(20);
        TxtRemarks.setRows(5);
        jScrollPane3.setViewportView(TxtRemarks);

        LblRemarks.setText("Remarks");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(452, 452, 452)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LblRemarks)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BtnApprove, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(BtnDecline, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(CmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(CmbDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 883, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {CmbDate, CmbStatus});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BtnApprove, BtnDecline});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CmbDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                .addComponent(LblRemarks)
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(BtnApprove)
                        .addGap(18, 18, 18)
                        .addComponent(BtnDecline))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(28, 28, 28))
        );

        jTabbedPane1.addTab("Manage Claims", jPanel2);

        jPanel3.setBackground(new java.awt.Color(247, 249, 252));

        BtnLogOut.setBackground(new java.awt.Color(199, 180, 127));
        BtnLogOut.setFont(new java.awt.Font("Helvetica Neue", 3, 13)); // NOI18N
        BtnLogOut.setText("<- Log Out");
        BtnLogOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnLogOutActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 3, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Welcome Insurance Manager");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(BtnLogOut)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(247, 247, 247))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BtnLogOut)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

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
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnLogOutActionPerformed

    private void CmbStatusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmbStatusActionPerformed
        applyFilters();
        // TODO add your handling code here:
    }//GEN-LAST:event_CmbStatusActionPerformed

    private void CmbDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CmbDateActionPerformed
        applyFilters();
        // TODO add your handling code here:
    }//GEN-LAST:event_CmbDateActionPerformed

    private void BtnDeclineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnDeclineActionPerformed
        handleClaimUpdate("Rejected");
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnDeclineActionPerformed

    private void BtnApproveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnApproveActionPerformed
        handleClaimUpdate("Approved");
        // TODO add your handling code here:
    }//GEN-LAST:event_BtnApproveActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtnApprove;
    private javax.swing.JButton BtnDecline;
    private javax.swing.JButton BtnLogOut;
    private javax.swing.JButton BtnRefresh;
    private javax.swing.JComboBox<String> CmbDate;
    private javax.swing.JComboBox<String> CmbStatus;
    private javax.swing.JLabel LblRemarks;
    private javax.swing.JTable TblClaims;
    private javax.swing.JTable TblPolicies;
    private javax.swing.JTextArea TxtRemarks;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}
