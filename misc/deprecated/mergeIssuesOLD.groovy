/*
*   Name:       mergeIssues.groovy
*   Author:     Evan Winter
*   
*   @brief Grabs description and comments from specified issue(s) and adds them to the 
*			current issue.
*
*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLinkManager
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.comments.CommentManager

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

/* Set up Managers */
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
CommentManager commentManager = ComponentAccessor.getCommentManager()
IssueManager issueManager = ComponentAccessor.getIssueManager()
IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager()

/* Initialize variables */
MutableIssue thisIssue = issue
ApplicationUser loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

mergeIssues(issueLinkManager, commentManager, thisIssue, loggedInUser)
storeChanges(issueManager, loggedInUser, thisIssue)

def mergeIssues(IssueLinkManager issueLinkManager, CommentManager commentManager, MutableIssue thisIssue, ApplicationUser loggedInUser) {
	def linkedIssues = issueLinkManager.getOutwardLinks(thisIssue.getId())
    def initialDescription = thisIssue.getDescription()
    def mergedDescriptions = ''
	if (linkedIssues) {
		/* For each valid issue in "Issues to merge" field */
		for (linkedIssue in linkedIssues) {
            def iss = linkedIssue.getDestinationObject()
			/* Append this issue description to main issue description */
	       	def mergedDescriptions = getMergedDescriptions(iss, initialDescription)
	       	/* Get all comments from this issue and add as single comment on main issue */
            mergeComments(thisIssue, iss, commentManager, loggedInUser)
            /* Add this issue's reporter to main issue's Request Participants */
            addToParticipants(thisIssue)
		}
		/* Actually update the main issue's description to the newly merged description */
        thisIssue.setDescription(mergedDescriptions)
	} else {
	    log.debug "No linked issues."
	}
}

String getMergedDescriptions(Issue iss, String desc) {
	desc += ('\n\n-------------------\n' +
                                '*FROM MERGE:* ' + iss.getKey() + ' - ' + iss.getSummary() + '\n' + 
	                          	iss.getDescription() + '\n')
	return desc
}

def mergeComments(MutableIssue thisIssue, Issue iss, CommentManager commentManager, ApplicationUser loggedInUser) {
	def commentsList = commentManager.getComments(iss)
	if (commentsList.size() > 0) {
		String comments = '*FROM MERGE:* ' + iss.getKey() + ' comments'
		for (comment in commentsList) {
		    comments += ('\n\n*' + comment.getAuthorFullName() + ' (' + comment.getCreated() + ')*: ' + comment.getBody())
		}
		commentManager.create(thisIssue, loggedInUser, comments, false)
	}
}

def addToParticipants(MutableIssue thisIssue) {
	def REQUEST_PARTICIPANTS_FIELD = "customfield_10600"
}

def storeChanges(IssueManager issueManager, ApplicationUser loggedInUser, MutableIssue thisIssue) {
    try {
        issueManager.updateIssue(
            loggedInUser,
            thisIssue,
            EventDispatchOption.ISSUE_UPDATED,
            false
        )
    } catch (Exception e) {
        log.debug "Exception: " + e
    }
}