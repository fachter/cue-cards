package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.GetRoomsWebUseCase;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class GetRoomsWebUseCaseImplTest {

    private GetRoomsWebUseCase useCase;
    @BeforeEach
    void setUp() {
        useCase = new GetRoomsWebUseCaseImpl();
    }
}