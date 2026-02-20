package com.fitgeek.IATestPreparator.services.impl;

import com.fitgeek.IATestPreparator.Prompting.Impl.PromptV1Strategy;
import com.fitgeek.IATestPreparator.Prompting.PromptStrategy;
import com.fitgeek.IATestPreparator.dtos.GeneratedQuizDto;
import com.fitgeek.IATestPreparator.dtos.QuizGenerationRequestDto;
import com.fitgeek.IATestPreparator.entities.KnowledgeSource;
import com.fitgeek.IATestPreparator.entities.Question;
import com.fitgeek.IATestPreparator.entities.Quiz;
import com.fitgeek.IATestPreparator.entities.User;
import com.fitgeek.IATestPreparator.entities.enums.Difficulty;
import com.fitgeek.IATestPreparator.excpetion.BusinessException;
import com.fitgeek.IATestPreparator.dtos.GeneratedQuestionDto;
import com.fitgeek.IATestPreparator.repositories.KnowledgeSourceRepository;
import com.fitgeek.IATestPreparator.repositories.UserRepository;
import com.fitgeek.IATestPreparator.services.QuizGenerationService;
import com.fitgeek.IATestPreparator.services.QuizOrchestrationService;
import com.fitgeek.IATestPreparator.services.QuizSessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class QuizOrchestrationServiceImpl implements QuizOrchestrationService {

    private final KnowledgeSourceRepository knowledgeSourceRepository;
    private final QuizGenerationService quizGenerationService;
    private final UserRepository userRepository;

    @Override
    public GeneratedQuizDto generateQuizFromKnowledge(
            QuizGenerationRequestDto requestDto,
            UserDetails userDetails
    ) {
        User owner = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        KnowledgeSource source = knowledgeSourceRepository
                .findById(requestDto.sourceId())
                .orElseThrow(() -> new BusinessException("KnowledgeSource not found"));

        if (!source.getOwner().getId().equals(owner.getId())) {
            throw new BusinessException("You do not own this knowledge source");
        }

        try {

            return quizGenerationService.generateQuiz(
                    source,
                    requestDto.numberOfQuestions(),
                    Difficulty.valueOf(requestDto.difficulty()));

        } catch (Exception e) {
            throw new BusinessException("Failed to generate quiz: " + e.getMessage());
        }

    }

}
