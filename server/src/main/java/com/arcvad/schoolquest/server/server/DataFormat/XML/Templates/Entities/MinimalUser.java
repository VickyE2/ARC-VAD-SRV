package com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities;

import com.arcvad.schoolquest.server.server.DataFormat.XML.utilities.BaseTemplate;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;

public class MinimalUser extends BaseTemplate {

    @XmlTransient
    private User user;

    @XmlAttribute
    private String username;
    @XmlAttribute
    private String firstname;
    @XmlAttribute
    private String lastname;

    public MinimalUser(){}

    public MinimalUser(User user){
        this.username = user.getUsername();
        this.lastname = user.getLastname();
        this.firstname = user.getFirstname();
    }
    @XmlTransient
    public String getFirstname() {
        return firstname;
    }
    @XmlTransient
    public String getLastname(){
        return lastname;
    }
    @XmlTransient
    public String getUsername(){
        return username;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
