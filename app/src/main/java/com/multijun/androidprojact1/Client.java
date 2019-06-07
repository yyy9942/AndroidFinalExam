package com.multijun.androidprojact1;

import java.io.Serializable;

public class Client implements Serializable {
    private String Id;
    private String Pw;
    private String name;

    public String getId() {
        return Id;
    }

    public String getPw() {
        return Pw;
    }

    public String getName(){
        return name;
    }

    public Client(String Id, String Pw,  String name){
        this.Id = Id;
        this.Pw = Pw;
        this.name=name;
    }
    public Client(String Id, String name){
        this.Id = Id;
        this.name = name;
    }
    public void setName(String name){
        this.name=name;
    }

    public boolean checkId(String Id, String Pw){
        if(this.Id.equals(Id) && this.Pw.equals(Pw))
            return true;
        else
            return false;
    }
}
