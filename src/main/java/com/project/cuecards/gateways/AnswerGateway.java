package com.project.cuecards.gateways;

import com.project.cuecards.entities.Answer;
import com.project.cuecards.exceptions.InvalidArgumentException;

import java.util.ArrayList;

public interface AnswerGateway {

    void removeList(ArrayList<Answer> answers) throws InvalidArgumentException;
}
