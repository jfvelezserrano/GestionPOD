package com.urjc.backend.service;

import com.urjc.backend.Data;
import com.urjc.backend.model.Teacher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataServices {

    public static List<Object[]> createBarGraphData() {
        Object[] item1 = new Object[]{"Multimedia", 100, 50};
        Object[] item2 = new Object[]{"Estadística", 120, 40};
        return Arrays.asList(item1, item2);
    }

    public static List<Object[]> createDoughnutChartData() {
        Object[] item1 = new Object[]{"Multimedia", 40};
        Object[] item2 = new Object[]{"Estadística", 60};
        return Arrays.asList(item1, item2);
    }

    public static List<Object[]> createStatisticsAllTeachers(){
        Teacher teacher1 = Data.createTeacher("Luis Rodriguez", "ejemplo@ejemplo.com").get();
        Teacher teacher2 = Data.createTeacher("Pedro López", "ejemplo2@ejemplo2.com").get();

        Data.addCourseTeachers(teacher1, Data.createCourse("2022-2023").get());
        Data.addCourseTeachers(teacher2, Data.createCourse("2022-2023").get());
        Data.addCourseTeachers(teacher2, Data.createCourse("2021-2022").get());

        Object[] object1 = new Object[]{teacher1, teacher1.getCourseTeachers()};
        Object[] object2 = new Object[]{teacher2, teacher2.getCourseTeachers()};

        return Arrays.asList(object1, object2);
    }

    public static List<Integer[]> statisticsByTeacherAndCourse(){
        Integer[] data = new Integer[]{40, 100, 2};
        List<Integer[]> statistics = new ArrayList<>();
        statistics.add(data);
        return statistics;
    }
}
