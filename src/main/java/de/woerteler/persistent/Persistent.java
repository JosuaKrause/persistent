package de.woerteler.persistent;

import java.util.Collection;
import java.util.Map;

import de.woerteler.persistent.map.ImmutableMap;
import de.woerteler.persistent.map.PersistentHashMap;
import de.woerteler.persistent.map.PersistentMap;

/**
 * Handles the creation of immutable sequences.
 *
 * @author Joschi <josua.krause@googlemail.com>
 *
 */
public final class Persistent {

  /** No constructor. */
  private Persistent() {
    // no constructor
  }

  /**
   * Returns the empty sequence.
   * 
   * @param <E> The type of the sequence.
   * @return The empty sequence.
   */
  public static <E> PersistentSequence<E> emptySequence() {
    return TrieSequence.empty();
  }

  /**
   * Returns the empty map.
   * 
   * @param <K> The key type.
   * @param <V> The value type.
   * @return The empty map.
   */
  public static <K, V> PersistentMap<K, V> emptyMap() {
    return ImmutableMap.empty();
  }

  /**
   * Creates a persistent sequence from an array.
   * 
   * @param <E> The type of the sequence.
   * @param es The array.
   * @return The sequence.
   */
  public static <E> PersistentSequence<E> from(final E... es) {
    return ArraySequence.from(es);
  }

  /**
   * Creates a persistent sequence from an iterable.
   * 
   * @param <E> The type of the sequence.
   * @param it The iterable.
   * @return The sequence.
   */
  public static <E> PersistentSequence<E> from(final Iterable<E> it) {
    if(it instanceof Collection) return from((Collection<E>) it);
    return TrieSequence.from(it);
  }

  /**
   * Creates a persistent sequence from a collection.
   * 
   * @param <E> The type of sequence.
   * @param c The collection.
   * @return The sequence.
   */
  public static <E> PersistentSequence<E> from(final Collection<E> c) {
    return ArraySequence.from(c);
  }

  /**
   * Creates a persistent map from a regular map.
   * 
   * @param <K> The key type.
   * @param <V> The value type.
   * @param map The regular map.
   * @return The persistent map.
   */
  public static <K, V> PersistentMap<K, V> from(final Map<K, V> map) {
    return PersistentHashMap.from(map);
  }

  /**
   * Creates a singleton persistent map.
   * 
   * @param <K> The key type.
   * @param <V> The value type.
   * @param key The key.
   * @param value The value.
   * @return The singleton map.
   */
  public static <K, V> PersistentMap<K, V> singleton(final K key, final V value) {
    return ImmutableMap.singleton(key, value);
  }

}
