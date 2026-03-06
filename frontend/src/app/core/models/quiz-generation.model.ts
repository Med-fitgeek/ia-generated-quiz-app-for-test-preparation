import { Difficulty } from "./difficulty.model";

export interface QuizGenerationRequest {
  sourceId: number;
  title: string;
  numberOfQuestions: number;
  difficulty: Difficulty;
}
