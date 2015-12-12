package models;

import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by Mike on 12/12/2015.
 */
@Entity
public class AdminUser {

    @Id
    public Long id;

    @Constraints.Required
    public String username = null;

    public String password_hash = null;



}
