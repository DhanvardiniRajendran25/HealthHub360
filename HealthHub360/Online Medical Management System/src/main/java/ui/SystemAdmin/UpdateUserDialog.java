/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/AWTForms/Dialog.java to edit this template
 */
package ui.SystemAdmin;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;
import modelDAO.UserDAO;
import ui.DatabaseUtil;

/**
 *
 * @author AnjanaSruthiR
 */
public class UpdateUserDialog extends java.awt.Dialog {

    private static final Logger LOGGER = Logger.getLogger(UpdateUserDialog.class.getName());
    private final UserDAO userDAO;
    private final int userId;
    private boolean success;

    /**
     * Creates new form UpdateUserDailog
     */
    public UpdateUserDialog(Frame parent, boolean modal, UserDAO userDAO, int userId, String name, String contact, String email, String address, String roleName) {
        super(parent, modal);
        this.userDAO = userDAO;
        this.userId = userId;
        initComponents();

        TxtName.setText(name);
        TxtContact.setText(contact);
        TxtEmail.setText(email);
        TxtAdd.setText(address);

        // If you want to fix the role to Enterprise Admin:
        CmbRole.setModel(new DefaultComboBoxModel<>(new String[]{"Enterprise Admin"}));
        CmbRole.setEnabled(false);

        // Make email field non-editable
        TxtEmail.setEditable(false);
    }

    public boolean isSuccess() {
        return success;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TxtContact = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        LblAddUser = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        TxtEmail = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        TxtAdd = new javax.swing.JTextField();
        TxtName = new javax.swing.JTextField();
        CmbRole = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        Update = new javax.swing.JButton();
        Cancel = new javax.swing.JButton();

        setBackground(new java.awt.Color(247, 249, 252));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jLabel3.setText("Email");
        jLabel3.setToolTipText("");

        LblAddUser.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        LblAddUser.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LblAddUser.setText("Update Enterprise Admin");
        LblAddUser.setToolTipText("");

        jLabel6.setText("Address");
        jLabel6.setToolTipText("");

        TxtEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TxtEmailActionPerformed(evt);
            }
        });

        jLabel1.setText("Name");
        jLabel1.setToolTipText("");

        CmbRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Doctor", "Patient", "Lab Manager", "Lab Technician", "Pharmacy Manager", "Pharmasist", "Insurance Manager", "Billing Manager" }));

        jLabel2.setText("Contact");
        jLabel2.setToolTipText("");

        jLabel7.setText("Role");

        Update.setText("Update");
        Update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                UpdateActionPerformed(evt);
            }
        });

        Cancel.setText("Cancel");
        Cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(151, 151, 151)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(149, 149, 149)
                        .addComponent(LblAddUser, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(322, 322, 322))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(198, 198, 198)
                                .addComponent(Update)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(Cancel))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                            .addComponent(jLabel3)
                                            .addGap(27, 27, 27)
                                            .addComponent(TxtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(jLabel1)
                                                .addComponent(jLabel2))
                                            .addGap(27, 27, 27)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(TxtContact, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(TxtName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(27, 27, 27)
                                        .addComponent(TxtAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7)))
                        .addGap(27, 27, 27)
                        .addComponent(CmbRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(183, 183, 183))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addComponent(LblAddUser, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(71, 71, 71)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(TxtName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CmbRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(TxtContact, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(TxtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(TxtAdd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(69, 69, 69)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Update)
                    .addComponent(Cancel))
                .addContainerGap(145, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void TxtEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_TxtEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TxtEmailActionPerformed

    private void CancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CancelActionPerformed
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel?", "Cancel", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
        }        // TODO add your handling code here:
    }//GEN-LAST:event_CancelActionPerformed

    private void UpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_UpdateActionPerformed
        try {
            String name = TxtName.getText().trim();
            String contact = TxtContact.getText().trim();
            String email = TxtEmail.getText().trim();
            String address = TxtAdd.getText().trim();
            String roleName = (String) CmbRole.getSelectedItem();

            if (name.isEmpty() || contact.isEmpty() || email.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                JOptionPane.showMessageDialog(this, "Invalid email format!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!contact.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "Invalid contact number. It should be 10 digits!", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int roleId = getRoleId(roleName);
            if (roleId == -1) {
                JOptionPane.showMessageDialog(this, "Invalid role selected!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean updated = userDAO.updateUserWithRole(userId, name, contact, email, address, roleId);
            if (updated) {
                JOptionPane.showMessageDialog(this, "Enterprise Admin updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                success = true;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update Enterprise Admin.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            LOGGER.severe("Database error: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_UpdateActionPerformed

    /**
     * @param args the command line arguments
     */
public static void main(String args[]) {
    java.awt.EventQueue.invokeLater(() -> {
        try {
            Connection connection = DatabaseUtil.getConnection();
            UserDAO userDAO = new UserDAO(connection);

            SystemAdminDashboard dashboard = new SystemAdminDashboard(connection);

            JTable adminTable = dashboard.getTblEnterpriseAdmins();

            if (adminTable.getRowCount() == 0) {
                System.out.println("No enterprise admins found.");
                return;
            }

            adminTable.setRowSelectionInterval(0, 0);
            int selectedRow = adminTable.getSelectedRow();
            if (selectedRow == -1) {
                System.out.println("No admin selected.");
                return;
            }

            int userId = (int) adminTable.getValueAt(selectedRow, 0);       // ID
            String name = (String) adminTable.getValueAt(selectedRow, 1);   // Name
            String roleName = (String) adminTable.getValueAt(selectedRow, 2); // Role
            String email = (String) adminTable.getValueAt(selectedRow, 3);  // Email
            String contact = (String) adminTable.getValueAt(selectedRow, 4);// Contact
            String address = (String) adminTable.getValueAt(selectedRow, 5);// Address

            UpdateUserDialog dialog = new UpdateUserDialog(
                    new Frame(),
                    true,
                    userDAO,
                    userId,
                    name,
                    contact,
                    email,
                    address,
                    roleName
            );

            dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    System.exit(0);
                }
            });

            dialog.setVisible(true);

            // Check if update was successful
            if (dialog.isSuccess()) {
                System.out.println("Enterprise Admin updated successfully!");
            } else {
                System.out.println("Update canceled or failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        }
    });
}


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Cancel;
    private javax.swing.JComboBox<String> CmbRole;
    private javax.swing.JLabel LblAddUser;
    private javax.swing.JTextField TxtAdd;
    private javax.swing.JTextField TxtContact;
    private javax.swing.JTextField TxtEmail;
    private javax.swing.JTextField TxtName;
    private javax.swing.JButton Update;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    // End of variables declaration//GEN-END:variables

    private int getRoleId(String roleName) {
        if ("Enterprise Admin".equals(roleName)) {
            return 2;
        }
        return -1;
    }
}
