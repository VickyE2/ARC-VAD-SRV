package com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Entities;

import com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities.BaseTemplate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MinimalUser extends BaseTemplate {

    @JsonIgnore
    private User user;

    @JsonProperty
    private String username;
    @JsonProperty
    private String firstname;
    @JsonProperty
    private String lastname;

    public MinimalUser(){}

    public MinimalUser(User user){
        this.username = user.getUsername();
        this.lastname = user.getLastname();
        this.firstname = user.getFirstname();
    }
    @JsonIgnore
    public String getFirstname() {
        return firstname;
    }
    @JsonIgnore
    public String getLastname(){
        return lastname;
    }
    @JsonIgnore
    public String getUsername(){
        return username;
    }
}
