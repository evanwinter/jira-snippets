/* 
	This is a Post Function script that gets the current issue's most recent comment
	and copies it to its parent issue. If a user provides a comment during the transition
	in which this Post Function is embedded, that's the comment it will use. 
*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue 
import com.atlassian.jira.issue.comments.CommentManager

Issue parent = issue.getParentObject()
def commentManager = ComponentAccessor.getCommentManager()

def comment = commentManager.getLastComment(issue)

if (comment) {
	commentManager.create(parent, comment.getAuthorApplicationUser(), comment.getBody(), true)
}