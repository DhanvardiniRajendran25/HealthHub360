/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.InsuranceEnterprise;

/**
 *
 * @author AnjanaSruthiR
 */

public class InsurancePolicy {
    private int id;
    private String name; // Policy name
    private String description; // Details about coverage
    private double coverageAmount; // Maximum coverage
    private double premium; // Monthly premium

    // Constructor
    public InsurancePolicy(int id, String name, String description, double coverageAmount, double premium) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.coverageAmount = coverageAmount;
        this.premium = premium;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCoverageAmount() {
        return coverageAmount;
    }

    public void setCoverageAmount(double coverageAmount) {
        this.coverageAmount = coverageAmount;
    }

    public double getPremium() {
        return premium;
    }

    public void setPremium(double premium) {
        this.premium = premium;
    }

    @Override
    public String toString() {
        return "InsurancePolicy{id=" + id + ", name='" + name + "', description='" + description +
               "', coverageAmount=" + coverageAmount + ", premium=" + premium + "}";
    }
}
