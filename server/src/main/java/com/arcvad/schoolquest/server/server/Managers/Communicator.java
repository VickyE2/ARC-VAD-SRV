package com.arcvad.schoolquest.server.server.Managers;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

public class Communicator {

    // Base message class with a type field to identify the type
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = UserRegister.class, name = "UserRegister"),
        @JsonSubTypes.Type(value = UserGetter.class, name = "UserUpdate")
    })
    public abstract class BaseMessage {
        private String type;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    class UserRegister extends BaseMessage {
        private String username;
        private String password;
        private String email;
        private String gender;
        private String firstname;
        private String lastname;

        public void setUsername(String username){
            this.username = username;
        }
        public void setPassword(String password){
            this.password = password;
        }
        public void setEmail(String email){
            this.email = email;
        }
        public void setGender(String gender){
            this.gender = gender;
        }
        public void setFirstname(String firstname){
            this.firstname = firstname;
        }
        public void setLastname(String lastname){
            this.lastname = lastname;
        }


        public String setUsername(){
            return this.username;
        }
        public String setPassword(){
            return this.password;
        }
        public String setEmail(){
            return this.email;
        }
        public String setGender(){
            return this.gender;
        }
        public String setFirstname(){
            return this.firstname;
        }
        public String setLastname(){
            return this.lastname;
        }
    }

    class UserGetter extends BaseMessage {
        private String username;
        private String top_cloth;
        private List<String> top_cloth_collection;
        private String bottom_cloth;
        private List<String> bottom_cloth_collection;
        private String eye_color;
        private String eye_lash_style;
        private String hair_color;
        private String hair_style;
        private String shoe;
        private List<String> shoe_collection;


        // Getter and Setter for username
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        // Getter and Setter for top_cloth
        public String getTop_cloth() {
            return top_cloth;
        }

        public void setTop_cloth(String top_cloth) {
            this.top_cloth = top_cloth;
        }

        // Getter and Setter for top_cloth_collection
        public List<String> getTop_cloth_collection() {
            return top_cloth_collection;
        }

        public void setTop_cloth_collection(List<String> top_cloth_collection) {
            this.top_cloth_collection = top_cloth_collection;
        }

        // Getter and Setter for bottom_cloth
        public String getBottom_cloth() {
            return bottom_cloth;
        }

        public void setBottom_cloth(String bottom_cloth) {
            this.bottom_cloth = bottom_cloth;
        }

        // Getter and Setter for bottom_cloth_collection
        public List<String> getBottom_cloth_collection() {
            return bottom_cloth_collection;
        }

        public void setBottom_cloth_collection(List<String> bottom_cloth_collection) {
            this.bottom_cloth_collection = bottom_cloth_collection;
        }

        // Getter and Setter for eye_color
        public String getEye_color() {
            return eye_color;
        }

        public void setEye_color(String eye_color) {
            this.eye_color = eye_color;
        }

        // Getter and Setter for eye_lash_style
        public String getEye_lash_style() {
            return eye_lash_style;
        }

        public void setEye_lash_style(String eye_lash_style) {
            this.eye_lash_style = eye_lash_style;
        }

        // Getter and Setter for hair_color
        public String getHair_color() {
            return hair_color;
        }

        public void setHair_color(String hair_color) {
            this.hair_color = hair_color;
        }

        // Getter and Setter for hair_style
        public String getHair_style() {
            return hair_style;
        }

        public void setHair_style(String hair_style) {
            this.hair_style = hair_style;
        }

        // Getter and Setter for shoe
        public String getShoe() {
            return shoe;
        }

        public void setShoe(String shoe) {
            this.shoe = shoe;
        }

        // Getter and Setter for shoe_collection
        public List<String> getShoe_collection() {
            return shoe_collection;
        }

        public void setShoe_collection(List<String> shoe_collection) {
            this.shoe_collection = shoe_collection;
        }

    }
}
