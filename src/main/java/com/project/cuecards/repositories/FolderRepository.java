package com.project.cuecards.repositories;

import com.project.cuecards.entities.Folder;
import com.project.cuecards.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;

public interface FolderRepository extends JpaRepository<Folder, Long> {

    Folder findByNameAndRootFolder(String name, Folder rootFolder);

    ArrayList<Folder> findAllByCreatedByAndRootFolderIsNullAndRoomIsNull(User user);
}
