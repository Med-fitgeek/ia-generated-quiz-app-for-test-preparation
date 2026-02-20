import { SubmitAnswerRequestDto } from "./submit-answer-request-dto.models";

export interface SubmitSessionWrapperDto {
    sessionId: number,
    sessionRequestDto: SubmitAnswerRequestDto
}