package com.project.cuecards.gateways;

import com.project.cuecards.entities.Folder;
import com.project.cuecards.entities.User;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.repositories.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class FolderGatewayImpl implements FolderGateway {

    private final FolderRepository folderRepository;

    @Autowired
    public FolderGatewayImpl(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    @Override
    public Folder getFolderByNameAndRootFolder(String folderName, Folder rootFolder) {
        //TODO
        return null;
    }

    @Override
    public void add(Folder folder) throws InvalidArgumentException {
        if (folder == null)
            throw new InvalidArgumentException();
        folderRepository.saveAndFlush(folder);
    }

    @Override
    public void addList(ArrayList<Folder> folders) throws InvalidArgumentException {
        try {
            folderRepository.saveAll(folders);
        } catch (Exception e) {
            throw new InvalidArgumentException();
        }
    }

    @Override
    public Folder getFolderById(int rootFolderId) {
        //TODO
        return null;
    }

    @Override
    public ArrayList<Folder> getRootFoldersByUser(User user) throws InvalidArgumentException {
        if (user == null)
            throw new InvalidArgumentException();
        try {
            return folderRepository.findAllByCreatedByAndRootFolderIsNull(user);
        } catch (Exception e) {
            throw new InvalidArgumentException();
        }
    }

    @Override
    public void removeList(ArrayList<Folder> folders) throws InvalidArgumentException {
        try {
            folderRepository.deleteAll(folders);
        } catch (Exception e) {
            throw new InvalidArgumentException();
        }
    }
}
