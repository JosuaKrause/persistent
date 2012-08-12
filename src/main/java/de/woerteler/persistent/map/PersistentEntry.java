package de.woerteler.persistent.map;

/**
 * A key value pair from a persistent map.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 * @param <K> The key type.
 * @param <V> The value type.
 */
public final class PersistentEntry<K, V> {

  /** The key. */
  private final K key;
  /** The value. */
  private final V value;

  /**
   * An entry.
   * 
   * @param key The key.
   * @param value The value.
   */
  public PersistentEntry(final K key, final V value) {
    this.key = key;
    this.value = value;
  }

  /**
   * Getter.
   * @return The key.
   */
  public K getKey() {
    return key;
  }

  /**
   * Getter.
   * @return The value.
   */
  public V getValue() {
    return value;
  }

  @Override
  public boolean equals(final Object obj) {
    if(obj == this) return true;
    if(!(obj instanceof PersistentEntry)) return false;
    final PersistentEntry<?, ?> e = (PersistentEntry<?, ?>) obj;
    return TrieNode.equal(key, e.getKey()) && TrieNode.equal(value, e.getValue());
  }

  @Override
  public int hashCode() {
    return (key == null ? 1 : key.hashCode()) * 31
        + (value == null ? 1 : value.hashCode());
  }

}