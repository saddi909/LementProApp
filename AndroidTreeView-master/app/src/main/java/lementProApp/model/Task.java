package lementProApp.model;

/**
 * Created by User on 03.07.2015.
 */
public class Task {
    public String folderKey = "";
    public boolean CanDelete;
    public boolean CanEdit;
    public boolean CanRead;
    public boolean CanUnread;
    public int childrenCount;
    public boolean expanded;
    public String ID;
    public boolean isClosed;
    public boolean isMarked;
    public boolean isOutOfDate;
    public boolean isRouteTask;
    public boolean isUnread;
    public String authorID;
    public String authorAvatarFileId;
    public boolean isInVacation;
    public String authorText;
    public String creationDate;
    public String commentID;
    public String lastModifiedDate;
    public String simplifiedMessage;
    public String subject = "";
    public int newCommentCount;
    public int state;
    public int unreadChildrenCount;
    public String name;
    public Boolean isEmptyComment;
    public Task(){
        this.folderKey = "";
        this.CanDelete = false;
        this.CanEdit = false;
        this.CanRead = false;
        this.CanUnread = false;
        this.childrenCount = 0;
        this.expanded = false;
        this.ID = "";
        this.isClosed = false;
        this.isMarked =false;
        this.isOutOfDate = false;
        this.isRouteTask =false;
        this.isUnread =false;
        this.authorID = "";
        this.authorAvatarFileId = "";
        this.isInVacation = false;
        this.authorText = "";
        this.creationDate = "";
        this.commentID = "";
        this.lastModifiedDate = "";
        this.simplifiedMessage ="";
        this.subject = "";
        this.newCommentCount = 0;
        this.state = 0;
        this.unreadChildrenCount = 0;
        this.name = "";
        isEmptyComment = false;
    }




}
