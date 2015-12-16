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
public class BorrowRequest extends Model {

    @Id
    public long id;

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

    public static Model.Finder<Long, BorrowRequest> find = new Finder<Long, BorrowRequest>(BorrowRequest.class);

    public static BorrowRequest createNewBorrowRequest(@Nonnull Users requester, @Nonnull Tool requestedTool) {
        BorrowRequest borrowRequest = new BorrowRequest();
        borrowRequest.requester = requester;
        borrowRequest.requestedTool = requestedTool;
        borrowRequest.requestTime = new Timestamp(System.currentTimeMillis());
        return borrowRequest;
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
