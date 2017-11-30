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
    
if (linkedIssues) {
	for (linkedIssue in linkedIssues) {
        def iss = linkedIssue.getDestinationObject()
       	description += ('\n\n-------------------\n' + '*FROM MERGE:* ' + iss.getKey() + ' - ' + iss.getSummary() + '\n' + iss.getDescription() + '\n')
		def commentsList = ComponentAccessor.getCommentManager().getComments(iss)
		if (commentsList.size() > 0) {
			String comments = '*FROM MERGE:* ' + iss.getKey() + ' comments'
			for (comment in commentsList) {
	   			comments += ('\n\n*' + comment.getAuthorFullName() + ' (' + comment.getCreated() + ')*: ' + comment.getBody())
			}
			ComponentAccessor.getCommentManager().create(thisIssue, loggedInUser, comments, false)
		}
		ApplicationUser reporter = iss.getReporter()
		if (reporter) {
			requestParticipants.add(reporter)
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