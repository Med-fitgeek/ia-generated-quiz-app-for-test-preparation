export interface QuizResponseDto {
    id: number;
    title: string;
    ownerId: number;
    generatedAt: Date;
    numberOfSessions: number;

}