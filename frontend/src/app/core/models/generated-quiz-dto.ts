import { GeneratedQuestion } from "./generated-question.model";

export interface GeneratedQuizDto {
  quizId: number;
  generatedQuestions: GeneratedQuestion[];
}