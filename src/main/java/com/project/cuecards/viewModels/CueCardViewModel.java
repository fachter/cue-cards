package com.project.cuecards.viewModels;

import com.project.cuecards.enums.CardType;

import java.util.ArrayList;

public class CueCardViewModel {

    public String id;
    public String cardTopic;
    public String questionText;
    public String solution;
    public ArrayList<AnswerViewModel> answers = new ArrayList<>();
    public int cardLevel = 0;
    public CardType cardType = CardType.FT;
}
