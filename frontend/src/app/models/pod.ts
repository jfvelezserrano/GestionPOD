import { Course } from "./course";
import { SubjectModel } from "./subject";
import { Teacher } from "./teacher";

export class Pod {
    constructor(
        public id:number|any,
        public subject:SubjectModel|any,
        public course: Course|any,
        public teacher: Teacher|any,
        public chosenHours:number|any
    ) {

    }
}
