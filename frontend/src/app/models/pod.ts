import { Course } from "./course";
import { SubjectModel } from "./subject";
import { Teacher } from "./teacher";

export class Pod {
    constructor(
        public id:number,
        public subject:SubjectModel,
        public course: Course,
        public teacher: Teacher,
        public chosenHours:number
    ) {

    }
}
