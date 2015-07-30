package lementProApp.model;

/**
 * Created by User on 18.06.2015.
 */
public class Folder {
    public String text = "";
    public int ID = 0;
    public String folderkey = "";
    public boolean hasSubFolders = false;
    public boolean hasGroupings = false;
    public boolean isGrouping = false;
    public String parentId = "";

    public Folder(String name, int ID, String fkey, boolean hasSubFolders, boolean hasGroupings, boolean isGrouping, String parentID){
        this.text = name;
        this.ID = ID;
        this.folderkey = fkey;
        this.hasSubFolders = hasSubFolders;
        this.hasGroupings = hasGroupings;
        this.isGrouping = isGrouping;
        this.parentId = parentID;
    }
}
