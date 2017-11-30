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
import java.util.ArrayList
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLinkManager
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.comments.CommentManager
import java.text.SimpleDateFormat

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

/* Initialize variables */
MutableIssue thisIssue = issue
ApplicationUser loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

def description = thisIssue.getDescription()
def requestParticipantsField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject("customfield_10600")
def requestParticipants = thisIssue.getCustomFieldValue(requestParticipantsField) as ArrayList<ApplicationUser>
def linkedIssues = ComponentAccessor.getIssueLinkManager().getOutwardLinks(thisIssue.getId())

def date = new Date()
def sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
def formattedDate = sdf.format(date)
    
if (linkedIssues) {
	description += (
		'\n\n-------------------\n' + 
		'*MERGE - ' + formattedDate + '*\n\n'
	)
	for (linkedIssue in linkedIssues) {
        def iss = linkedIssue.getDestinationObject()
       	description += (
       		iss.getKey() + ' - ' + iss.getSummary() + '\n' + 
       		iss.getDescription() + '\n\n'
       	)
		def commentsList = ComponentAccessor.getCommentManager().getComments(iss)
		if (commentsList.size() > 0) {
			String comments = '*MERGE* ' + formattedDate + ' - ' + iss.getKey()
			for (comment in commentsList) {
	   			comments += ('\n\n*' + comment.getAuthorFullName() + '*: ' + comment.getBody() + '\n_' + comment.getCreated() + '_')
			}
			ComponentAccessor.getCommentManager().create(thisIssue, loggedInUser, comments, false)
		}
		ApplicationUser reporter = iss.getReporter()
		if (reporter) {
			if (reporter != thisIssue.getReporter()) {
				requestParticipants.add(reporter)
			}
		} else {
			log.debug "That issue doesn't have a valid reporter!\n"
		}
	}
    thisIssue.setDescription(description)
    thisIssue.setCustomFieldValue(requestParticipantsField, requestParticipants)
} else {
    log.debug "No linked issues."
}

try {
	ComponentAccessor.getIssueManager().updateIssue(loggedInUser, thisIssue, EventDispatchOption.ISSUE_UPDATED, false)
} catch (Exception e) {
    log.debug "Exception: " + e
    return 'An error occured. Please contact your JIRA administrator.'
}