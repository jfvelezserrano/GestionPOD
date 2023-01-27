package com.urjc.backend.controller;

import com.urjc.backend.Data;
import com.urjc.backend.model.Subject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataControllers {

    public static List<Object[]> createSubjectsWithConflicts(){
        ArrayList teachers = new ArrayList();
        teachers.add("30h Luis Rodriguez");

        Object[] subjectWithConflicts = new Object[]{Data.createSubject("5436834", "Estadística").get(), teachers, 30, Collections.emptyList()};
        List<Object[]> subjectsWithConflicts = new ArrayList<>();
        subjectsWithConflicts.add(subjectWithConflicts);
        return subjectsWithConflicts;
    }

    public static List<Object[]> createSubjectsWithTeachers() {
        Subject subject1 = Data.createSubject("2323232", "Estructuras de datos").get();
        subject1.setSchedules(new ArrayList<>());
        subject1.setAssistanceCareers(new ArrayList<>());
        Subject subject2 = Data.createSubject("4545454", "Bases de datos").get();
        subject2.setSchedules(new ArrayList<>());
        subject2.setAssistanceCareers(new ArrayList<>());

        ArrayList teachers = new ArrayList();
        teachers.add("30h Luis Rodriguez");

        ArrayList<Object[]> result = new ArrayList<>();
        result.add(new Object[] {subject1, teachers});
        result.add(new Object[] {subject2, Collections.emptyList()});

        return result;
    }

    public static ByteArrayInputStream createByteArrayInputStream(List<String[]> body) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        CSVPrinter printer = new CSVPrinter(new PrintWriter(stream), CSVFormat.Builder.create().setDelimiter(";").build());
        for (String[] line:body) {
            printer.printRecord(line);
        }

        printer.flush();
        return new ByteArrayInputStream(stream.toByteArray());
    }
}
