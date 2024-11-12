package com.arcvad.schoolquest.server.server.GlobalUtils;

public interface Mergeable<T> {
    /**
     * Merges this object with the provkeyed object.
     *
     * @param other the object to merge with
     * @throws IllegalArgumentException if the other object is null
     */
    void mergeWith(T other);
}
