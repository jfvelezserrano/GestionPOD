import { CourseSubject } from "./course-subject";
import { CourseTeacher } from "./course-teacher";
import { Pod } from "./pod";

export class Course {
    constructor(
        public id:number,
        public name:string,
        public creationDate: Date,
        public pods:Set<Pod>,
        public courseTeachers: Set<CourseTeacher>,
        public courseSubjects:Set<CourseSubject>
    ) {

    }
}
