package com.ecom.bookusinggooglesheet;

public class gradelistitems {
    public String Id;
    public String name; //Name


    public gradelistitems(String Id,String name)
    {
        this.Id = Id;
        this.name = name;

    }
    public String getId() {
        return Id;
    }

    public String getName() {
        return name;
    }

}
