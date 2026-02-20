import { SessionStatus } from "./sesion-status.model";

export interface SessionRequestDto {
    quizId: number;
    sessionStatus: SessionStatus;
    duration : number;
}