package com.project.cuecards.viewModels;

import java.util.ArrayList;

public class FolderViewModel {

    public String ID;
    public String name;
    public boolean isFolder = true;
    public ArrayList<FolderViewModel> subFolders = new ArrayList<>();
    public ArrayList<CueCardViewModel> cards = new ArrayList<>();
}
