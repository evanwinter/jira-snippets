/*
*	@name 	lastCommentTrucated_prod.groovy
*	@type 	Script Field	
*	@brief 	Renders the last comment added to this issues, truncated to 140 chars.
*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

def loggedInUser = ComponentAccessor.jiraAuthenticationContext.getLoggedInUser()
Issue thisIssue = issue

int START = 0
def TARGET_LENGTH = 140

def comments = ComponentAccessor.getCommentManager().getCommentsForUser(thisIssue, loggedInUser)

if (!comments || comments.size() == 0) { return }

def lastComment = comments.last()
def lastCommentClean = lastComment.body.replace("\n", " ")
def commenterName = lastComment.getAuthorApplicationUser().displayName

boolean isTooLong = (lastComment.body.length() > TARGET_LENGTH)

def newComment = (isTooLong) ? lastCommentClean.substring(START, TARGET_LENGTH) + "..." : lastCommentClean

ComponentAccessor.getRendererManager().getRenderedContent(
	"atlassian-wiki-renderer",
	"*" + commenterName + "*: " + newComment,
	thisIssue.issueRenderContext
)