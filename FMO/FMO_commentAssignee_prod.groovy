/*
*	@name 	FMO_commentAssignee_prod.groovy
*	@type 	Script Listener
*	@brief 	When an issue is assigned, add a comment and mention the assignee.
*/

import com.atlassian.jira.issue.Issue
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.comments.CommentManager
import com.atlassian.jira.user.util.UserManager
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.XXX")
log.setLevel(Level.DEBUG)

Issue thisIssue = issue
def loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def fmoAdmin = ComponentAccessor.getUserManager().getUserByName("fmoadmin")

def assignee = thisIssue.getAssignee()
if (!assignee) { return }

def comment = "This issue has been assigned to [~$assignee.username]."

try {
	ComponentAccessor.getCommentManager().create(thisIssue, fmoAdmin, comment, true)	
} catch (Exception e) {
	log.debug "Error adding comment: " + e
}