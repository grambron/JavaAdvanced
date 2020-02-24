//package ru.ifmo.badikova.arrayset;
//
//import java.util.*;
//
//public class HardArraySet<E> extends ArraySet<E> implements NavigableSet<E> {
//
//    private final List<E> elements;
//    private final Comparator<? super E> comparator;
//
//    public HardArraySet(List<E> elements, Comparator<? super E> comparator) {
//        this.elements = elements;
//        this.comparator = comparator;
//    }
//
//    public HardArraySet() {
//        this(Collections.emptyList(), null);
//    }
//
//    public HardArraySet(Collection<? extends E> elements, Comparator<? super E> comparator) {
//        TreeSet<E> data = new TreeSet<>(comparator);
//        data.addAll(elements);
//        this.elements = new ArrayList<>(data);
//        this.comparator = comparator;
//    }
//
//    public HardArraySet(Comparator <? super E> comparator) {
//        this.comparator = comparator;
//        this.elements = new ArrayList<>();
//    }
//
//    public HardArraySet(Collection<? extends E> elements) {
//        comparator = null;
//        this.elements = new ArrayList<>(new TreeSet<>(elements));
//    }
//
//    private E getElement(int index) {
//        return (0 <= index && index < size()) ? elements.get(index) : null;
//    }
//
//    private int findIndex(E element, boolean inclusive, boolean findHigherIndex) {
//        int index = Collections.binarySearch(elements, element, comparator);
//        if (index < 0) {
//            return findHigherIndex ? -index - 1 : -index - 2;
//        }
//        if (!inclusive) {
//            return findHigherIndex ? index + 1 : index - 1;
//        }
//        return index;
//    }
//
//    @Override
//    public E lower(E e) {
//        return getElement(findIndex(e, false, false));
//    }
//
//    @Override
//    public E floor(E e) {
//        return getElement(findIndex(e, true, false));
//    }
//
//    @Override
//    public E ceiling(E e) {
//        return getElement(findIndex(e, false, true));
//    }
//
//    @Override
//    public E higher(E e) {
//        return getElement(findIndex(e, true, true));
//    }
//
//    @Override
//    public E pollFirst() {
//        throw new UnsupportedOperationException("ArraySet cannot be modified");
//    }
//
//    @Override
//    public E pollLast() {
//        throw new UnsupportedOperationException("ArraySet cannot be modified");
//    }
//
//    @Override
//    public NavigableSet<E> descendingSet() {
//        return null;
//    }
//
//    @Override
//    public Iterator<E> descendingIterator() {
//        return null;
//    }
//
//    private SortedSet<E> subSet(E fromElement, E toElement, boolean inclusive) {
//        if (fromElement == null || toElement == null) {
//            throw new IllegalArgumentException("Not null arguments expected");
//        }
//        int from = findIndex(fromElement, inclusive, true);
//        int to = findIndex(toElement, );
//
//        if (inclusive) {
//            to++;
//        }
//
//        if (from > to) {
//            throw new IllegalArgumentException("Start index is greater than end index");
//        }
//        return new ArraySet<>(elements.subList(from, to), comparator);
//    }
//
//    @Override
//    public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive) {
//        return null;
//    }
//
//    @Override
//    public NavigableSet<E> headSet(E toElement, boolean inclusive) {
//        return null;
//    }
//
//    @Override
//    public NavigableSet<E> tailSet(E fromElement, boolean inclusive) {
//        return null;
//    }
//
//    @Override
//    public int size() {
//        return elements.size();
//    }
//}
