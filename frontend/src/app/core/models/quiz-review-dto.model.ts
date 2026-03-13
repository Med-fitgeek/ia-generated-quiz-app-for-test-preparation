import { QuestionReviewDto } from "./question-review-dto.model";

export interface QuizReviewDto {
  rate: number;
  questions: QuestionReviewDto[];
}