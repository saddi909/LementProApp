package lementProApp.model;

/**
 * Created by Saadi on 16/07/2015.
 */
public class History {
    public String changeDate;
    public String description;
    public String id;
    public String authorAvatarFileld;
    public String authorId;
    public boolean authorisInVacation;
    public String authorText;
    public String authorFromAvatarFileld;
    public String authorFromId;
    public boolean authorFromisInVacation;
    public String authorFromText;
    public History(){
        changeDate = "";
        description = "";
        id = "";
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
