package com.project.cuecards.gateways;

import com.project.cuecards.entities.Folder;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.EntityNotFoundException;
import com.project.cuecards.exceptions.InvalidArgumentException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FolderGateway {

    Folder getFolderByNameAndRootFolder(String folderName, Folder rootFolder);

    void add(Folder folder) throws InvalidArgumentException;

    void saveList(List<Folder> folder) throws InvalidArgumentException;

    Folder getFolderById(long rootFolderId) throws EntityNotFoundException;

    List<Folder> getRootFoldersByUser(User user) throws InvalidArgumentException;

    void removeList(List<Folder> folders) throws InvalidArgumentException;
}
