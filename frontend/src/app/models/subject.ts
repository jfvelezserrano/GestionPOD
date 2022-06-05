import { CourseSubject } from "./course-subject";
import { Pod } from "./pod";
import { Schedule } from "./schedule";

export class SubjectModel {
    constructor(
        public id:number|any,
        public code:string,
        public name: string,
        public title: string,
        public totalHours:number|any,
        public campus: string,
        public year:number|any,
        public quarter:string,
        public type:string,
        public turn:string,
        public career:string,
        public pods:Set<Pod>|any,
        public courseSubjects: Set<CourseSubject>|any,
        public assitanceCareers: Array<string>,
        public schedules: Array<Schedule>
    ) {

    }
}
