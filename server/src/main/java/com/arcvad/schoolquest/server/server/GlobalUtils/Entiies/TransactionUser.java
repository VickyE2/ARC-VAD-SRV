package com.arcvad.schoolquest.server.server.GlobalUtils.Entiies;

import com.arcvad.schoolquest.server.server.Playerutils.Genders;

public class TransactionUser{
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private String password;
    private Genders gender;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Genders getGender() {
        return gender;
    }

    public void setGender(Genders gender) {
        this.gender = gender;
    }


    public static class TransactionUserBuilder {

        private String username;
        private String email;
        private String firstname;
        private String lastname;
        private String password;
        private Genders gender;

        public TransactionUserBuilder setEmail(String email) {
            this.email = email;
            return this;
        }

        public TransactionUserBuilder setFirstname(String firstname) {
            this.firstname = firstname;
            return this;
        }

        public TransactionUserBuilder setGender(Genders gender) {
            this.gender = gender;
            return this;
        }

        public TransactionUserBuilder setLastname(String lastname) {
            this.lastname = lastname;
            return this;
        }

        public TransactionUserBuilder setPassword(String password) {
            this.password = password;
            return this;
        }

        public TransactionUserBuilder setUsername(String username) {
            this.username = username;
            return this;
        }

        public TransactionUser build() {

            TransactionUser user = new TransactionUser();
            user.setEmail(this.email);
            user.setFirstname(this.firstname);
            user.setGender(this.gender);
            user.setLastname(this.lastname);
            user.setPassword(this.password);
            user.setUsername(this.username);

            return user;
        }
    }
}
