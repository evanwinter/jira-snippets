/*
*	@name 	ITCM_deleteSubtasks_prod.groovy
*	@type 	Post function
*	@brief 	Deletes all subtasks associated with the current issue.
*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.XXX")
log.setLevel(Level.DEBUG)

def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def issueManager = ComponentAccessor.getIssueManager()

// returns a Collection<Issue> with all the subtasks of the current issue
def subTasksList = issue.getSubTaskObjects()

// delete each subtask
for (subTask in subTasksList) {
	log.debug "Deleting subtask: "+subTask.getKey()
    issueManager.deleteIssue(user, subTask, EventDispatchOption.ISSUE_DELETED, false)
}