import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.MutableIssue
import java.util.ArrayList
import com.atlassian.jira.user.ApplicationUser

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

/* Initialize variables */
ApplicationUser currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
MutableIssue thisIssue = issue

/*  */
def requestParticipantsField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10600")
def raiseOnBehalfOfField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_12131")

ApplicationUser thisReporter = thisIssue.getReporter()
ApplicationUser newReporter = thisIssue.getCustomFieldValue(raiseOnBehalfOfField) as ApplicationUser

ArrayList<ApplicationUser> requestParticipants = []
requestParticipants.add(thisReporter)

/* Check if the "Raise on behalf of" field is set */
if (!newReporter) { return "No new reporter specified." }

/* Add original Reporter to Request Participants */
thisIssue.setCustomFieldValue(requestParticipantsField, requestParticipants)

/* Add specified user as new Reporter */
thisIssue.setReporter(newReporter)

/* Clear the "Raise this request on behalf of" field */
thisIssue.setCustomFieldValue(raiseOnBehalfOfField, null)

/* Store to DB */
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