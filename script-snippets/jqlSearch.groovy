import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.web.bean.PagerFilter

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

def searchQuery = 'project = AT and issuetype = "ATW Equipment"'
SearchService searchService = ComponentAccessor.getComponent(SearchService.class)

def issues = []

SearchService.ParseResult parseResult = searchService.parseQuery(user, searchQuery)
if (parseResult.isValid()) {
    def searchResult = searchService.search(user, parseResult.getQuery(), PagerFilter.getUnlimitedFilter())
    issues = searchResult.issues.collect {ComponentAccessor.getIssueManager().getIssueObject(it.id)}
    
    for (issue in issues) {
        log.debug(issue.getKey())
    }
    
    log.debug (issues.size() + ' issues')
    
} else {
    log.error('Invalid JQL: ' + searchQuery)
}