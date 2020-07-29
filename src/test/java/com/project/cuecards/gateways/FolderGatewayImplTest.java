package com.project.cuecards.gateways;

import com.project.cuecards.entities.Folder;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.repositories.FolderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FolderGatewayImplTest {

    private FolderGateway folderGateway;
    @Mock
    private FolderRepository folderRepositoryMock;


    @BeforeEach
    void setUp() {
        folderGateway = new FolderGatewayImpl(folderRepositoryMock);
    }

    @Test
    public void add_givenNull_thenThrowException() {
        Assertions.assertThrows(InvalidArgumentException.class,
                () -> folderGateway.add(null));
    }

    @Test
    public void add_givenNewFolder_thenSaveAndFlushFolderAndChangePassedFolder() throws Exception {
        Folder folder = new Folder();
        folder.setName("Test Folder");
        ArgumentCaptor<Folder> captor = ArgumentCaptor.forClass(Folder.class);
        Folder savedFolder = new Folder();
        savedFolder.setId((long) 1);
        savedFolder.setName("Test Folder");
        when(folderRepositoryMock.saveAndFlush(folder)).thenReturn(savedFolder);

        folderGateway.add(folder);

        verify(folderRepositoryMock, times(1)).saveAndFlush(captor.capture());
        assertEquals(folder, captor.getValue());
    }

    @Test
    public void addList_givenThrowsException_thenThrowInvalidArgumentException() throws Exception {
        doThrow(new IllegalArgumentException()).when(folderRepositoryMock).saveAll(any());

        Assertions.assertThrows(InvalidArgumentException.class, () -> folderGateway.addList(null));
    }

    @Test
    public void addList_givenArrayList_thenSaveList() throws Exception {
        when(folderRepositoryMock.saveAll(any())).thenReturn(any());
        ArrayList<Folder> folders = new ArrayList<>();
        Folder folder1 = new Folder();
        folder1.setName("Test 1");
        Folder folder2 = new Folder();
        folder2.setName("Test 2");
        Folder folder3 = new Folder();
        folder3.setName("Test 3");
        folder3.setSet(true);
        folders.add(folder1);
        folders.add(folder2);
        folders.add(folder3);

        folderGateway.addList(folders);

        verify(folderRepositoryMock, times(1)).saveAll(folders);
    }

    @Test
    public void getRootFoldersByUser_givenNull_thenThrowException() {
        Assertions.assertThrows(InvalidArgumentException.class,
                () -> folderGateway.getRootFoldersByUser(null));
    }

    @Test
    public void getRootFoldersByUser_givenValidUser_thenReturnList() throws Exception {
        ArrayList<Folder> folders = new ArrayList<>();
        Folder folder = new Folder();
        folder.setName("Folder");
        Folder set = new Folder();
        set.setName("Set");
        set.setSet(true);
        folders.add(folder);
        folders.add(set);
        User validUser = new User();
        validUser.setUsername("validUsername");
        when(folderRepositoryMock.findAllByCreatedByAndRootFolderIsNull(validUser)).thenReturn(folders);

        ArrayList<Folder> rootFoldersByUser = folderGateway.getRootFoldersByUser(validUser);

        assertEquals(folders, rootFoldersByUser);
    }

    @Test
    public void getRootFoldersByUser_givenRepoThrowsException_thenThrowException() {
        doThrow(IllegalArgumentException.class).when(folderRepositoryMock).findAllByCreatedByAndRootFolderIsNull(any());

        Assertions.assertThrows(InvalidArgumentException.class, () -> folderGateway.getRootFoldersByUser(new User()));
    }
}