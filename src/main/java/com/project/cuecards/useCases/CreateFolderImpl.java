package com.project.cuecards.useCases;

import com.project.cuecards.boundaries.CreateFolder;
import com.project.cuecards.entities.Folder;
import com.project.cuecards.exceptions.InvalidArgumentException;
import com.project.cuecards.gateways.FolderGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateFolderImpl implements CreateFolder {

    private final FolderGateway folderGateway;

    @Autowired
    public CreateFolderImpl(FolderGateway folderGateway) {
        this.folderGateway = folderGateway;
    }

    @Override
    public void create(String name, Integer rootFolderId) throws InvalidArgumentException {
        if (name == null || name.equals(""))
            throw new InvalidArgumentException();
        Folder folder = new Folder();
        folder.setName(name);
        if (rootFolderId != null)
            folder.setRootFolder(folderGateway.getFolderById(rootFolderId));
        folderGateway.add(folder);
    }
}
