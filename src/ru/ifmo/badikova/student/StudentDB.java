package ru.ifmo.badikova.student;

import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;

import java.sql.Array;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class StudentDB implements StudentQuery {

    private final Comparator<Student> studentComparator = Comparator.comparing(Student::getLastName).
            thenComparing(Student::getFirstName).thenComparing(Student::getId);

    private List<String> getName(List<Student> students, Function<Student, String> f) {
        return students.stream().map(f).collect(Collectors.toList());
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
        return students.stream().map(student -> student.getFirstName() + " " + student.getLastName()).collect(Collectors.toList());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return students.stream().map(Student::getFirstName).collect(Collectors.toSet());
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
        return students.stream().sorted(studentComparator).collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return students.stream().filter(student -> student.getFirstName().equals(name)).collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return students.stream().filter(student -> student.getLastName().equals(name)).collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, String group) {
        return students.stream().filter(student -> student.getGroup().equals(group)).sorted(studentComparator).collect(Collectors.toList());
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, String group) {
        return findStudentsByGroup(students, group).stream().collect(Collectors.toMap(Student::getLastName, Student::getFirstName, BinaryOperator.minBy(String::compareTo)));
    }
}
