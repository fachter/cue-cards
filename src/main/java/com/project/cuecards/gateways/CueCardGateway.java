package com.project.cuecards.gateways;

import com.project.cuecards.entities.CueCard;
import com.project.cuecards.exceptions.InvalidArgumentException;

import java.util.ArrayList;

public interface CueCardGateway {

    void removeList(ArrayList<CueCard> cueCards) throws InvalidArgumentException;
}
