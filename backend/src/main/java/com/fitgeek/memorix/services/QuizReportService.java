package com.fitgeek.memorix.services;

import com.fitgeek.memorix.entities.QuizReport;
import com.fitgeek.memorix.entities.QuizSession;

public interface QuizReportService {

    QuizReport generateReport(QuizSession session);

}
