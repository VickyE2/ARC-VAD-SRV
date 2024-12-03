package com.arcvad.schoolquest.server.server.DataFormat.Converter.util;

import com.arcvad.schoolquest.server.server.DataFormat.XML.utilities.BaseTemplate;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlTransient;

public class Backup extends BaseTemplate {

    @XmlAttribute
    private String backupId;
    @XmlAttribute
    private String date;
    @XmlAttribute
    private String backupPath;
    @XmlAttribute
    private String backupSize; // in MB, KB, GB...
    @XmlAttribute
    private BackupType backupType;

    // Private constructor to enforce usage of the builder
    private Backup(BackupBuilder builder) {
        this.backupId = builder.backupId;
        this.date = builder.date;
        this.backupPath = builder.backupPath;
        this.backupSize = builder.backupSize;
        this.backupType = builder.backupType;
    }

    public Backup() {}


    // Getters (optional)

    @XmlTransient
    public String getBackupId() {
        return backupId;
    }
    @XmlTransient
    public String getDate() {
        return date;
    }
    @XmlTransient
    public String getBackupPath() {
        return backupPath;
    }
    @XmlTransient
    public String getBackupSize() {
        return backupSize;
    }
    @XmlTransient
    public BackupType getBackupType() {
        return backupType;
    }

    // Builder class
    public static class BackupBuilder {
        private String backupId;
        private String date;
        private String backupPath;
        private String backupSize;
        private BackupType backupType;

        public BackupBuilder setBackupId(String backupId) {
            this.backupId = backupId;
            return this;
        }

        public BackupBuilder setDate(String date) {
            this.date = date;
            return this;
        }

        public BackupBuilder setBackupPath(String backupPath) {
            this.backupPath = backupPath;
            return this;
        }

        public BackupBuilder setBackupSize(long fileSizeInBytes) {
            this.backupSize = convertFileSize(fileSizeInBytes);
            return this;
        }

        public BackupBuilder setBackupType(BackupType backupType) {
            this.backupType = backupType;
            return this;
        }

        public Backup build() {
            if (backupId == null || date == null || backupPath == null || backupSize == null || backupType == null) {
                throw new IllegalStateException("Mandatory fields are missing for Backup object creation.");
            }
            return new Backup(this);
        }

        // Method to convert file size
        private String convertFileSize(long bytes) {
            if (bytes >= 1_073_741_824) { // 1 GB = 1024 * 1024 * 1024 bytes
                return String.format("%.2f GB", bytes / 1_073_741_824.0);
            } else if (bytes >= 1_048_576) { // 1 MB = 1024 * 1024 bytes
                return String.format("%.2f MB", bytes / 1_048_576.0);
            } else if (bytes >= 1024) { // 1 KB = 1024 bytes
                return String.format("%.2f KB", bytes / 1024.0);
            } else {
                return bytes + " Bytes";
            }
        }
    }
}
