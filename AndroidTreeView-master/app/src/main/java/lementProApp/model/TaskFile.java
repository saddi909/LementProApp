package lementProApp.model;

public class TaskFile{
    public String id;
    public boolean isFinal;
    public boolean isInStorage;
    public String name;
    public int revision;
    public String size;
    public String creationDate;
    public String authorAvatarFileld;
    public String authorId;
    public boolean authorisInVacation;
    public String authorText;
    public String authorFromAvatarFileld;
    public String authorFromId;
    public boolean authorFromisInVacation;
    public String authorFromText;
    public TaskFile(){
        id = "";
        isFinal = false;
        name = "";
        revision = 0;
        size = "";
        creationDate = "";
        authorAvatarFileld = "";
        authorId = "";
        authorisInVacation = false;
        authorText = "";
        authorFromAvatarFileld = "";
        authorFromId = "";
        authorFromisInVacation = false;
        authorFromText = "";

    }
}