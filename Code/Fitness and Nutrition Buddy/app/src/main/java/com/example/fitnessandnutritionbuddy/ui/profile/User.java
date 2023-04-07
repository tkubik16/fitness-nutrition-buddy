package com.example.fitnessandnutritionbuddy.ui.profile;

public class User {

    public static String username;
    public static String fullname;
    public static String password;
    public static String gender;
    public static int age;
    public static int height;
    public static int weight;
    public static int calories_lte;
    public static int calories_gte;

    public static int protein_lte;
    public static int protein_gte;

    public static int fat_lte;
    public static int fat_gte;

    public static int sugars_lte;
    public static int sugars_gte;

    public static String[] coordinates = new String[2];;

    public User (String username, String password){
        this.username = username;
        this.password = password;
        calories_lte = -1;
        calories_gte = -1;
        protein_lte = -1;
        protein_gte = -1;
        fat_lte = -1;
        fat_gte = -1;
        sugars_lte = -1;
        sugars_gte = -1;
    }

    public static String getUsername(){
        return username;
    }

    public static int getCaloriesGte(){
        return calories_gte;
    }
    public static int getCaloriesLte(){
        return calories_lte;
    }

    public static int getProteinGte(){
        return protein_gte;
    }
    public static int getProteinLte(){
        return protein_lte;
    }

    public static int getFatGte(){
        return fat_gte;
    }
    public static int getFatLte(){
        return fat_lte;
    }

    public static int getSugarsGte(){
        return sugars_gte;
    }
    public static int getSugarsLte(){
        return sugars_lte;
    }

    public static void setCaloriesGte( int calories){ calories_gte = calories; }
    public static void setCaloriesLte( int calories){ calories_lte = calories; }

    public static void setProteinGte( int protein){ protein_gte = protein; }
    public static void setProteinLte( int protein){ protein_lte = protein; }

    public static void setFatGte( int fat){ fat_gte = fat; }
    public static void setFatLte( int fat){ fat_lte = fat; }

    public static void setSugarsGte( int sugars){ sugars_gte = sugars; }
    public static void setSugarsLte( int sugars){ sugars_lte = sugars; }


}
