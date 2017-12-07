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
IssueManager issueManager = ComponentAccessor.getIssueManager()

MutableIssue thisIssue = issueManager.getIssueObject("AT-57")
ApplicationUser loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

def ANALYTICS_ISSUETYPE_FIELD = "customfield_12101"
def analyticsValue = thisIssue.getCustomFieldValue(customFieldManager.getCustomFieldObject(ANALYTICS_ISSUETYPE_FIELD)) as String
log.debug(analyticsValue)

def AVSUPPORT_ISSUETYPE_FIELD = "customfield_12102"
def avSupportValue = thisIssue.getCustomFieldValue(customFieldManager.getCustomFieldObject(AVSUPPORT_ISSUETYPE_FIELD)) as String
log.debug(avSupportValue)

def COURSEWEBSITESUPPORT_ISSUETYPE_FIELD = "customfield_12103"
def courseWebsiteSupportValue = thisIssue.getCustomFieldValue(customFieldManager.getCustomFieldObject(COURSEWEBSITESUPPORT_ISSUETYPE_FIELD)) as String
log.debug(courseWebsiteSupportValue)

def MEDIAPRODUCTION_ISSUETYPE_FIELD = "customfield_12104"
def mediaProductionValue = thisIssue.getCustomFieldValue(customFieldManager.getCustomFieldObject(MEDIAPRODUCTION_ISSUETYPE_FIELD)) as String
log.debug(mediaProductionValue)

def TRAINING_ISSUETYPE_FIELD = "customfield_12105"
def trainingValue = thisIssue.getCustomFieldValue(customFieldManager.getCustomFieldObject(TRAINING_ISSUETYPE_FIELD)) as String
log.debug(trainingValue)

def initIssueTypeName = thisIssue.getIssueType().getName() as String
def desiredIssueTypeName = ''

if (analyticsValue) {
    log.debug "This is an Analytics request."
    desiredIssueTypeName = analyticsValue
} else if (avSupportValue) {
    log.debug "This is an AV Support request."
    desiredIssueTypeName = avSupportValue
} else if (courseWebsiteSupportValue) {
    log.debug "This is a Course Website Support request."
    desiredIssueTypeName = courseWebsiteSupportValue
} else if (mediaProductionValue) {
    log.debug "This is a Media Production request."
    desiredIssueTypeName = mediaProductionValue
} else if (trainingValue) {
    log.debug "This is a Training request."
    desiredIssueTypeName = trainingValue
} else {
    log.debug "No match found."
}

def desiredIssueType = ComponentAccessor.issueTypeSchemeManager.getIssueTypesForProject(thisIssue.getProjectObject()).find {
    it.getName() as String == desiredIssueTypeName
}

log.debug "Desired issue type: " + desiredIssueType.getName()
log.debug "Current issue type: " + initIssueTypeName

if (desiredIssueType) {
	if (initIssueTypeName != desiredIssueTypeName) {
    	log.debug "They don't match!"
        thisIssue.setIssueType(desiredIssueType)
        storeChanges(thisIssue, loggedInUser)
	} else {
    	log.debug "They match!"
        return "Issue type is set correctly."
	}
} else {
    log.debug "Error determining a new issue type."
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