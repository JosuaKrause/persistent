package de.woerteler.persistent.map;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import de.woerteler.persistent.FlatSequence;
import de.woerteler.persistent.Persistent;
import de.woerteler.persistent.PersistentSequence;

/**
 * A persistent view on a regular map.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 * @param <K> The key type.
 * @param <V> The value type.
 */
public final class PersistentHashMap<K, V> extends AbstractPersistentMap<K, V> {

  /**
   * Creates a persistent view of the given map.
   * 
   * @param <K> The key type.
   * @param <V> The value type.
   * @param map The map.
   * @return a persistent view.
   */
  public static <K, V> PersistentMap<K, V> from(final Map<K, V> map) {
    final HashMap<K, V> m = new HashMap<K, V>(map);
    // to ensure the map was not modified in the meanwhile
    if(m.isEmpty()) return ImmutableMap.empty();
    return new PersistentHashMap<K, V>(m);
  }

  /** A soft reference to the converted map. */
  // maybe use SoftReference here to ensure longer live-span
  private volatile WeakReference<ImmutableMap<K, V>> converted;

  /**
   * Converts this map into a fast modifiable immutable map. The resulting map
   * may be cached via a soft reference.
   * 
   * @return The converted map.
   */
  protected ImmutableMap<K, V> convert() {
    ImmutableMap<K, V> res = null;
    if(converted == null || (res = converted.get()) == null) {
      res = ImmutableMap.from(map);
      converted = new WeakReference<ImmutableMap<K, V>>(res);
    }
    return res;
  }

  /** The baking map. */
  private final Map<K, V> map;

  /**
   * Creates a persistent view of the map.
   * 
   * @param map The map. Will not be copied.
   */
  private PersistentHashMap(final Map<K, V> map) {
    this.map = map;
  }

  @Override
  public PersistentMap<K, V> put(final K key, final V value) {
    return convert().put(key, value);
  }

  @Override
  public V get(final K key) {
    return map.get(key);
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean containsKey(final K key) {
    return map.containsKey(key);
  }

  @Override
  public PersistentMap<K, V> remove(final K key) {
    return convert().remove(key);
  }

  @Override
  public PersistentSequence<K> keySequence() {
    return Persistent.from(map.keySet());
  }

  @Override
  public PersistentSequence<V> valueSequence() {
    return Persistent.from(map.values());
  }

  @Override
  public PersistentSequence<PersistentEntry<K, V>> entrySequence() {
    final Set<Entry<K, V>> set = map.entrySet();
    return new FlatSequence<PersistentEntry<K, V>>() {

      private PersistentEntry<K, V>[] arr;

      @Override
      public PersistentEntry<K, V> get(final int pos) {
        if(arr == null) {
          fillArray();
        }
        return arr[pos];
      }

      private void fillArray() {
        @SuppressWarnings("unchecked")
        final PersistentEntry<K, V>[] tmp =
        (PersistentEntry<K, V>[]) new PersistentEntry<?, ?>[set.size()];
        int i = 0;
        for(final Entry<K, V> e : set) {
          tmp[i++] = new ImmutableMap.PEntry<K, V>(e.getKey(), e.getValue());
        }
        arr = tmp;
      }

      @Override
      public int size() {
        return set.size();
      }

      @Override
      public Iterator<PersistentEntry<K, V>> iterator() {
        if(arr != null) return Arrays.asList(arr).iterator();
        return new Iterator<PersistentEntry<K, V>>() {

          private final Iterator<Entry<K, V>> it = set.iterator();

          @Override
          public boolean hasNext() {
            return it.hasNext();
          }

          @Override
          public PersistentEntry<K, V> next() {
            final Entry<K, V> e = it.next();
            return new ImmutableMap.PEntry<K, V>(e.getKey(), e.getValue());
          }

          @Override
          public void remove() {
            throw new UnsupportedOperationException();
          }

        };
      }

    };
  }

}
