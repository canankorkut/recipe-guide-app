# Recipe Guide App
![Image](https://github.com/user-attachments/assets/1255817c-3802-4a71-b203-a16624be14aa)


## 📋 Project Overview
This desktop application allows users to:
- Store and manage recipes
- Track ingredient inventory
- Discover dishes that can be prepared with available ingredients  


## 🚀 Features

### Core Functionality
- **Recipe Management**: Add, update, delete, and view recipes  
- **Ingredient Database**: Manage quantities and pricing of ingredients  
- **Smart Recommendations**:  
  - ✅ Green highlight → all ingredients available  
  - ❌ Red highlight → missing ingredients  
  - 💰 Cost calculation for missing ingredients  
- **Dynamic Search**: Search by recipe name or ingredients  
- **Advanced Filtering**: By category, time, cost, or ingredient count  
- **Ingredient Matching**: Percentage-based recipe compatibility  

### Search & Filter Options
- Recipe name search  
- Ingredient-based discovery  
- Sort by preparation time (asc/desc)  
- Sort by cost (asc/desc)  
- Filter by category, cost range, ingredient count  


## 🛠 Technologies Used
- **Language**: Java  
- **UI Framework**: JavaFX  
- **Database**: PostgreSQL

## 📊 Database Structure

### Tables
1. **Recipes**
   - `recipe_id` (SERIAL PK)  
   - `recipe_name` (VARCHAR)  
   - `category` (VARCHAR)  
   - `preparation_time` (INT)  
   - `instructions` (TEXT)  
   - `image_url` (TEXT)  

2. **Ingredients**
   - `ingredient_id` (SERIAL PK)  
   - `ingredient_name` (VARCHAR)  
   - `total_quantity` (DECIMAL)  
   - `ingredient_unit` (VARCHAR)  
   - `unit_price` (DECIMAL)  

3. **Recipe_Ingredients**
   - `recipe_id` (FK → Recipes)  
   - `ingredient_id` (FK → Ingredients)  
   - `ingredient_quantity` (FLOAT)  
   - **Composite PK** (`recipe_id`, `ingredient_id`)  


## 🔧 Installation & Setup

### Prerequisites
- Java **11+**  
- JavaFX SDK  
- PostgreSQL **12+**  
- PostgreSQL JDBC Driver  

### Database Setup
1. Install PostgreSQL and create a database: `recipe_guide`  
2. Create a PostgreSQL user with privileges  
3. Update database connection settings in the app  

### Running the Application
1. **Clone the repository**
   ```bash
   git clone https://github.com/canankorkut/recipe-guide-app.git
   ```
2. **Navigate to project directory:**
   ```bash
   cd recipe-guide-app
   ```
3. **Ensure PostgreSQL is running and accessible**
4. **Compile and run:**
   ```bash
   javac --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml *.java
   java --module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml MainApplication
   ```
