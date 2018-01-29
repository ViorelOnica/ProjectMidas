package com.example.sunquest.midas_project;

/**
 * Created by SunQuest on 12/27/2017.
 */

public class InfoCompany {
    String company_name;
    String username_author;
    String company_id;
    String grade;
    String revenue;
    String currency;
    String employees_number;
    String foundation_date;
    String description;
    String current_user_stars_number;
    String reviews_number;
    public InfoCompany(String company_name, String username_author, String company_id, String grade, String reviews_number){
        this.company_name=company_name;
        this.username_author=username_author;
        this.company_id=company_id;
        this.grade=grade;
        this.reviews_number=reviews_number;
    }
    public InfoCompany(){
        this.company_name="";
        this.revenue="";
        this.currency="";
        this.employees_number="";
        this.foundation_date="";
        this.description="";
        this.username_author="";
        this.grade="";
        this.current_user_stars_number="";
    }
}
