package com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Entities;

import com.arcvad.schoolquest.server.server.DataFormat.XML.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

@XmlRootElement(namespace = "player")
public class PlayerRegistrar extends BaseTemplate implements Mergeable<PlayerRegistrar> {
    @XmlElementWrapper
    @XmlElement(name = "user")
    private List<User> users;
    @XmlElement
    private boolean createdDefault = false;


    public void setUsers(List<User> users){
        this.users = users;
    }
    public void setCreatedDefault(boolean createdDefault){
        this.createdDefault = createdDefault;
    }

    @XmlTransient
    public List<User> getUsers(){
        return this.users;
    }
    @XmlTransient
    public boolean isCreatedDefault(){
        return this.createdDefault;
    }

    public boolean deleteUser(String username) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        // Check if user exists and remove them if found
        Iterator<User> iterator = users.iterator();
        while (iterator.hasNext()) {
            User user = iterator.next();
            if (user.getUsername().equals(username)) {
                iterator.remove();  // Remove the user
                logger.info("ARC-MERGE", StringTemplate.STR."User with username '\{username}' has been deleted.");
                return true;  // Deletion was successful
            }
        }

        logger.warning("ARC-MERGE",StringTemplate.STR."User with username {\{username}} not found for deletion.");
        return false;  // User not found
    }

    public void mergeWith(PlayerRegistrar other) {
        if (other == null) {
            logger.severe("ARC-MERGE", "Cannot merge with null PlayerRegistrar object");
            throw new IllegalArgumentException("Cannot merge with null PlayerRegistrar object");
        }

        // Merging users list, avoiding duplicates based on username
        if (other.users != null) {
            if (this.users == null) {
                this.users = new ArrayList<>(other.users);  // Initialize if null
            } else {
                // Using a set to track existing usernames in this.users
                Set<String> existingUsernames = this.users.stream()
                    .map(User::getUsername)  // Assuming User has a getUsername() method
                    .collect(Collectors.toSet());

                // Add only users from 'other' that are not already in 'this'
                for (User user : other.users) {
                    if (!existingUsernames.contains(user.getUsername())) {
                        this.users.add(user);
                    }
                }
            }
        }

        // Merging createdDefault (we take the value from the other object if true)
        if (other.createdDefault) {
            this.createdDefault = true;  // If other has createdDefault as true, we set it as true
        }

        // Log the merge action
        logger.info("ARC-MERGE","green[Merged PlayerRegistrar data successfully without duplicates.]");
    }

}
