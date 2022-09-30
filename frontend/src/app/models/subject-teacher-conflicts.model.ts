import { Subject } from "./subject.model";

export class SubjectTeacherConflicts {
    constructor(
        public subject: Subject,
        public joinedTeachers:string[],
        public leftHours: number,
        public conflicts:string[],
    ) {

    }
}
