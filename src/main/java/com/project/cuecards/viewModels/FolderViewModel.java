package com.project.cuecards.viewModels;

import java.util.ArrayList;

public class FolderViewModel {

    public Long id;
    public String name;
    public boolean isFolder = true;
    public ArrayList<FolderViewModel> subFolders = new ArrayList<>();
    public ArrayList<CueCardViewModel> cueCards = new ArrayList<>();
}
