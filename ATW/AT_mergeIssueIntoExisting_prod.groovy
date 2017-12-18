/*
*	@name 	AT_mergeIssueIntoExisting_prod.groovy
*	@type	Post function
*	@brief 	Merges current issue into target issue by appending the current issue's
*			description, comments and Reporter to the target issue's corresponding
*			fields. The current issue's Reporter is added to the target issue as
*			a Request Participant.
*/

import com.atlassian.jira.component.ComponentAccessor
import java.text.SimpleDateFormat
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import java.util.ArrayList
import com.atlassian.jira.event.type.EventDispatchOption

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

/* Get timestamp */
def date = new Date()
def sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
def formattedDate = sdf.format(date)

/* Initialize variables */
MutableIssue thisIssue = issue
ApplicationUser loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

def linkedIssues = ComponentAccessor.getIssueLinkManager().getOutwardLinks(thisIssue.getId())
MutableIssue mainIssue = linkedIssues[0].getDestinationObject() as MutableIssue

/* Merge descriptions (this -> main) */
def mainDescription = mainIssue.getDescription()
def thisDescription = thisIssue.getDescription()
mainDescription += ('\n\n *MERGE* ' + thisIssue.getKey() + ' | ' + formattedDate + '\n----- \n' + thisDescription)
mainIssue.setDescription(mainDescription)

/* Merge comments (this -> main) */ 
def thisComments = ComponentAccessor.getCommentManager().getComments(thisIssue)
if (thisComments.size() > 0) {
	def newMainComment = '*MERGE* ' + thisIssue.getKey() + ' | ' + formattedDate + '\n----- \n'
	for (comment in thisComments) {
		newMainComment += ('*' + comment.getAuthorFullName() + '*: ' + comment.getBody() + '\n_' + comment.getCreated() + '_\n')
	}
	ComponentAccessor.getCommentManager().create(mainIssue, loggedInUser, newMainComment, false)
}

/* Merge participants (this Reporter -> main Request Participants) */
ApplicationUser thisReporter = thisIssue.getReporter()
def mainRequestParticipantsField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10600")
ArrayList<ApplicationUser> mainRequestParticipants = mainIssue.getCustomFieldValue(mainRequestParticipantsField) as ArrayList<ApplicationUser>
mainRequestParticipants.add(thisReporter)
mainIssue.setCustomFieldValue(mainRequestParticipantsField, mainRequestParticipants)

/* Comment on this issue to tag as merged */
ComponentAccessor.getCommentManager().create(thisIssue, loggedInUser, "MERGED into " + mainIssue.getKey(), false)

/* Persist changes */
try {
	ComponentAccessor.getIssueManager().updateIssue(
		loggedInUser,
		mainIssue,
		EventDispatchOption.ISSUE_UPDATED,
		false
	)
} catch (Exception e) {
    log.debug "Exception: " + e
    return 'An error occured. Please contact your JIRA administrator.'
}