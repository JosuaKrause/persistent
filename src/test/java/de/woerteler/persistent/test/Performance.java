package de.woerteler.persistent.test;

import java.util.Iterator;

import de.woerteler.persistent.Persistent;
import de.woerteler.persistent.PersistentSequence;
import de.woerteler.persistent.TrieSequence;

/**
 * Makes a comparison performance test to check benefits from alternate
 * implementations of the interfaces.
 * 
 * @author Joschi <josua.krause@googlemail.com>
 */
public final class Performance {

  /** No constructor. */
  private Performance() {
    // no constructor
  }

  /** Whether results are printed. */
  private static boolean dryRun;

  /**
   * Runs the performance tests.
   * 
   * @param args No arguments.
   */
  public static void main(final String[] args) {
    dryRun = true;
    for(int i = 0; i < 1; ++i) {
      System.out.println("dry run");
      testSequence();
    }
    dryRun = false;
    testSequence();
  }

  /** The test sequence. */
  private static void testSequence() {
    insertTestWithInitialContent(0, 10000);
    insertTestWithInitialContent(1, 10000);
    insertTestWithInitialContent(10, 10000);
    insertTestWithInitialContent(100, 10000);
    insertTestWithInitialContent(1000, 10000);
    insertTestWithInitialContent(1000000, 10000);
    buildingUp(0);
    buildingUp(1);
    buildingUp(10);
    buildingUp(1000);
    buildingUp(1000000);
    branchedInsert(10000, 10000, 1);
    branchedInsert(10000, 10000, 10);
    branchedInsert(10000, 10000, 100);
    branchedInsert(10000, 10000, 10000);
    reading(1, 1000000);
    reading(10, 1000000);
    reading(1000, 1000000);
    reading(1000000, 1000000);
    iterate(0, 1000);
    iterate(1, 1000);
    iterate(10, 1000);
    iterate(1000, 1000);
    iterate(1000000, 1000);
    iterate(10000000, 50);
  }

  /**
   * Building up data structures and then reading from them.
   * 
   * @param contentSize The size.
   * @param reads The number of reads.
   */
  private static void reading(final int contentSize, final int reads) {
    final Object[] content = new Object[contentSize];
    for(int i = 0; i < content.length; ++i) {
      content[i] = new Object();
    }
    final long test0 = startTest();
    reads(Persistent.from(content), reads);
    final double time0 = endTest(test0);
    printResult("reads" + contentSize, "array", time0);
    final long test1 = startTest();
    reads(TrieSequence.from(content), reads);
    final double time1 = endTest(test1);
    printResult("reads" + contentSize, "trie ", time1);
  }

  /**
   * Building up data structures test.
   * 
   * @param contentSize The size.
   */
  private static void buildingUp(final int contentSize) {
    final Object[] content = new Object[contentSize];
    for(int i = 0; i < content.length; ++i) {
      content[i] = new Object();
    }
    final long test0 = startTest();
    Persistent.from(content);
    final double time0 = endTest(test0);
    printResult("build" + contentSize, "array", time0);
    final long test1 = startTest();
    TrieSequence.from(content);
    final double time1 = endTest(test1);
    printResult("build" + contentSize, "trie ", time1);
  }

  /**
   * Inserts objects into the data structure with initial content.
   * 
   * @param contentSize The initial size.
   * @param insertSize The number of items to insert.
   */
  private static void insertTestWithInitialContent(final int contentSize,
      final int insertSize) {
    final Object[] content = new Object[contentSize];
    for(int i = 0; i < content.length; ++i) {
      content[i] = new Object();
    }
    final long test0 = startTest();
    final PersistentSequence<Object> seq0 = Persistent.from(content);
    adds(seq0, insertSize);
    final double time0 = endTest(test0);
    printResult("itwic" + contentSize, "array", time0);
    final long test1 = startTest();
    final PersistentSequence<Object> seq1 = TrieSequence.from(content);
    adds(seq1, insertSize);
    final double time1 = endTest(test1);
    printResult("itwic" + contentSize, "trie ", time1);
  }

  /**
   * Tests iteration over the sequence.
   * 
   * @param contentSize The size.
   * @param numIt The number of full iterations.
   */
  private static void iterate(final int contentSize,
      final int numIt) {
    final Object[] content = new Object[contentSize];
    for(int i = 0; i < content.length; ++i) {
      content[i] = new Object();
    }
    final long test0 = startTest();
    final PersistentSequence<Object> seq0 = Persistent.from(content);
    for(int i = 0; i < numIt; ++i) {
      final Iterator<Object> it = seq0.iterator();
      while(it.hasNext()) {
        final Object o = it.next();
        if(o == null) throw new NullPointerException();
      }
    }
    final double time0 = endTest(test0);
    printResult("iter " + contentSize, "array", time0);
    final long test1 = startTest();
    final PersistentSequence<Object> seq1 = TrieSequence.from(content);
    for(int i = 0; i < numIt; ++i) {
      final Iterator<Object> it = seq1.iterator();
      while(it.hasNext()) {
        final Object o = it.next();
        if(o == null) throw new NullPointerException();
      }
    }
    final double time1 = endTest(test1);
    printResult("iter " + contentSize, "trie ", time1);
  }

  /**
   * Inserts multiple times starting at the same data structure.
   * 
   * @param contentSize The initial size.
   * @param insertSize The number of items.
   * @param branches The number of branches.
   */
  private static void branchedInsert(final int contentSize, final int insertSize,
      final int branches) {
    final Object[] content = new Object[contentSize];
    for(int i = 0; i < content.length; ++i) {
      content[i] = new Object();
    }
    final long test0 = startTest();
    final PersistentSequence<Object> seq0 = Persistent.from(content);
    for(int b = 0; b < branches; ++b) {
      adds(seq0, insertSize);
    }
    final double time0 = endTest(test0);
    printResult("branc" + branches, "array", time0);
    final long test1 = startTest();
    final PersistentSequence<Object> seq1 = TrieSequence.from(content);
    for(int b = 0; b < branches; ++b) {
      adds(seq1, insertSize);
    }
    final double time1 = endTest(test1);
    printResult("branc" + branches, "trie ", time1);
  }

  /**
   * Adds the given number of objects into the sequence.
   * 
   * @param seq The sequence.
   * @param count The number.
   */
  private static void adds(final PersistentSequence<Object> seq, final int count) {
    PersistentSequence<Object> s = seq;
    for(int i = 0; i < count; ++i) {
      s = s.add(new Object());
    }
  }

  /**
   * Reads multiple times from the data structure.
   * 
   * @param seq The sequence.
   * @param count The number of reads.
   */
  private static void reads(final PersistentSequence<Object> seq, final int count) {
    for(int i = 0; i < count; ++i) {
      seq.get((int) Math.random() * seq.size());
    }
  }

  /**
   * Starts a test.
   * 
   * @return The test id.
   */
  private static long startTest() {
    return System.nanoTime();
  }

  /**
   * Ends a test.
   * 
   * @param id The test id.
   * @return The time in milli seconds.
   */
  private static double endTest(final long id) {
    return (System.nanoTime() - id) / 1000000.0;
  }

  /**
   * Prints the test result.
   * 
   * @param test The test.
   * @param name The name.
   * @param time The time.
   */
  private static void printResult(final String test, final String name, final double time) {
    if(!dryRun) {
      System.out.println(name + "[" + test + "]: " + time + "ms");
    }
    for(int i = 0; i < 5; ++i) {
      System.gc();
      System.runFinalization();
    }
  }

}
