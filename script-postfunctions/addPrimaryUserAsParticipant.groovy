/*
*	@name	addPrimaryUserAsParticipant.groovy
*	@type	Script Post Function
*	@brief Adds the user in custom field "Primary User" to the field "Request Participants"
*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.IssueManager
import java.util.ArrayList
import com.atlassian.jira.user.ApplicationUser
import org.apache.log4j.Logger

def log = Logger.getLogger("com.acme.XXX")

def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def REQUEST_PARTICIPANTS_FIELD = "customfield_10600"
def PRIMARY_USER_FIELD = "customfield_10913"

CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
IssueManager issueManager = ComponentAccessor.getIssueManager()
MutableIssue issue = issue // FOR TESTING IN CONSOLE replace `issue`  with `issueManager.getIssueObject("{issuekey}")`

/* Get field objects */
def requestParticipantsField = customFieldManager.getCustomFieldObject(REQUEST_PARTICIPANTS_FIELD)
def primaryUserField = customFieldManager.getCustomFieldObject(PRIMARY_USER_FIELD)

/* Get Primary User */
ApplicationUser primaryUser = issue.getCustomFieldValue(primaryUserField) as ApplicationUser

if (primaryUser) {
	
	/* Create an empty arraylist and add Primary User */
	ArrayList<ApplicationUser> participants = []
	participants.add(primaryUser)

	/* Assign the arraylist to the Request Participants field */
	issue.setCustomFieldValue(requestParticipantsField, participants)

	/* Store to database. */
	try {
		issueManager.updateIssue(
			currentUser,
			issue,
			EventDispatchOption.ISSUE_UPDATED,
			false
		)
	} catch (Exception e) {
		log.debug "Exception: " + e
	}
	
} else {
	log.debug "No Primary User specified."
}