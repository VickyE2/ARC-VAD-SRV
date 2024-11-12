package com.arcvad.schoolquest.server.server.Templates.Entities;


import com.arcvad.schoolquest.server.server.Playerutils.Genders;
import com.arcvad.schoolquest.server.server.Templates.BaseTemplate;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;


public class User extends BaseTemplate {
    @XmlAttribute(name = "email")
    private String keyentifierEmail;
    @XmlAttribute(name="firstname")
    private String keyentifierFirstname;
    @XmlAttribute(name="lastname")
    private String keyentifierLastname;
    @XmlAttribute(name="password")
    private String keyentifierPassword;
    @XmlAttribute(name="username")
    private String keyentifier;
    @XmlAttribute(name="gender")
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

    @XmlTransient
    public String getUsername(){
        return this.keyentifier;
    }
    @XmlTransient
    public String getPassword(){
        return this.keyentifierPassword;
    }
    @XmlTransient
    public String getLastname(){
        return this.keyentifierLastname;
    }
    @XmlTransient
    public String getFirstname(){
        return this.keyentifierFirstname;
    }
    @XmlTransient
    public String getEmail(){
        return this.keyentifierEmail;
    }
    @XmlTransient
    public Genders getGender(){
        return this.keyentifierGender;
    }
}
