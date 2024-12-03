package com.arcvad.schoolquest.server.server.DataFormat.SQL.Templates.Entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "player_registrar")
public class PlayerRegistrar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "playerRegistrar", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @Column(name = "created_default", nullable = false)
    private boolean createdDefault = false;

    public void addUser(User user) {
        users.add(user);
        user.setPlayerRegistrar(this); // Ensure proper linking
    }

    public void removeUser(User user) {
        users.remove(user);
        user.setPlayerRegistrar(null); // Break the link
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public boolean isCreatedDefault() {
        return createdDefault;
    }

    public void setCreatedDefault(boolean createdDefault) {
        this.createdDefault = createdDefault;
    }
}
