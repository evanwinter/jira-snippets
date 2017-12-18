/*
*	@name 	descriptionTruncated_prod.groovy
*	@type 	Script Field	
*	@brief 	Renders a shortened and compacted version of the issue description. 
*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

def loggedInUser = ComponentAccessor.jiraAuthenticationContext.getLoggedInUser()
Issue thisIssue = issue
String description = thisIssue.getDescription().replace("\u00a0", "")	// get rid of extra newlines

int START = 0
def TARGET_LENGTH = 600

/* True if Description is longer than TARGET_LENGTH */
boolean isTooLong = (description.length() > TARGET_LENGTH)

def newDescription = (isTooLong) ? description.substring(START, TARGET_LENGTH) + '...' : description
ComponentAccessor.getRendererManager().getRenderedContent("atlassian-wiki-renderer", newDescription, thisIssue.issueRenderContext)