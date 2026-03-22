import { GeneratedQuestion } from "./generated-question.model";

export interface GeneratedQuizDto {
  quizId: number;
  title: string;
  generatedQuestions: GeneratedQuestion[];
}