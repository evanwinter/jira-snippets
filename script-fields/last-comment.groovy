////////////////////////////////////////////////////////////////////////////////
//
//	Location: Administration > Add-ons > Script Fields > Last Comment
//
//	Description: Custom field that holds the latest comment on an issue.
//
////////////////////////////////////////////////////////////////////////////////

import com.atlassian.jira.component.ComponentAccessor

def user = ComponentAccessor.jiraAuthenticationContext.getLoggedInUser()
def commentManager = ComponentAccessor.getCommentManager()
def rendererManager = ComponentAccessor.getRendererManager()

def lastComment
def commenterName
def formattedComment
int start = 0
int end

// get list of all visible comments
def comments = commentManager.getCommentsForUser(issue, user)

// render last comment if it exists
if (comments && comments.size() > 0) {

	// because comments are often email responses, get rid of newlines for readability
	lastComment = comments.last().body.replace("\n", " ")
	commenterName = comments.last().getAuthorApplicationUser().displayName
	// if longer than 140 chars, display only first 140 -- else, show full comment
	if (lastComment.length() > 140) {
		end = 140
		formattedComment = lastComment.substring(start, end) + '...'
	} else {
		end = lastComment.length()
		formattedComment = lastComment.substring(start, end)
	}

	rendererManager.getRenderedContent(
		"atlassian-wiki-renderer",
		"*" + commenterName + "*: " + formattedComment,
		issue.issueRenderContext)
}