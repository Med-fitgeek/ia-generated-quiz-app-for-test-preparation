package com.fitgeek.IATestPreparator.services;

import com.fitgeek.IATestPreparator.entities.QuizReport;
import com.fitgeek.IATestPreparator.entities.QuizSession;

public interface QuizReportService {

    QuizReport generateReport(QuizSession session);

}
