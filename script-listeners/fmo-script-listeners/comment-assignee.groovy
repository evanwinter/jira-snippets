////////////////////////////////////////////////////////////////////////////////
// File Name:      comment-assignee.groovy
//
// Author:         Evan Winter
//
// Context:        FMO
//
// Description:    When an issue's assignee changes (or is set initially), add a
//                 comment to communicate this change to the customer.
//
////////////////////////////////////////////////////////////////////////////////

import com.atlassian.jira.issue.Issue
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.comments.CommentManager
import com.atlassian.jira.user.util.UserManager
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.XXX")
log.setLevel(Level.DEBUG)

UserManager userManager = ComponentAccessor.getUserManager()
CommentManager commentManager = ComponentAccessor.getCommentManager()
ComponentManager componentManager = ComponentManager.getInstance()

Issue currentIssue = issue
def assignee
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def fmoAdmin = userManager.getUserByName("fmoadmin")

assignee = currentIssue.getAssignee()

if ( assignee ) {
	
	// Get the last comment entered in on the issue to a String
	def comment = "This issue has been assigned to [~" + assignee.getUsername() + "]."

	// Check if the issue exists
	if ( currentIssue ) {
		// Create a comment on the issue
		try {
			commentManager.create( currentIssue, fmoAdmin, comment, true )	
		}
		catch ( Exception e ) {
			log.debug "Error adding comment."
			log.debug(e)
		}
	} else {
		log.debug "Issue is null."
	}

} else {
	log.debug "This issue was unassigned."
}