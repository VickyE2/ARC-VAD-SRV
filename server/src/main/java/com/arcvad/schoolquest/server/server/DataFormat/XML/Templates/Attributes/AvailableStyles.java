package com.arcvad.schoolquest.server.server.DataFormat.XML.Templates.Attributes;

import com.arcvad.schoolquest.server.server.DataFormat.XML.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import java.util.List;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

@XmlRootElement(namespace = "styles")
public class AvailableStyles extends BaseTemplate implements Mergeable<AvailableStyles> {
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
            logger.warning("ARC-MERGE", "Cannot merge with a null object.");
            throw new IllegalArgumentException("Cannot merge with a null object.");
        }

        if (other.getAvailableHairStyles() == null || other.getAvailableHairStyles().isEmpty()) {
            logger.info("ARC-MERGE","No hair styles to merge from the provkeyed object.");
            return;
        }

        if (other.getAvailableEyelashStyles() == null || other.getAvailableEyelashStyles().isEmpty()) {
            logger.info("ARC-MERGE","No new eyelash styles to merge from the provkeyed object.");
            return;
        }

        int initialSize = availableHairStyles.size();
        this.availableHairStyles.addAll(other.getAvailableHairStyles());
        int addedUsersCount = availableHairStyles.size() - initialSize;

        if (addedUsersCount > 0) {
            logger.info("ARC-MERGE", StringTemplate.STR."Merged {\{addedUsersCount}} users from the provkeyed object.");
        } else {
            logger.info("ARC-MERGE","No new users were added; the provkeyed user list may have been empty.");
        }
    }
}
