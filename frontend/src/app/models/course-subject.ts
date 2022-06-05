import { Course } from "./course";
import { SubjectModel } from "./subject";

export class CourseSubject {
    constructor(
        public id:number,
        public course:Course,
        public subject: SubjectModel
    ) {

    }
}
