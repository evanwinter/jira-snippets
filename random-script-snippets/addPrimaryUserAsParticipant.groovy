/*
*	Name:		addPrimaryUserAsParticipant.groovy
*	Author: 	Evan Winter
* 	
* 	@brief Post function to get user in custom field "Primary User" and add them as a Request Participant
*
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

MutableIssue myIssue = issue // FOR TESTING IN CONSOLE -> issueManager.getIssueObject("KEY-000")

/* Get custom field objects */
def requestParticipantsField = customFieldManager.getCustomFieldObject(REQUEST_PARTICIPANTS_FIELD)
def primaryUserField = customFieldManager.getCustomFieldObject(PRIMARY_USER_FIELD)

/* Get Primary User */
ApplicationUser primaryUser = myIssue.getCustomFieldValue(primaryUserField) as ApplicationUser

if (primaryUser) {
	
	/* Add Primary User to array */
	ArrayList<ApplicationUser> participants = []
	participants.add(primaryUser)

	/* Update the Request Participants field */
	myIssue.setCustomFieldValue(
	    requestParticipantsField, 
	    participants
	)

	/* Store to database. */
	try {
		issueManager.updateIssue(
			currentUser,
			myIssue,
			EventDispatchOption.ISSUE_UPDATED,
			false
		)
	} catch (Exception e) {
		log.debug "Exception: " + e
	}
	
} else {
	log.debug "No Primary User specified."
}