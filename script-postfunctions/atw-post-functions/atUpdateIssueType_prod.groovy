import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

/* Establish context -- current issue and current logged in user */
MutableIssue thisIssue = issue
ApplicationUser loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

/* Get value of each AT Issue Type field. Only one will have a value. */
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()

def analyticsField = customFieldManager.getCustomFieldObject("customfield_12101")
def analyticsValue = thisIssue.getCustomFieldValue(analyticsField) as String

def avSupportField = customFieldManager.getCustomFieldObject("customfield_12102")
def avSupportValue = thisIssue.getCustomFieldValue(avSupportField) as String

def courseWebsiteSupportField = customFieldManager.getCustomFieldObject("customfield_12103")
def courseWebsiteSupportValue = thisIssue.getCustomFieldValue(courseWebsiteSupportField) as String

def mediaProductionField = customFieldManager.getCustomFieldObject("customfield_12104")
def mediaProductionValue = thisIssue.getCustomFieldValue(mediaProductionField) as String

def trainingField = customFieldManager.getCustomFieldObject("customfield_12105")
def trainingValue = thisIssue.getCustomFieldValue(trainingField) as String

def initIssueTypeName = thisIssue.getIssueType().getName() as String
def desiredIssueTypeName = ''

/* Determine which field has a value and get the value (desired Issue Type) */
if (analyticsValue) {
    desiredIssueTypeName = analyticsValue
    thisIssue.setCustomFieldValue(analyticsField, null)
} else if (avSupportValue) {
    desiredIssueTypeName = avSupportValue
    thisIssue.setCustomFieldValue(avSupportField, null)
} else if (courseWebsiteSupportValue) {
    desiredIssueTypeName = courseWebsiteSupportValue
    thisIssue.setCustomFieldValue(courseWebsiteSupportField, null)
} else if (mediaProductionValue) {
    desiredIssueTypeName = mediaProductionValue
    thisIssue.setCustomFieldValue(mediaProductionField, null)
} else if (trainingValue) {
    desiredIssueTypeName = trainingValue
    thisIssue.setCustomFieldValue(trainingField, null)
} else {
    log.debug "No match found."
}

/* Get the actual Issue Type object whose name is equal to the value in the AT Issue Type field */
def desiredIssueType = ComponentAccessor.issueTypeSchemeManager.getIssueTypesForProject(thisIssue.getProjectObject()).find {
    it.getName() as String == desiredIssueTypeName
}

if (desiredIssueType) {
	if (initIssueTypeName != desiredIssueTypeName) {
        thisIssue.setIssueType(desiredIssueType)
        storeChanges(thisIssue, loggedInUser)
        return "Successfully updated issue type to " + desiredIssueTypeName
	} else {
        return "Issue type is already set correctly."
	}
} else {
    return "Error determining a new issue type."
}

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