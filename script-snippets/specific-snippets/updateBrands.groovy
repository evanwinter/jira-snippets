/*
*   @name   updateBrands.groovy
*   @type   Script snippet
*   @brief  For every issue matching a JQL query, copy the value in "Brand" to the custom label field "Brand (ATW)".
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

JqlQueryParser jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
SearchProvider searchProvider = ComponentAccessor.getComponent(SearchProvider)
IssueManager issueManager = ComponentAccessor.getIssueManager()
ApplciationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

LabelManager labelManager = ComponentAccessor.getComponent(LabelManager)

String queryString = "issuetype = 'ATW Equipment' and brand is not empty"

def query = jqlQueryParser.parseQuery(queryString)
def results = searchProvider.search(query, user, PagerFilter.getUnlimitedFilter())
log.debug("Total issues: ${results.total}")

results.getIssues().each {documentIssue ->
    
    MutableIssue issue = issueManager.getIssueObject(documentIssue.id)
    String brand = issue.getCustomFieldValue(ComponentAccessor.getCustomFieldManager().getCustomFieldObjectByName('Brand'))
    
    def brandLabelField = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectByName('Brand (ATW)')
    def brandLabels = labelManager.getLabels(issue.id, brandLabelField.getIdAsLong()).collect{ it.getLabel() }
    
    brandLabels += brand

    labelManager.setLabels(user, issue.id, brandLabelField.getIdAsLong(), brandLabels.toSet(), false, false)
    // storeChanges(issue, user)
    
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