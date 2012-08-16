package de.woerteler.persistent;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

/**
 * An interface for immutable sequences.
 * All operations that would update a mutable sequence return a new sequence reflecting
 * the changes instead.
 *
 * @author Leo Woerteler
 * @param <E> element type
 */
public abstract class PersistentSequence<E> implements Iterable<E>, RandomAccess {

  /** The empty sequence. */
  public static final PersistentSequence<?> EMPTY = TrieSequence.EMPTY_SEQUENCE;

  /** The fix size of this persistent sequence. */
  private final int size;

  /**
   * Creates a persistent sequence.
   * 
   * @param size The size of the sequence.
   */
  public PersistentSequence(final int size) {
    if(size < 0) throw new IllegalArgumentException("size is negative: " + size);
    if(size == 0 && EMPTY != null) throw new IllegalStateException(
        "non unique empty sequence");
    this.size = size;
  }

  /**
   * Adds an item at the end of the sequence.
   * @param item item to be added
   * @return a sequence which contains {@code item} in addition to the items of this
   *   sequence
   */
  public abstract PersistentSequence<E> add(final E item);

  /**
   * Appends the elements of the given sequence to this sequence.
   * @param sequence sequence to be appended
   * @return a sequence where the items of {@code sequence} are appended to this sequence
   */
  public PersistentSequence<E> append(final PersistentSequence<? extends E> sequence) {
    PersistentSequence<E> seq = this;
    for(final E elem : sequence) {
      seq = seq.add(elem);
    }
    return seq;
  }

  /**
   * Returns the first index of the given element if it exists in the sequence.
   * 
   * @param elem The element.
   * @return The first index of the element or <code>-1</code> if the element is
   *         not contained.
   */
  public int indexOf(final E elem) {
    int pos = 0;
    for(final E e : this) {
      if(e == null) {
        if(elem == null) return pos;
      } else {
        if(e.equals(elem)) return pos;
      }
      ++pos;
    }
    return -1;
  }

  /**
   * Whether the sequence contains the given element.
   * 
   * @param elem The element.
   * @return Whether the sequence contains it.
   */
  public boolean contains(final E elem) {
    return indexOf(elem) != -1;
  }

  /**
   * Gets the element at the given position in the sequence.
   * @param pos position of the element
   * @return element at the given position
   * @throws NoSuchElementException is the given position is not in the range of indices
   *   of this sequence
   */
  public abstract E get(final int pos);

  /**
   * Number of elements of this sequence.
   * @return number of elements
   */
  public final int size() {
    return size;
  }

  @Override
  public Iterator<E> iterator() {
    return new Iterator<E>() {

      private int pos;

      @Override
      public boolean hasNext() {
        return pos < size();
      }

      @Override
      public E next() {
        if(pos >= size()) throw new NoSuchElementException();
        return get(pos++);
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }

    };
  }

  /**
   * Returns an array containing all of the elements in this sequence in proper
   * order (from first to last element).
   * @return array containing all elements
   */
  public abstract Object[] toArray();

  /**
   * Returns an array containing all of the elements in this sequence in
   * proper order (from first to last element); the runtime type of the returned array is
   * that of the specified array. If the sequence fits in the specified array, it is
   * returned therein.  Otherwise, a new array is allocated with the runtime type of the
   * specified array and the size of this sequence.
   * @param array array to copy the elements in or to determine the type of the output
   * @return array containing the elements of this sequence
   */
  public abstract E[] toArray(final E[] array);

  @Override
  public boolean equals(final Object obj) {
    if(obj == this) return true;
    if(!(obj instanceof PersistentSequence)) return false;
    final PersistentSequence<?> other = (PersistentSequence<?>) obj;
    if(size() != other.size()) return false;

    final Iterator<?> mine = iterator(), theirs = other.iterator();
    while(mine.hasNext()) {
      final Object a = mine.next(), b = theirs.next();
      if(a == null ? b != null : !a.equals(b)) return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    // the hash code is not cached because to objects may change their hash code
    int hash = 1;
    for(final E val : this) {
      hash = 31 * hash + (val == null ? 0 : val.hashCode());
    }
    return hash;
  }

}
