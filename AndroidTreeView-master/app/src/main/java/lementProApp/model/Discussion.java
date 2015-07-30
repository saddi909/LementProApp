package lementProApp.model;

/**
 * Created by User on 13.07.2015.
 */
public class Discussion {
    public String creationDate;
    public TaskFile files[];
    public String id;
    public boolean isEditable;
    public boolean isSelfAction;
    public boolean isSystem;
    public boolean isUnread;
    public String lastModifiedDate;
    public String message;
    public String simplifiedMessage;
    public String subject;
    public String authorAvatarFileld;
    public String authorId;
    public boolean authorisInVacation;
    public String authorText;
    public String authorFromAvatarFileld;
    public String authorFromId;
    public boolean authorFromisInVacation;
    public String authorFromText;
    public Discussion(){
        creationDate = "";
        files = new TaskFile[0];
        isEditable = false;
        isSelfAction = false;
        isSystem =false;
        isUnread = false;
        lastModifiedDate = "";
        message = "";
        simplifiedMessage = "";
        subject = "";
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
