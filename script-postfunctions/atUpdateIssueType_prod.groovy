import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

/* Set up Managers */
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()

MutableIssue thisIssue = issue
ApplicationUser loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

def ANALYTICS_ISSUETYPE_FIELD = "customfield_12101"
def analyticsField = customFieldManager.getCustomFieldObject(ANALYTICS_ISSUETYPE_FIELD)
def analyticsValue = thisIssue.getCustomFieldValue(analyticsField) as String

def AVSUPPORT_ISSUETYPE_FIELD = "customfield_12102"
def avSupportField = customFieldManager.getCustomFieldObject(AVSUPPORT_ISSUETYPE_FIELD)
def avSupportValue = thisIssue.getCustomFieldValue(avSupportField) as String

def COURSEWEBSITESUPPORT_ISSUETYPE_FIELD = "customfield_12103"
def courseWebsiteSupportField = customFieldManager.getCustomFieldObject(COURSEWEBSITESUPPORT_ISSUETYPE_FIELD)
def courseWebsiteSupportValue = thisIssue.getCustomFieldValue(courseWebsiteSupportField) as String

def MEDIAPRODUCTION_ISSUETYPE_FIELD = "customfield_12104"
def mediaProductionField = customFieldManager.getCustomFieldObject(MEDIAPRODUCTION_ISSUETYPE_FIELD)
def mediaProductionValue = thisIssue.getCustomFieldValue(mediaProductionField) as String

def TRAINING_ISSUETYPE_FIELD = "customfield_12105"
def trainingField = customFieldManager.getCustomFieldObject(TRAINING_ISSUETYPE_FIELD)
def trainingValue = thisIssue.getCustomFieldValue(trainingField) as String

def initIssueTypeName = thisIssue.getIssueType().getName() as String
def desiredIssueTypeName = ''

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