package models;

import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Mike on 11/14/2015.
 */

@Entity
public class Tool extends Model {
    @Id
    public Long id;

    @Constraints.Required
    public String name;
}
