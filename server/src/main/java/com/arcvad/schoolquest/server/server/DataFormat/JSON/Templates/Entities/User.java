package com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities;


import com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.Playerutils.Genders;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class User extends BaseTemplate {
    @JsonProperty("email")
    private String keyentifierEmail;
    @JsonProperty("firstname")
    private String keyentifierFirstname;
    @JsonProperty("lastname")
    private String keyentifierLastname;
    @JsonProperty("password")
    private String keyentifierPassword;
    @JsonProperty("username")
    private String keyentifier;
    @JsonProperty("gender")
    private Genders keyentifierGender;



    public void setUsername(String keyentifier){
        this.keyentifier = keyentifier;
    }

    public void setEmail(String keyentifierEmail){
        this.keyentifierEmail = keyentifierEmail;
    }

    public void setPassword(String keyentifyingPassword){
        this.keyentifierPassword = keyentifyingPassword;
    }

    public void setFirstname(String keyentifierFirstname){
        this.keyentifierFirstname = keyentifierFirstname;
    }

    public void setLastname(String keyentifierLastname){
        this.keyentifierLastname = keyentifierLastname;
    }

    public void setGender(Genders keyentifierGender){
        this.keyentifierGender = keyentifierGender;
    }

    @JsonIgnore
    public String getUsername(){
        return this.keyentifier;
    }
    @JsonIgnore
    public String getPassword(){
        return this.keyentifierPassword;
    }
    @JsonIgnore
    public String getLastname(){
        return this.keyentifierLastname;
    }
    @JsonIgnore
    public String getFirstname(){
        return this.keyentifierFirstname;
    }
    @JsonIgnore
    public String getEmail(){
        return this.keyentifierEmail;
    }
    @JsonIgnore
    public Genders getGender(){
        return this.keyentifierGender;
    }
}
