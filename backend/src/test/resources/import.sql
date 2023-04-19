INSERT INTO course (id, name, creation_date) VALUES (1l, '2021-2022', '2022-12-12 00:00:00');
INSERT INTO course (id, name, creation_date) VALUES (2l, '2022-2023', '2022-12-15 00:00:00');

INSERT INTO teacher (id, email, name) VALUES (3l, 'aliciaejemplo@gmail.com', 'Merino Martínez, Alicia');
INSERT INTO teacher_roles (teacher_id, roles) VALUES (3l, 'TEACHER');
INSERT INTO teacher_roles (teacher_id, roles) VALUES (3l, 'ADMIN');
INSERT INTO teacher (id, email, name) VALUES (6l, 'ejemplo@gmail.com', 'Luis Rodriguez');
INSERT INTO teacher_roles (teacher_id, roles) VALUES (6l, 'TEACHER');

INSERT INTO course_teacher (course_id, teacher_id, corrected_hours, observation, original_hours) VALUES (1l, 6l, 115, 'Tengo horas de más', 90);
INSERT INTO course_teacher (course_id, teacher_id, corrected_hours, observation, original_hours) VALUES (1l, 3l, 130, 'Elegir una asigmatura más', 100);
INSERT INTO course_teacher (course_id, teacher_id, corrected_hours, observation, original_hours) VALUES (2l, 3l, 100, null , 100);

INSERT INTO subject (id, campus, career, code, name, quarter, title, total_hours, turn, type, year)
    VALUES (4l, 'Fuenlabrada', 'G_SOFT_2A(M)', '234234234', 'Asignatura 1', 'Segundo Cuatrimestre', '(2034) Grado Ingeniería Software (M)', 180, 'M', 'Optativa', 2);
INSERT INTO subject (id, campus, career, code, name, quarter, title, total_hours, turn, type, year)
VALUES (5l, 'Móstoles', 'G_SOFT_2A(M)', '12135127', 'Asignatura 2', 'Segundo Cuatrimestre', '(2034) Grado Ingeniería Informática (T)', 150, 'T', 'Obligatoria', 2);

INSERT INTO course_subject (course_id, subject_id) VALUES (1l, 4l);
INSERT INTO course_subject (course_id, subject_id) VALUES (1l, 5l);

INSERT INTO pod (course_id, subject_id, teacher_id, chosen_hours) VALUES (1l, 5l, 3l, 50);
INSERT INTO pod (course_id, subject_id, teacher_id, chosen_hours) VALUES (1l, 5l, 6l, 30);