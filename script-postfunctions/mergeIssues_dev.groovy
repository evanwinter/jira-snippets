import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption
import java.text.SimpleDateFormat
import java.util.ArrayList

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

/* Get timestamp */
def date = new Date()
def sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
def formattedDate = sdf.format(date)

/* Initialize variables */
MutableIssue thisIssue = ComponentAccessor.getIssueManager().getIssueObject("AT-63")
ApplicationUser loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

def linkedIssues = ComponentAccessor.getIssueLinkManager().getOutwardLinks(thisIssue.getId())
MutableIssue mainIssue = linkedIssues[0].getDestinationObject() as MutableIssue

/* Append description */
def mainDescription = mainIssue.getDescription()
def thisDescription = thisIssue.getDescription()
mainDescription += ('\n\n *MERGE* ' + thisIssue.getKey() + ' | ' + formattedDate + '\n----- \n' + thisDescription)
mainIssue.setDescription(mainDescription)

/* Append comments */
def thisComments = ComponentAccessor.getCommentManager().getComments(thisIssue)
def newMainComment = '*MERGE* ' + thisIssue.getKey() + ' | ' + formattedDate + '\n----- \n'
for (comment in thisComments) {
	newMainComment += ('*' + comment.getAuthorFullName() + '*: ' + comment.getBody() + '\n_' + comment.getCreated() + '_\n')
}
ComponentAccessor.getCommentManager().create(mainIssue, loggedInUser, newMainComment, false)

/* Add this Reporter to main Request Participants */
ApplicationUser thisReporter = thisIssue.getReporter()
def mainRequestParticipantsField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10600")
ArrayList<ApplicationUser> mainRequestParticipants = mainIssue.getCustomFieldValue(mainRequestParticipantsField) as ArrayList<ApplicationUser>
mainRequestParticipants.add(thisReporter)
mainIssue.setCustomFieldValue(mainRequestParticipantsField, mainRequestParticipants)

try {
	ComponentAccessor.getIssueManager().updateIssue(loggedInUser, mainIssue, EventDispatchOption.ISSUE_UPDATED, false)
} catch (Exception e) {
    log.debug "Exception: " + e
    return 'An error occured. Please contact your JIRA administrator.'
}