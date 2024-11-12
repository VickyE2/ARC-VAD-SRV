package com.arcvad.schoolquest.server.server.Templates.Attributes;

import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import com.arcvad.schoolquest.server.server.Templates.BaseTemplate;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@XmlRootElement(namespace = "styles")
public class AvailableStyles extends BaseTemplate implements Mergeable<AvailableStyles> {
    private static final Logger logger = Logger.getLogger(AvailableStyles.class.getName());
    @XmlElement
    private List<HairStyle> availableHairStyles;
    @XmlElement
    private List<EyelashStyle> availableEyelashStyles;


    public void setAvailableHairStyles(List<HairStyle> hairStyles) {
        this.availableHairStyles = hairStyles;
    }
    public void setAvailableEyelashStyles(List<EyelashStyle> eyelashStyles) {
        this.availableEyelashStyles = eyelashStyles;
    }

    @XmlTransient
    public List<HairStyle> getAvailableHairStyles() {
        return this.availableHairStyles;
    }
    @XmlTransient
    public List<EyelashStyle> getAvailableEyelashStyles() {
        return this.availableEyelashStyles;
    }

    @Override
    public void mergeWith(AvailableStyles other) {
        if (other == null) {
            logger.log(Level.WARNING, "Attempted to merge with a null object.");
            throw new IllegalArgumentException("Cannot merge with a null object.");
        }

        if (other.getAvailableHairStyles() == null || other.getAvailableHairStyles().isEmpty()) {
            logger.info("No hair styles to merge from the provkeyed object.");
            return;
        }

        if (other.getAvailableEyelashStyles() == null || other.getAvailableEyelashStyles().isEmpty()) {
            logger.info("No new eyelash styles to merge from the provkeyed object.");
            return;
        }

        int initialSize = availableHairStyles.size();
        this.availableHairStyles.addAll(other.getAvailableHairStyles());
        int addedUsersCount = availableHairStyles.size() - initialSize;

        if (addedUsersCount > 0) {
            logger.info("Merged {" + addedUsersCount + "} users from the provkeyed object.");
        } else {
            logger.info("No new users were added; the provkeyed user list may have been empty.");
        }
    }
}
