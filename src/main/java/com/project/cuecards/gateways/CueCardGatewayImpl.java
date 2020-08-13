package com.project.cuecards.gateways;

import com.project.cuecards.entities.CueCard;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.repositories.CueCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CueCardGatewayImpl implements CueCardGateway {

    private final CueCardRepository cueCardRepository;

    @Autowired
    public CueCardGatewayImpl(CueCardRepository cueCardRepository) {
        this.cueCardRepository = cueCardRepository;
    }

    @Override
    public void removeList(ArrayList<CueCard> cueCards) throws InvalidArgumentException {
        try {
            cueCardRepository.deleteAll(cueCards);
        } catch (Exception e) {
            throw new InvalidArgumentException();
        }
    }
}
