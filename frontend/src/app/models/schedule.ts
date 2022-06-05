export class Schedule {
    constructor(
        public id:number|any,
        public dayWeek:CharacterData,
        public startTime: string,
        public endTime: string
    ) {

    }
}
