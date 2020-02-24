package ru.ifmo.badikova.arrayset;

import java.util.*;

public class ArraySet<E> extends AbstractSet<E> implements SortedSet<E> {

    private final List<E> elements;
    private final Comparator<? super E> comparator;

    public ArraySet(Collection<? extends E> elements, Comparator<? super E> comparator) {
        TreeSet<E> data = new TreeSet<>(comparator);
        data.addAll(elements);
        this.elements = new ArrayList<>(data);
        this.comparator = comparator;
    }

    private ArraySet(List<E> elements, Comparator<? super E> comparator) {
        this.elements = elements;
        this.comparator = comparator;
    }

    public ArraySet() {
        this(Collections.emptyList(), null);
    }

    public ArraySet(Collection<? extends E> elements) {
        this(elements, null);
    }

    public ArraySet(Comparator<? super E> comparator) {
        this(Collections.emptyList(), comparator);
    }

    private void checkIsEmpty() {
        if (elements.isEmpty()) {
            throw new NoSuchElementException();
        }
    }

    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }

    private int findIndex(E element) {
        int index = Collections.binarySearch(elements, element, comparator);
        return index >= 0 ? index : -index - 1;
    }

    private SortedSet<E> subSet(E fromElement, E toElement, boolean inclusive) {
//        if (fromElement == null || toElement == null) {
//            throw new IllegalArgumentException("Not null arguments expected");
//        }
        int from = findIndex(fromElement);
        int to = findIndex(toElement);

        if (inclusive) {
            to++;
        }

        if (from > to) {
            throw new IllegalArgumentException("Start index is greater than end index");
        }
        return new ArraySet<>(elements.subList(from, to), comparator);
    }

    @Override
    @SuppressWarnings("unchecked cast")
    public SortedSet<E> subSet(E fromElement, E toElement) {
        if (((comparator == null) ? ((Comparable<E>)fromElement).compareTo(toElement) : comparator.compare(fromElement, toElement)) > 0) {
            throw new IllegalArgumentException("From element is less than to element");
        }
        return subSet(fromElement, toElement, false);
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return isEmpty() ? new ArraySet<>(comparator) : subSet(first(), toElement, false);
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return isEmpty() ? new ArraySet<>(comparator) : subSet(fromElement, last(), true);
    }

    @Override
    public E first() {
        checkIsEmpty();
        return elements.get(0);
    }

    @Override
    public E last() {
        checkIsEmpty();
        return elements.get(elements.size() - 1);
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public Iterator<E> iterator() {
        return Collections.unmodifiableList(elements).iterator();
    }

    @Override
    @SuppressWarnings("unchecked cast")
    public boolean contains(Object o) {
        return Collections.binarySearch(elements, (E) Objects.requireNonNull(o), comparator) >= 0;
    }
}
