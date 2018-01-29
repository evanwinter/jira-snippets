/*
*	@name	jqlSearch.groovy
*	@type	script snippet
*	@brief	Gets all issues which match a JQL query and print out each summary.
*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.MutableIssue
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

JqlQueryParser jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
SearchProvider searchProvider = ComponentAccessor.getComponent(SearchProvider)
IssueManager issueManager = ComponentAccessor.getIssueManager()
ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

// Replace this string with your desired JQL query.
String queryString = "assignee = currentUser()"

def query = jqlQueryParser.parseQuery(queryString)
def results = searchProvider.search(query, user, PagerFilter.getUnlimitedFilter())

log.debug("Returned ${results.total} issues that matched the query.")

if (results.total > 0) {
	results.getIssues().each {documentIssue ->
	    MutableIssue issue = issueManager.getIssueObject(documentIssue.id)
	    
		// Do something with each issue.
		log.debug("Summary: ${issue.summary}")

	}
} else {
	log.debug('No issues matched this query')
	return "No issues matched this query. Please try another or contact a JIRA administrator."
}

return "Execution completed without error."