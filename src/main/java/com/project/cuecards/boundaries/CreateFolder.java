package com.project.cuecards.boundaries;

import com.project.cuecards.exceptions.InvalidArgumentException;

public interface CreateFolder {

    void create(String name, Integer rootFolderId) throws InvalidArgumentException;
}
