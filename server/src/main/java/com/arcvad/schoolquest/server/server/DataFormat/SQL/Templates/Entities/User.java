package com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities;

import com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Attributes.Family;
import com.arcvad.schoolquest.server.server.Playerutils.Genders;
import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @NaturalId
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "firstname", nullable = false)
    private String firstname;

    @Column(name = "lastname", nullable = false)
    private String lastname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "family_position", nullable = false, updatable = false)
    public int familyPosition;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Genders gender;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "family_id", nullable = false) // Specify the foreign key column
    private Family family;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST) // Added cascade type
    @JoinColumn(name = "player_registrar_id", nullable = false)
    private PlayerRegistrar playerRegistrar;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id", nullable = false) // Specify the foreign key colum
    private Player player;

    // Getters and Setters
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Genders getGender() {
        return gender;
    }

    public void setGender(Genders gender) {
        this.gender = gender;
    }

    public Family getFamily() {
        return family;
    }
    public void setFamily(Family family) {
        this.family = family;
    }

    public PlayerRegistrar getPlayerRegistrar() {
        return playerRegistrar;
    }

    public void setPlayerRegistrar(PlayerRegistrar playerRegistrar) {
        this.playerRegistrar = playerRegistrar;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getFamilyPosition() {
        return familyPosition;
    }

    public void setFamilyPosition(int familyPosition) {
        this.familyPosition = familyPosition;
    }
}

