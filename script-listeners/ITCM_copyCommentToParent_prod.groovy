/*
*   @name   ITCM_copyCommentToParent_prod.groovy
*   @type   Script Listener
*   @brief  Copies new comments added to a subtask to the subtask's parent issue.
*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue 
import com.atlassian.jira.issue.comments.CommentManager

Issue thisIssue = issue
if (!thisIssue.isSubTask()) { return "Not a subtask - don't copy comment."}

Issue parentIssue = thisIssue.getParentObject()

def commentManager = ComponentAccessor.getCommentManager()
def commentToCopy = commentManager.getLastComment(thisIssue)

if (commentToCopy) {
	try {
        commentManager.create(
            parentIssue,
            commentToCopy.getAuthorApplicationUser(),
            commentToCopy.getBody(),
            true
        )
    } catch (Exception e) {
        log.debug "Exception: " + e
    }
} else {
    log.debug "Comment is null."
}