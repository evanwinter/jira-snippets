/*
*	@name 	FMO_commentAssignee_prod.groovy
*	@type 	Script Listener
*	@brief 	When an issue is assigned, add a comment and mention the assignee.
*/

import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.comments.CommentManager
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.security.groups.GroupManager
import com.atlassian.jira.event.type.EventDispatchOption
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.XXX")
log.setLevel(Level.DEBUG)

MutableIssue thisIssue = event.issue as MutableIssue
def loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def fmoAdmin = ComponentAccessor.getUserManager().getUserByName("fmoadmin")

def assignee = thisIssue.getAssignee()
if (!assignee) { return }

// If assignee is an FMO student, add Jonathan Wolff as Watcher
GroupManager groupManager = ComponentAccessor.getGroupManager()
def watcherManager = ComponentAccessor.getWatcherManager()
ApplicationUser jonathan = ComponentAccessor.getUserManager().getUserByKey('jwolf5')
if (groupManager.isUserInGroup(assignee, 'fmo-students')) {
    if (!watcherManager.isWatching(jonathan, thisIssue)) {
    	watcherManager.startWatching(jonathan, thisIssue)
    }
}

def comment = "This issue has been assigned to [~$assignee.username]."
try {
	ComponentAccessor.getCommentManager().create(thisIssue, fmoAdmin, comment, true)	
} catch (Exception e) {
	log.debug "Error adding comment: " + e
}

try {
	ComponentAccessor.getIssueManager().updateIssue(loggedInUser, thisIssue, EventDispatchOption.ISSUE_UPDATED, false)
} catch (Exception e) {
	log.debug "Exception: " + e
	return 'An error occured. Please contact your JIRA administrator.'
}


