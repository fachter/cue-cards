package com.project.cuecards.gateways;

import com.project.cuecards.entities.Answer;
import com.project.cuecards.exceptions.InvalidArgumentException;

import java.util.ArrayList;
import java.util.List;

public interface AnswerGateway {

    void removeList(List<Answer> answers) throws InvalidArgumentException;
}
