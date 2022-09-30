export class StatisticsTeacher {
    constructor(
        public name: string,
        public originalHours: number,
        public correctedHours: number,
        public observation: string,
        public percentage: number,
        public charge: number,
        public numSubjects: number
    ) {

    }
}
