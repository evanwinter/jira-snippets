/*
*	@name	getLinkedIssues.groovy
*	@type	script snippet
*	@brief	Get all Issues objects that are linked to a specified issue and print out each summary.
*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.CustomFieldManager

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject('ISSUE-100')
IssueManager issueManager = ComponentAccessor.getIssueManager()
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()

def linkedIssues = ComponentAccessor.issueLinkManager.getInwardLinks(issue.getId())
if (linkedIssues.size() > 0) {
	linkedIssues.each{ iss ->
	    def linkedIssue = issueManager.getIssueObject(iss.destinationId)

	    // Do something with each linked issue.
	    log.debug(linkedIssue.getSummary())

	}
	return true
} else {
	log.debug("No 'inward link' linked issues found.")
	return "This issue has no inward-linked issues. Please try a different issue or contact a JIRA administrator."
}