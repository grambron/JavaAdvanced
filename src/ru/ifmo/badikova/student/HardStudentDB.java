package ru.ifmo.badikova.student;

import info.kgeorgiy.java.advanced.student.Group;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentGroupQuery;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HardStudentDB implements StudentGroupQuery {
    private final Comparator<Student> studentComparatorByName = Comparator
            .comparing(Student::getLastName)
            .thenComparing(Student::getFirstName)
            .thenComparing(Student::getId);

    private Group makeGroup(String name, List<Student> students) {
        return new Group(name, students);
    }

    private Stream<Group> groupStream (Collection<Student> students, Comparator<Student> comparator) {
        return students.stream()
                .sorted(comparator)
                .collect(Collectors.groupingBy(Student::getGroup, HashMap::new, Collectors.toList()))
                .entrySet().stream()
                .map(entry -> makeGroup(entry.getKey(), entry.getValue()));
    }

    private Stream<Group> groupStreamSort (Collection<Student> students, Comparator<Student> comparator) {
        return groupStream(students.stream().sorted(comparator).collect(Collectors.toList()), comparator);
    }

    private <C, T extends Collection<C>> T mapAndCollect(Collection<Student> students, Function<Student, C> mapper, Supplier<T> supplier) {
        return students.stream().map(mapper).collect(Collectors.toCollection(supplier));
    }

    private List<String> getName(List<Student> students, Function<Student, String> f) {
        return mapAndCollect(students, f, ArrayList::new);
    }

    private List<Group> sortAndCollectToListGroup(Stream<Group> stream, Comparator<Group> comparator) {
        return stream.sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public List<Group> getGroupsByName(Collection<Student> students) {
        return sortAndCollectToListGroup(groupStreamSort(students, studentComparatorByName), Comparator.comparing(Group::getName));
    }

    @Override
    public List<Group> getGroupsById(Collection<Student> students) {
        return sortAndCollectToListGroup(groupStreamSort(students, Comparator.comparing(Student::getId)), Comparator.comparing(Group::getName));
    }

    private String getMinGroupName(Stream<Group> stream, Comparator<Group> comparator) {
        return stream.min(comparator)
                .orElse(makeGroup("", Collections.emptyList()))
                .getName();
    }

    private int getGroupSize(Group group) {
        return group.getStudents().size();
    }

    private int getDistinctGroupSize(Group group) {
        return getDistinctFirstNames(group.getStudents()).size();
    }

    private String getLargest(Collection<Student> students, Function<Group, Integer> f) {
        return getMinGroupName(groupStream(students, Comparator.comparingInt(Student::getId)),
                Comparator.comparing(f).reversed().thenComparing(Group::getName));
    }

    @Override
    public String getLargestGroup(Collection<Student> students) {
        return getLargest(students, this::getGroupSize);
    }

    @Override
    public String getLargestGroupFirstName(Collection<Student> students) {
        return getLargest(students, this::getDistinctGroupSize);
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return getName(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return getName(students, Student::getLastName);
    }

    @Override
    public List<String> getGroups(List<Student> students) {
        return getName(students, Student::getGroup);
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return mapAndCollect(students, student -> student.getFirstName() + " " + student.getLastName(), ArrayList::new);
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return mapAndCollect(students, Student::getFirstName, TreeSet::new);
    }

    @Override
    public String getMinStudentFirstName(List<Student> students) {
        return students.stream().min(Student::compareTo).map(Student::getFirstName).orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return students.stream().sorted(Student::compareTo).collect(Collectors.toList());
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return students.stream().sorted(studentComparatorByName).collect(Collectors.toList());
    }

    private List<Student> findStudentsBy(Collection<Student> students, Predicate<Student> predicate) {
        return students.stream().filter(predicate).sorted(studentComparatorByName).collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return findStudentsBy(students, student -> student.getFirstName().equals(name));
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return findStudentsBy(students,student -> student.getLastName().equals(name));
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, String group) {
        return students.stream()
                .filter(student -> student.getGroup().equals(group))
                .sorted(studentComparatorByName).collect(Collectors.toList());
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, String group) {
        return findStudentsByGroup(students, group).stream()
                .collect(Collectors.toMap(
                        Student::getLastName,
                        Student::getFirstName,
                        BinaryOperator.minBy(String::compareTo)));
    }
}