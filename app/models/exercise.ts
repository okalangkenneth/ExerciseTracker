export interface Exercise {
    id: string;
    type: string;
    duration: number;
    steps?: number;
    distance?: number;
    date: Date;
  }
  
  export interface DailyGoal {
    steps: number;
    duration: number;
    distance: number;
  }
  