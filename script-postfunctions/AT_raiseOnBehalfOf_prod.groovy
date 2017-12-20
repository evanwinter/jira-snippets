/*
*	@name 	AT_raiseOnBehalfOf_prod.groovy
*	@type	Post function	
*	@brief 	Sets the user in custom field "Raise on behalf of" as Reporter, 
*			and adds the initial Reporter as a Request Participant.
*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.MutableIssue
import java.util.ArrayList
import com.atlassian.jira.event.type.EventDispatchOption

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

/* Initialize variables */
ApplicationUser currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
MutableIssue thisIssue = issue

def requestParticipantsField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10600")
def raiseOnBehalfOfField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_12131")

ApplicationUser thisReporter = thisIssue.getReporter()
ApplicationUser newReporter = thisIssue.getCustomFieldValue(raiseOnBehalfOfField) as ApplicationUser

if (!newReporter) { return "No new reporter specified." }

ArrayList<ApplicationUser> requestParticipants = []
requestParticipants.add(thisReporter)

thisIssue.setCustomFieldValue(requestParticipantsField, requestParticipants)
thisIssue.setReporter(newReporter)
thisIssue.setCustomFieldValue(raiseOnBehalfOfField, null)

// Store to DB.
try {
	ComponentAccessor.getIssueManager().updateIssue(
		currentUser,
		thisIssue,
		EventDispatchOption.ISSUE_UPDATED,
		false
	)
} catch (Exception e) {
	log.debug "Exception: " + e
}