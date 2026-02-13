import { Difficulty } from "./difficulty.model";

export interface QuizGenerationRequest {
  sourceId: number;
  numberOfQuestions: number;
  difficulty: Difficulty;
}
