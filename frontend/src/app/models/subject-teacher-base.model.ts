import { Subject } from "./subject.model";

export class SubjectTeacherBase {
    constructor(
        public subject: Subject,
        public joinedTeachers: string[]
    ) {

    }
}
