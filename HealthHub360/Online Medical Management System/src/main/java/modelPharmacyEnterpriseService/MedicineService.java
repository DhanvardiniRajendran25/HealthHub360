/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelPharmacyEnterpriseService;
import modelPharmacyEnterpriseDAO.MedicineDAO;
import java.util.List;
import modelPharmacyEnterprise.Medicine;

/**
 *
 * @author keerthichandrakanth
 */
public class MedicineService {
    
    private final MedicineDAO medicineDAO;

    public MedicineService() {
        this.medicineDAO = new MedicineDAO();
    }

    /**
     * Add a new medicine to the database with validation.
     */
    public void addMedicine(String name, String description, double price, int quantityInStock, int reorderLevel, String supplier, java.sql.Date expiryDate ) {
        if (name == null || name.isEmpty() || price <= 0 || quantityInStock < 0 || reorderLevel < 0 || expiryDate == null) {
            throw new IllegalArgumentException("Invalid medicine details provided.");
        }
        
        Medicine medicine = new Medicine(0, name, description, new java.math.BigDecimal(price), quantityInStock, reorderLevel, supplier,expiryDate );
        medicineDAO.create(medicine);
    }

    /**
     * Get a medicine by ID.
     */
    public Medicine getMedicineById(int id) {
        Medicine medicine = medicineDAO.read(id);
        if (medicine == null) {
            throw new IllegalArgumentException("Medicine with ID " + id + " not found.");
        }
        return medicine;
    }

    /**
     * Update an existing medicine.
     */
    public void updateMedicine(int id, String name, String description, double price, int quantityInStock, int reorderLevel, String supplier, java.sql.Date expiryDate) {
        Medicine existingMedicine = medicineDAO.read(id);
        if (existingMedicine == null) {
            throw new IllegalArgumentException("Medicine with ID " + id + " not found.");
        }
        
        existingMedicine.setName(name);
        existingMedicine.setDescription(description);
        existingMedicine.setPrice(new java.math.BigDecimal(price));
        existingMedicine.setQuantityInStock(quantityInStock);
        existingMedicine.setReorderLevel(reorderLevel);
        existingMedicine.setSupplier(supplier);
        existingMedicine.setExpiryDate(expiryDate);
        
        medicineDAO.update(existingMedicine);
    }

    /**
     * Delete a medicine by ID.
     */
    public void deleteMedicine(int id) {
        Medicine medicine = medicineDAO.read(id);
        if (medicine == null) {
            throw new IllegalArgumentException("Medicine with ID " + id + " not found.");
        }
        medicineDAO.delete(id);
    }

    /**
     * Retrieve a list of all medicines.
     */
    public List<Medicine> getAllMedicines() {
        return medicineDAO.getAll();
    }

    /**
     * Check if a medicine needs to be reordered based on the stock level.
     */
    public boolean needsReorder(int id) {
        Medicine medicine = medicineDAO.read(id);
        if (medicine != null) {
            return medicine.getQuantityInStock() <= medicine.getReorderLevel();
        }
        throw new IllegalArgumentException("Medicine with ID " + id + " not found.");
    }
    
     public List<Object[]> fetchMedicineStockUpdateData(int prescriptionId) {
        return medicineDAO.fetchMedicineStockUpdateData(prescriptionId);
    }

    public boolean updateMedicineStock(int medicineId, int newStock) {
        return medicineDAO.updateMedicineStock(medicineId, newStock);
    }
    
}
