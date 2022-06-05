import { Course } from "./course";
import { Teacher } from "./teacher";

export class CourseTeacher {
    constructor(
        public id:number,
        public course: Course,
        public teacher: Teacher,
        public correctedHours: number,
        public originalHours: number,
        public observation: string
    ) {

    }
}
