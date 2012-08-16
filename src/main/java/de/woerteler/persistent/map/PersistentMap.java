package de.woerteler.persistent.map;

import java.util.Map;
import java.util.Map.Entry;

import de.woerteler.persistent.PersistentSequence;

/**
 * An interface for an immutable map. All operations that would update a mutable
 * map return a new sequence reflecting the changes instead.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 * @param <K> The key type.
 * @param <V> The value type.
 */
public abstract class PersistentMap<K, V> {

  /** The unique empty map. */
  public static final PersistentMap<?, ?> EMPTY = ImmutableMap.EMPTY_MAP;

  /** The fix size of this persistent map. */
  protected final int size;

  /**
   * Creates a persistent map with the given size.
   * 
   * @param size The number of entries in the map.
   */
  public PersistentMap(final int size) {
    if(size < 0) throw new IllegalArgumentException("size is negative: " + size);
    if(size == 0 && EMPTY != null) throw new IllegalStateException(
        "non unique empty sequence");
    this.size = size;
  }

  /**
   * Inserts the given value into this map.
   * 
   * @param key key to insert
   * @param value value to insert
   * @return updated map if changed, {@code this} otherwise
   */
  public abstract PersistentMap<K, V> put(final K key, final V value);

  /**
   * Gets the value from this map.
   * 
   * @param key key to look for
   * @return bound value if found, the empty sequence {@code ()} otherwise
   */
  public abstract V get(K key);

  /**
   * Number of key/value-pairs contained in this map.
   * 
   * @return size
   */
  public final int size() {
    return size;
  }

  /**
   * Checks if the given key exists in the map.
   * 
   * @param key key to look for
   * @return {@code true()}, if the key exists, {@code false()} otherwise
   */
  public abstract boolean containsKey(K key);

  /**
   * Deletes a key from this map.
   * 
   * @param key key to delete
   * @return updated map if changed, {@code this} otherwise
   */
  public abstract PersistentMap<K, V> remove(K key);

  /**
   * Adds all bindings from the given map into {@code this}.
   * 
   * @param other map to add
   * @return updated map if changed, {@code this} otherwise
   */
  public PersistentMap<K, V> putAll(final PersistentMap<K, V> other) {
    PersistentMap<K, V> res = this;
    for(final PersistentEntry<K, V> e : other.entrySequence()) {
      res = res.put(e.getKey(), e.getValue());
    }
    return res;
  }

  /**
   * Adds all bindings from the given map into {@code this}.
   * 
   * @param other map to add
   * @return updated map if changed, {@code this} otherwise
   */
  public PersistentMap<K, V> putAll(final Map<? extends K, ? extends V> other) {
    PersistentMap<K, V> res = this;
    for(final Entry<? extends K, ? extends V> e : other.entrySet()) {
      res = res.put(e.getKey(), e.getValue());
    }
    return res;
  }

  /**
   * Returns a sequence of all keys in an arbitrary order.
   * 
   * @return A sequence of all keys.
   */
  public abstract PersistentSequence<K> keySequence();

  /**
   * Returns a sequence of all values in an arbitrary order.
   * 
   * @return A sequence of all values.
   */
  public abstract PersistentSequence<V> valueSequence();

  /**
   * Returns a sequence of all entries in an arbitrary order.
   * 
   * @return A sequence of all entries.
   */
  public abstract PersistentSequence<PersistentEntry<K, V>> entrySequence();

  @Override
  public boolean equals(final Object obj) {
    if(obj == this) return true;
    if(!(obj instanceof PersistentMap)) return false;
    @SuppressWarnings("unchecked")
    final PersistentMap<Object, Object> other = (PersistentMap<Object, Object>) obj;
    if(size() != other.size()) return false;
    for(final K key : keySequence()) {
      if(!other.containsKey(key)) return false;
      final V value = get(key);
      final Object o = other.get(key);
      if(value == null) {
        if(o != null) return false;
      } else {
        if(!value.equals(o)) return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    throw new UnsupportedOperationException(
        "find hash code compatible with immutable map");
  }

}
