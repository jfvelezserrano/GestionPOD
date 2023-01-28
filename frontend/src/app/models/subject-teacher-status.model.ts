import { Subject } from "./subject.model";

export class SubjectTeacherStatus {
    constructor(
        public subject: Subject,
        public joinedTeachers:string[],
        public leftHours: number
    ) {

    }
}
