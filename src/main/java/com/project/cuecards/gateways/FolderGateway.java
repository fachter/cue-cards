package com.project.cuecards.gateways;

import com.project.cuecards.entities.Folder;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidArgumentException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public interface FolderGateway {

    Folder getFolderByNameAndRootFolder(String folderName, Folder rootFolder);

    void add(Folder folder) throws InvalidArgumentException;

    void addList(ArrayList<Folder> folder) throws InvalidArgumentException;

    Folder getFolderById(int rootFolderId);

    ArrayList<Folder> getRootFoldersByUser(User user) throws InvalidArgumentException;
}
