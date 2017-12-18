/*
*   @name   AT_updateIssueType_cascade_prod.groovy
*   @type   Script Listener
*   @brief  Updates Issue Type based on the child value of the cascading select
*           custom field "AT Issue Categories".
*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.event.type.EventDispatchOption

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()

MutableIssue thisIssue = issue
ApplicationUser loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
CustomField issueCategorizationsField = customFieldManager.getCustomFieldObject("customfield_12201")
Map issueCategorizationsValues = thisIssue.getCustomFieldValue(issueCategorizationsField) as Map

if (!issueCategorizationsValues) { return "No value set for Issue Categorizations." }

String level1 = issueCategorizationsValues.get(null)	// parent
String level2 = issueCategorizationsValues.get("1")		// child

def desiredIssueType = ComponentAccessor.issueTypeSchemeManager.getIssueTypesForProject(thisIssue.getProjectObject()).find {
	it.getName() as String == level2
}

if (desiredIssueType) {
	if (desiredIssueType.name != thisIssue.getIssueType().getName() as String) {
     	thisIssue.setIssueType(desiredIssueType)
    	storeChanges(thisIssue, loggedInUser)   
    } else { return "Issue type is already correctly set."}
} else { return "Invalid or nonexistent issue type."}

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