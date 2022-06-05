import { CourseTeacher } from "./course-teacher";
import { Pod } from "./pod";

export class Teacher {
    constructor(
        public id:number|any,
        public name: string,
        public email: string,
        public roles: Array<string>,
        public courseTeacher: Set<CourseTeacher>|any,
        public pods: Set<Pod>|any
    ) {

    }
}
