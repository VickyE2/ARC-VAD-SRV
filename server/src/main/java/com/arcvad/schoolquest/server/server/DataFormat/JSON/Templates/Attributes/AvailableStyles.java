package com.arcvad.schoolquest.server.server.DataFormat.JSON.Templates.Attributes;

import com.arcvad.schoolquest.server.server.DataFormat.JSON.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

public class AvailableStyles extends BaseTemplate implements Mergeable<AvailableStyles> {
    @JsonProperty
    private List<HairStyle> availableHairStyles;
    @JsonProperty
    private List<EyelashStyle> availableEyelashStyles;


    public void setAvailableHairStyles(List<HairStyle> hairStyles) {
        this.availableHairStyles = hairStyles;
    }
    public void setAvailableEyelashStyles(List<EyelashStyle> eyelashStyles) {
        this.availableEyelashStyles = eyelashStyles;
    }

    @JsonIgnore
    public List<HairStyle> getAvailableHairStyles() {
        return this.availableHairStyles;
    }
    @JsonIgnore
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
