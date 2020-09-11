package com.project.cuecards.gateways;

import com.project.cuecards.entities.Answer;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.repositories.AnswerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnswerGatewayImpl implements AnswerGateway {

    private final AnswerRepository answerRepository;

    public AnswerGatewayImpl(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    @Override
    public void removeList(List<Answer> answers) throws InvalidArgumentException {
        try {
            answerRepository.deleteAll(answers);
        } catch (Exception e) {
            throw new InvalidArgumentException();
        }

    }
}
