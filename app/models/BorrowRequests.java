package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.annotation.Nonnull;
import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by Mike on 12/16/2015.
 */

@Entity
@UniqueConstraint(columnNames = {"requester_id","requested_tool_id"})
public class BorrowRequests extends Model {

    @Id
    public long id;

    @Constraints.Required
    @ManyToOne
    public Users owner;

    @Constraints.Required
    public String message;

    @Constraints.Required
    public Timestamp timestamp;

    @ManyToOne
    @Constraints.Required
    public Users requester;

    @ManyToOne
    @Constraints.Required
    public Tool requestedTool;

    @Constraints.Required
    public Timestamp requestTime;

    @OneToOne(mappedBy = "request")
    public RequestNotification notification;

    public static Model.Finder<Long, BorrowRequests> find = new Finder<Long, BorrowRequests>(BorrowRequests.class);

    public static BorrowRequests createNewBorrowRequest(@Nonnull Users requester, @Nonnull Tool requestedTool) {
        BorrowRequests borrowRequests = new BorrowRequests();
        borrowRequests.requester = requester;
        borrowRequests.requestedTool = requestedTool;
        borrowRequests.requestTime = new Timestamp(System.currentTimeMillis());
        return borrowRequests;
    }

    @PostPersist
    private void postPersist() {
        //this.requestedTool.owner.notifyRequest(this);
    }

/*    @PreRemove
    private void preRemove() {
        //send user notification here
        //ie: requester.notify();
    }*/
}
