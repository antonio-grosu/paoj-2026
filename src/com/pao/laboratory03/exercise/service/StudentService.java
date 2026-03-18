package com.pao.laboratory03.exercise.service;

import com.pao.laboratory03.exercise.exception.StudentNotFoundException;
import com.pao.laboratory03.exercise.model.Student;
import com.pao.laboratory03.exercise.model.Subject;

import java.util.*;

public class StudentService {
    private static StudentService instance;
    private final List<Student> students;

    private StudentService() {
        students = new ArrayList<>();
    }

    public static StudentService getInstance() {
        if (instance == null) {
            instance = new StudentService();
        }
        return instance;
    }

    public void addStudent(String name, int age) {
        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name)) {
                throw new RuntimeException("Studentul '" + name + "' exista deja.");
            }
        }
        students.add(new Student(name, age));
    }

    public Student findByName(String name) {
        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name)) return s;
        }
        throw new StudentNotFoundException("Studentul '" + name + "' nu a fost gasit.");
    }

    public void addGrade(String studentName, Subject subject, double grade) {
        findByName(studentName).addGrade(subject, grade);
    }

    public void printAllStudents() {
        if (students.isEmpty()) {
            System.out.println("Nu exista studenyi inregistrati.");
            return;
        }
        for (Student s : students) {
            System.out.println(s);
            for (Map.Entry<Subject, Double> entry : s.getGrades().entrySet()) {
                System.out.printf("  %s: %.2f%n", entry.getKey().name(), entry.getValue());
            }
        }
    }

    public void printTopStudents() {
        if (students.isEmpty()) {
            System.out.println("Nu exista studenyi inregistrati.");
            return;
        }
        List<Student> sorted = new ArrayList<>(students);
        sorted.sort((a, b) -> Double.compare(b.getAverage(), a.getAverage()));
        System.out.println("Top studenți:");
        for (int i = 0; i < sorted.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, sorted.get(i));
        }
    }

    public Map<Subject, Double> getAveragePerSubject() {
        
        
        Map<Subject, Double> sumMap = new HashMap<>();
        Map<Subject, Integer> countMap = new HashMap<>();

        for (Student s : students) {
            for (Map.Entry<Subject, Double> entry : s.getGrades().entrySet()) {
                sumMap.merge(entry.getKey(), entry.getValue(), Double::sum);
                countMap.merge(entry.getKey(), 1, Integer::sum);
            }
        }

       
        Map<Subject, Double> avgMap = new HashMap<>();
        
        for (Subject subject : sumMap.keySet()) {
            avgMap.put(subject, sumMap.get(subject) / countMap.get(subject));
        }
       
       
        return avgMap;
    }
}
