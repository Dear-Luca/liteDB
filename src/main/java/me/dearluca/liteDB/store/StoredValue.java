package me.dearluca.liteDB.store;

/**
 * Represents a stored value in the key-value store, along with its associated timestamp for conflict resolution.
 * @param value the value being stored
 * @param timestamp the timestamp of the operation that created or updated this value
 */
public record StoredValue(String value, long timestamp) {}
