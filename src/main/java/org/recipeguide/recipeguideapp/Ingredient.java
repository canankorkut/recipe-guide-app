package org.recipeguide.recipeguideapp;

public class Ingredient {
    private int ingredientId;
    private String ingredientName;
    private double totalQuantity;
    private String ingredientUnit;
    private double unitPrice;

    public Ingredient(int ingredientId, String ingredientName, double totalQuantity, String ingredientUnit, double unitPrice) {
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.totalQuantity = totalQuantity;
        this.ingredientUnit = ingredientUnit;
        this.unitPrice = unitPrice;
    }

    public Ingredient() {
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public double getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public String getIngredientUnit() {
        return ingredientUnit;
    }

    public void setIngredientUnit(String ingredientUnit) {
        this.ingredientUnit = ingredientUnit;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "ingredientName='" + ingredientName + '\'' +
                ", totalQuantity=" + totalQuantity +
                ", ingredientUnit='" + ingredientUnit + '\'' +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
