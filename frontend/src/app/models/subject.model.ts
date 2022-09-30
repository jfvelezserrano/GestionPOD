import { Schedule } from "./schedule.model";

export class Subject {
    constructor(
        public id:number | any,
        public code:string,
        public name: string,
        public title: string,
        public totalHours:number| any,
        public campus: string,
        public year:number | any,
        public quarter:string,
        public type:string,
        public turn:string,
        public career:string,
        public assistanceCareers: Array<string>,
        public schedules: Array<Schedule>
    ) {

    }
}
