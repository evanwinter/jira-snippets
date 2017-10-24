////////////////////////////////////////////////////////////////////////////////
// File Name:      copy-comment.groovy
//
// Author:         Evan Winter
//
// Context:        ITCM
//
// Description:    When a comment is added to a subtask, duplicate it onto the subtask's parent.
//
////////////////////////////////////////////////////////////////////////////////

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue 
import com.atlassian.jira.issue.comments.CommentManager
import org.apache.log4j.Logger
import org.apache.log4j.Level

def commentManager = ComponentAccessor.getCommentManager()
def log = Logger.getLogger("com.acme.XXX")
log.setLevel(Level.DEBUG)

Issue currentIssue = issue
Issue parentIssue

def comment

if (currentIssue.isSubTask()) {
    parentIssue = currentIssue.getParentObject()
	comment = commentManager.getLastComment(currentIssue)
    log.debug("Comment body: " + comment.getBody())

	if (comment) {
		try {
            commentManager.create(parentIssue, comment.getAuthorApplicationUser(), comment.getBody(), true)
        } catch (Exception e) {
            log.debug "Error adding comment."
        }
	} else {
        log.debug "Comment is null."
    }
} else {
    log.debug "Not a subtask; comment ignored."
}