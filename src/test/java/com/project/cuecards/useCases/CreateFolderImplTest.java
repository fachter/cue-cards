package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.CreateFolder;
import com.project.cuecards.entities.Folder;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.gateways.FolderGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateFolderImplTest {

    private CreateFolder useCase;
    @Mock private FolderGateway folderGatewayMock;
    private String rootFolderName;
    private String testFolderName;

    @BeforeEach
    void setUp() {
        rootFolderName = "RootFolderName";
        testFolderName = "TestName";
        useCase = new CreateFolderImpl(folderGatewayMock);
    }

    @Test
    void create_givenNull_thenThrowException() {
        Assertions.assertThrows(InvalidArgumentException.class,
                () -> useCase.create(null, null));
    }

    @Test
    public void create_givenEmptyString_thenThrowException() throws Exception {
        Assertions.assertThrows(InvalidArgumentException.class,
                () -> useCase.create("", null));    }

    @Test
    void create_givenNewFolderAndNoRoot_thenCreateInRootFolder() throws Exception {
        Folder expectedFolder = new Folder();
        expectedFolder.setName(testFolderName);
        expectedFolder.setRootFolder(null);
        ArgumentCaptor<Folder> captor = ArgumentCaptor.forClass(Folder.class);

        useCase.create(testFolderName, null);

        verify(folderGatewayMock, times(1)).add(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(expectedFolder);
    }

    @Test
    public void create_givenRootFolderDoesNotExists_thenThrowException() {
        when(folderGatewayMock.getFolderById(1)).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> useCase.create(testFolderName, 1));
    }

    @Test
    void create_givenNewFolderAndRootFolderName_thenCreateSubfolderOfRootFolder() throws Exception {
        Folder expectedFolder = new Folder();
        expectedFolder.setName(testFolderName);
        Folder expectedRootFolder = new Folder();
        expectedRootFolder.setName(rootFolderName);
        expectedFolder.setRootFolder(expectedRootFolder);
        ArgumentCaptor<Folder> captor = ArgumentCaptor.forClass(Folder.class);
        when(folderGatewayMock.getFolderById(1)).thenReturn(expectedRootFolder);

        useCase.create(testFolderName, 1);

        verify(folderGatewayMock, times(1)).add(captor.capture());
        assertThat(captor.getValue()).usingRecursiveComparison().isEqualTo(expectedFolder);
    }

    @Test
    public void create_givenNameAlreadyExists() throws Exception {
        //TODO
    }
}
