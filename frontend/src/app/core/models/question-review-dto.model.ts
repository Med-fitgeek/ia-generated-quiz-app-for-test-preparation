export interface QuestionReviewDto {
  statement: string;
  choices: string[];
  correctIndex: number;
  userAnswer: number;
  explanation: string;
}