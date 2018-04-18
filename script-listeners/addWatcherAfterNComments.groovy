/*
*	@name 	addWatcherAfterNComments.groovy
*	@type 	Script Listener
*	@brief 	When a new comment is added, add Jonathan Wolf (jwolf5) as a Watcher 
					if the total number of PUBLIC comments is >= 4.
*/

import com.atlassian.jira.bc.issue.comment.property.CommentPropertyService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.issue.IssueEvent
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.comments.Comment
import com.atlassian.jira.issue.comments.CommentManager
import com.atlassian.jira.event.type.EventDispatchOption
import groovy.json.JsonSlurper

final SD_PUBLIC_COMMENT = "sd.public.comment"

def event = event as IssueEvent
def issue = event.issue as MutableIssue
log.debug(issue)

def user = event.getUser()
def comment = event.getComment()
def commentPropertyService = ComponentAccessor.getComponent(CommentPropertyService)

def isInternal = { Comment c ->
	def commentProperty = commentPropertyService.getProperty(user, c.id, SD_PUBLIC_COMMENT)
	.getEntityProperty().getOrNull()

	if (commentProperty) {
		def props = new JsonSlurper().parseText(commentProperty.getValue())
		props['internal'].toBoolean()
	}
	else {
		null
	}
}

def commentManager = ComponentAccessor.getCommentManager()
def watcherManager = ComponentAccessor.getWatcherManager()
def watcher = ComponentAccessor.getUserManager().getUserByName('jwolf5')
def admin = ComponentAccessor.getUserManager().getUserByName("fmoadmin")

log.debug "User to add as watcher: ${watcher}"
log.debug "User to make action: ${admin}"

if (comment) {

	// Count number of PUBLIC COMMENTS
	def numComments = 0
	def comments = commentManager.getComments(event.issue)
	for (c in comments) {
		if (!(isInternal(c))) {
			numComments++
		}
	}

	log.debug "Number of comments: ${numComments}"

	// If 4 or more PUBLIC COMMENTS and the selected user isn't already a Watcher,
	// add them as a Watcher.
	if (numComments >= 4) {
		if (!watcherManager.isWatching(watcher, issue)) {
			log.debug "Adding ${watcher} as a Watcher."
			watcherManager.startWatching(watcher, issue)
			try {
				ComponentAccessor.getIssueManager().updateIssue(admin, issue, EventDispatchOption.ISSUE_UPDATED, false)
				return true;
			}
			catch (Exception e) {
				return 'An error occurred: ' + e + '\n\nPlease contact your JIRA administrator.'
			}
		}
	}
}
return false