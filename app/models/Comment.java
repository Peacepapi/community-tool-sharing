package models;

import java.util.Date;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Comment extends Model {

	@Id
	public long id;

	@Constraints.Required
	public String body;

	@Constraints.Required
	public Users poster;

	@OneToMany
	public Tool tool;

	public Date datetime_posted;

	public static Model.Finder<Long, Comment> find = new Model.Finder<Long, Comment>(Comment.class);

}