/*
*   @name   bulkChangeInProject.groovy
*   @type   Script snippet
*   @brief  Make a bulk change to issues in a project.
*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.jira.issue.label.LabelManager
import com.atlassian.jira.issue.label.Label
import java.util.Set

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
def searchProvider = ComponentAccessor.getComponent(SearchProvider)
def issueManager = ComponentAccessor.getIssueManager()
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

// Get desired Issue Type.
def desiredIssueType = ComponentAccessor.issueTypeSchemeManager.getIssueTypesForProject(ComponentAccessor.getProjectManager().getProjectObjByKey('AT')).find {
	it.getName() as String == 'AT'
}

// Search for issues using a JQL query.
def query = jqlQueryParser.parseQuery("project = AT and issuetype = 'ATW Equipment' and brand is not empty")
def results = searchProvider.search(query, user, PagerFilter.getUnlimitedFilter())
log.debug("Total issues: ${results.total}")

results.getIssues().each {documentIssue ->
    
    def issue = issueManager.getIssueObject(documentIssue.id)
    def brand = issue.getCustomFieldValue(ComponentAccessor.getCustomFieldManager().getCustomFieldObject('customfield_12116'))
    
    def brandLabelField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject('customfield_12303')
    Set<Label> brandLabelValue = (ComponentAccessor.getComponent(LabelManager.class).getLabels(issue.id, 12303 as Long))
    brandLabelValue.add(brand)
    
    log.debug brandLabelValue

    // Make changes to each issue here.
}

// Persist changes in the database.
def storeChanges(MutableIssue issue, ApplicationUser user) {
    try {
        ComponentAccessor.getIssueManager().updateIssue(
            user,
            issue,
            EventDispatchOption.ISSUE_UPDATED,
            false
        )
    } catch (Exception e) {
        log.debug "Exception: " + e
    }
}