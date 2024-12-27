/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelPharmacyEnterpriseService;
import java.sql.Connection;
import modelPharmacyEnterpriseDAO.PrescriptionAssignmentDAO;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import modelPharmacyEnterprise.Prescription;
import modelPharmacyEnterprise.PrescriptionAssignment;
import ui.DatabaseUtil;

/**
 *
 * @author keerthichandrakanth
 */
public class PrescriptionAssignmentService {
    private PrescriptionAssignmentDAO dao;

    public PrescriptionAssignmentService() {
        this.dao = new PrescriptionAssignmentDAO();
    }

     public void assignPrescription(int prescriptionId, int pharmacistId) throws SQLException {
        Date currentDate = new Date(System.currentTimeMillis());
        dao.createAssignment(prescriptionId, pharmacistId, currentDate);
    }

    public List<String> getAllPharmacistNames() {
        return dao.getAllPharmacistNames();
    }
    
    public List<String> getAllPharmacyManager() {
        return dao.getAllPharmacyManager();
    }
    
    public List<String> getAllPatient() {
        return dao.getAllPatient();
    }
    
    public int getUserIdByNameAndRole(String name, String roleName) throws SQLException {
    return dao.getUserIdByNameAndRole(name, roleName);
   }
   
    public int getPatientIdByUsername(String name) throws SQLException {
    return dao.getPatientIdByUsername(name);
   }
    public int getPharmacistIdByName(String pharmacistName) throws SQLException {
        return dao.fetchPharmacistIdByName(pharmacistName);
    }
    
  public String getAssignedPharmacistName(int prescriptionId) throws SQLException {
    // Call to DAO layer to fetch pharmacist name by prescription ID
    return dao.fetchAssignedPharmacistNameByPrescriptionId(prescriptionId);
}
   
    public int fetchDoctorIdByUserId(int userId) throws SQLException {
        return dao.getDoctorIdByUserId(userId);
    }
  

}
