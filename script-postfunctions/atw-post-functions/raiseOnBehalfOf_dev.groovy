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
MutableIssue thisIssue = ComponentAccessor.getIssueManager().getIssueObject("AT-72")

/*  */
def REQUEST_PARTICIPANTS_FIELD = "customfield_10600"
def RAISE_ON_BEHALF_OF_FIELD = "customfield_12131"

ApplicationUser thisReporter = thisIssue.getReporter()
ApplicationUser newReporter = thisIssue.getCustomFieldValue(ComponentAccessor.getCustomFieldManager().getCustomFieldObject(RAISE_ON_BEHALF_OF_FIELD)) as ApplicationUser

ArrayList<ApplicationUser> requestParticipants = []
requestParticipants.add(thisReporter)

/* Check if the "Raise on behalf of" field is set */
if (!newReporter) { return "No new reporter specified." }

/* Add original Reporter to Request Participants */
thisIssue.setCustomFieldValue(ComponentAccessor.getCustomFieldManager().getCustomFieldObject(REQUEST_PARTICIPANTS_FIELD), requestParticipants)
log.debug "Added " + thisReporter.getName() + " to Request Participants"

/* Add specified user as new Reporter */
thisIssue.setReporter(newReporter)
log.debug "Set " + newReporter.getName() + " as the new Reporter"

/* Clear the "Raise this request on behalf of" field */
thisIssue.setCustomFieldValue(ComponentAccessor.getCustomFieldManager().getCustomFieldObject(RAISE_ON_BEHALF_OF_FIELD), null)
log.debug "Cleared the 'Raise on behalf of' field. New value: " + thisIssue.getCustomFieldValue(ComponentAccessor.getCustomFieldManager().getCustomFieldObject(RAISE_ON_BEHALF_OF_FIELD))

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