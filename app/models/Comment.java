package models;

import java.util.Date;
import com.avaje.ebean.Model;
import play.data.validation.Constraints;

import javax.annotation.Nonnull;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Comment extends Model {

	@Id
	public long id;

	@Constraints.Required
	public String body;

    @ManyToOne
	@Constraints.Required
	public Users poster;

	@ManyToOne
	@Constraints.Required
	public Tool tool;

	@Constraints.Required
	public Date datetime_posted;

	public static Model.Finder<Long, Comment> find = new Model.Finder<Long, Comment>(Comment.class);

	public static Comment createNewComment(@Nonnull Users user,@Nonnull Tool tool,@Nonnull String text) {
		if (user.id == null || tool.id == null || text.isEmpty()) {
			return null;
		}
		if (Users.find.byId(user.id) == null || Tool.find.byId(tool.id) == null) {
			return null;
		}

		Comment comment = new Comment();

		comment.poster = user;
		comment.tool = tool;
		comment.body = text;

		return comment;
	}
}