package models;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.annotation.Nonnull;
import javax.persistence.*;

/**
 * Created by Mike on 12/16/2015.
 */

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class RequestNotification extends Notification {


    @Constraints.Required
    @OneToOne
    public BorrowRequest request;

    public static Model.Finder<Long, RequestNotification> find =
            new Finder<Long, RequestNotification>(RequestNotification.class);

    public static RequestNotification createNewRequestNotification(
            @Nonnull Users owner,
            @Nonnull BorrowRequest request) {
        RequestNotification notification = new RequestNotification();
        notification.owner = owner;
        return notification;
    }
}
