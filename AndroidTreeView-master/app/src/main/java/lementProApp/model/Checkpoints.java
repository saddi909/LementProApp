package lementProApp.model;

/**
 * Created by Saadi on 20/07/2015.
 */
public class Checkpoints {
    public String dateClosed;
    public String description;
    public int duration;
    public String id;
    public boolean isClosed;
    public boolean isExpired;
    public String name;
    public String startDateTime;
    public String authorAvatarFileld;
    public String authorId;
    public boolean authorisInVacation;
    public String authorText;
    public String authorFromAvatarFileld;
    public String authorFromId;
    public boolean authorFromisInVacation;
    public String authorFromText;
    public Checkpoints(){
        dateClosed = "";
        description = "";
        duration=0;
        id = "";
        isClosed = false;
        isExpired =false;
        name = "";
        startDateTime = "";
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
