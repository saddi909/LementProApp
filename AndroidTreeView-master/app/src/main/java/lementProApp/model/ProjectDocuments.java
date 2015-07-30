package lementProApp.model;

/**
 * Created by Saadi on 30/07/2015.
 */
public class ProjectDocuments {
    public String name;
    public String Description;
    public String author;
    public String registrationDate;
    public String deadline;
    public String lastModifiedDate;
    public String closedDate;
    public Boolean isClosed;

    public ProjectDocuments(){
        name = "";
        Description = "";
        author = "";
        registrationDate = "";
        deadline = "";
        lastModifiedDate = "";
        closedDate = "";
        isClosed = false;
    }
}
