package com.project.cuecards.gateways;

import com.project.cuecards.entities.CueCard;
import com.project.cuecards.exceptions.InvalidArgumentException;

import java.util.ArrayList;
import java.util.List;

public interface CueCardGateway {

    void removeList(List<CueCard> cueCards) throws InvalidArgumentException;
}
