package com.arcvad.schoolquest.server.server.DataFormat.Converter.util;

import com.arcvad.schoolquest.server.server.DataFormat.XML.utilities.BaseTemplate;
import com.arcvad.schoolquest.server.server.GlobalUtils.Mergeable;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import static com.arcvad.schoolquest.server.server.GlobalUtils.GlobalUtilities.logger;

@XmlRootElement(namespace = "backup_manager")
public class BackupManager extends BaseTemplate implements Mergeable<BackupManager> {

    @XmlElementWrapper(name = "backups")
    @XmlElement
    private List<Backup> backup;

    @XmlTransient
    public List<Backup> getBackup(){
        return this.backup;
    }

    public void setBackup(List<Backup> backups){
        this.backup = backups;
    }

    public BackupManager() {}

    @Override
    public void mergeWith(BackupManager other) {
        mergeWith(other, false);
    }

    public void mergeWith(BackupManager other, boolean deleteFromOther) {
        if (other == null || other.getBackup() == null) return;

        // Lock to ensure thread safety if needed
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
            List<Backup> otherBackups = other.getBackup();

            // Merge backups while avoiding duplicates
            for (Backup backup : otherBackups) {
                if (!containsBackup(backup)) {
                    this.backup.add(backup);
                }
            }

            // Optionally remove backups from the other BackupManager
            if (deleteFromOther) {
                otherBackups.clear();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Checks if a backup with the same backupId exists in the current list.
     *
     * @param backup The backup to check.
     * @return true if the backup already exists; false otherwise.
     */
    private boolean containsBackup(Backup backup) {
        return this.backup.stream().anyMatch(existing -> existing.getBackupId().equals(backup.getBackupId()));
    }

    /**
     * Removes a backup by its ID from the current BackupManager.
     *
     * @param backupId The ID of the backup to remove.
     * @return true if a backup was removed; false otherwise.
     */
    public boolean removeBackupById(String backupId) {
        return this.backup.removeIf(backup -> backup.getBackupId().equals(backupId));
    }

    /**
     * Removes backups that match a custom condition.
     *
     * @param condition A condition (predicate) to test for each backup.
     * @return true if one or more backups were removed; false otherwise.
     */
    public boolean removeBackupsIf(Consumer<Backup> condition) {
        return this.backup.removeIf(backup -> {
            try {
                condition.accept(backup);
                return true;
            } catch (Exception e) {
                logger.error("ARC-MERGE", "Failed to process backup: " + backup.getBackupId());
                return false;
            }
        });
    }
}
