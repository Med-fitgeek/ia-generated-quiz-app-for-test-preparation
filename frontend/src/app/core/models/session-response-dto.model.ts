import { SessionStatus } from "./sesion-status.model";

export interface SessionResponseDto {
    id: number;
    status: SessionStatus;
    totalQuestions: number;
    rate: number;
}