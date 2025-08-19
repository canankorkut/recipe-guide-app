package org.recipeguide.recipeguideapp;

public class Recipe {
    private int recipe_id;
    private String name;
    private String imageUrl;
    private int preparationTime;
    private double totalCost;
    private String category;
    private String instructions;
    private double missingCost;
    private double matchPercentage;

    public Recipe(int recipe_id, String name, String imageUrl, int preparationTime, double totalCost, String category, String instructions) {
        this.recipe_id = recipe_id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.preparationTime = preparationTime;
        this.totalCost = totalCost;
        this.category = category;
        this.instructions = instructions;
    }

    public Recipe() {
    }

    public int getId() {
        return recipe_id;
    }

    public void setId(int recipe_id) {
        this.recipe_id = recipe_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public double getMissingCost() {
        return missingCost;
    }

    public void setMissingCost(double missingCost) {
        this.missingCost = missingCost;
    }

    public double getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }


    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", preparationTime=" + preparationTime +
                ", totalCost=" + totalCost +
                ", category='" + category + '\'' +
                ", instructions='" + instructions + '\'' +
                '}';
    }

}
