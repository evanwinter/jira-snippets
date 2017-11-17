/*
*   Name:       updateIssueTypeAT.groovy
*   Author:     Evan Winter
*   
*   @brief Sets issue type during issue creation, based on customer input.
*
*/

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

//MutableIssue thisIssue = issue
MutableIssue thisIssue = issueManager.getIssueObject("AT-12")
ApplicationUser loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

/* Determine Customer Request Type */
def customerRequestTypeField = customFieldManager.getCustomFieldObjectByName("Customer Request Type")
log.debug "Customer Request Type Field: " + customerRequestTypeField
def customerRequestTypeKey = thisIssue.getCustomFieldValue(customerRequestTypeField)
log.debug "Customer Request Type Key: " + customerRequestTypeKey
def customerRequestType = getCustomerRequestTypeFromKey(customerRequestTypeKey)
log.debug "Customer Request Type: " + customerRequestType

/* Determine if current issue type matchings desired issue type */
def currentIssueTypeName = thisIssue.getIssueType().getName() as String
log.debug "Current Issue Type: " + currentIssueTypeName
def desiredIssueTypeFieldName = getIssueTypeFieldIDFromRequestType(customerRequestType) as String
log.debug "Desired Issue Type Field Name: " + desiredIssueTypeFieldName
def desiredIssueTypeField = customFieldManager.getCustomFieldObject(desiredIssueTypeFieldName)
log.debug "Desired Issue Type Field: " + desiredIssueTypeField
def desiredIssueTypeName = thisIssue.getCustomFieldValue(desiredIssueTypeField) as String       // Got a NullPointer here. Maybe the RT IDs are different?
log.debug "Desired Issue Type: " + desiredIssueTypeName
def desiredIssueType = ComponentAccessor.issueTypeSchemeManager.getIssueTypesForProject(thisIssue.getProjectObject()).find {
    it.getName() == desiredIssueTypeName
}

if (currentIssueTypeName != desiredIssueTypeName) {
    if (desiredIssueType) {
        log.debug "Setting issue type to " + desiredIssueTypeName + "..."
        thisIssue.setIssueType(desiredIssueType)
        storeChanges(issueManager, loggedInUser, thisIssue)
    } else {
        log.debug "Couldn't find matching issue type. Please ensure that the value selected in the [AT] Issue Type dropdown is identical to an issue type in this project."
    }
} else {
    log.debug "Issue type is already correct."
}

/* Methods */
def getCustomerRequestTypeFromKey(key) {
    def ANALYTICS_REQUESTTYPE_FIELD = "at/2cdf64db-3f5c-402a-91ae-28cf3b0d0a86"
    def AVSUPPORT_REQUESTTYPE_FIELD = " at/ee716c88-d729-466f-a50f-035aba1b33dd"
    def COURSEWEBSITESUPPORT_REQUESTTYPE_FIELD = "at/d0c946ed-f60c-467b-9e22-dd3330a731c2"
    def MEDIAPRODUCTION_REQUESTTYPE_FIELD = "at/fdb2594b-e9a6-407b-bda0-277df2b7084f"
    def TRAINING_REQUESTTYPE_FIELD = "at/35e2ee3f-90eb-4617-be7b-226b7ec82738"
    def result
    log.debug key
    switch (key) {
        case ANALYTICS_REQUESTTYPE_FIELD as String:
            result = "Analytics"
            break
        case AVSUPPORT_REQUESTTYPE_FIELD as String:
            result = "AV Support"
            break
        case COURSEWEBSITESUPPORT_REQUESTTYPE_FIELD as String:
            result = "Course Website Support"
            break
        case MEDIAPRODUCTION_REQUESTTYPE_FIELD as String:
            result = "Media Production"
            break
        case TRAINING_REQUESTTYPE_FIELD as String:
            result = "Training"
            break
        default:
            log.debug "Request Type not found."
            break
    }
    log.debug "Request Type: " + result
    return result
}

def getIssueTypeFieldIDFromRequestType(requestType) {
    def ANALYTICS_ISSUETYPE_FIELD = "customfield_12101"
    def AVSUPPORT_ISSUETYPE_FIELD = "customfield_12102"
    def COURSEWEBSITESUPPORT_ISSUETYPE_FIELD = "customfield_12103"
    def MEDIAPRODUCTION_ISSUETYPE_FIELD = "customfield_12104"
    def TRAINING_ISSUETYPE_FIELD = "customfield_12105"
    def result
    log.debug requestType
    switch (requestType) {
        case "Analytics":
            result = ANALYTICS_ISSUETYPE_FIELD
            break
        case "AV Support":
            result = AVSUPPORT_ISSUETYPE_FIELD
            break
        case "Course Website Support":
            result = COURSEWEBSITESUPPORT_ISSUETYPE_FIELD
            break
        case "Media Production":
            result = MEDIAPRODUCTION_ISSUETYPE_FIELD
            break
        case "Training":
            result = TRAINING_ISSUETYPE_FIELD
            break
    }
    log.debug "Issue Type Field ID: " + result
    return result
}

def storeChanges(IssueManager issueManager, ApplicationUser loggedInUser, MutableIssue thisIssue) {
    try {
        issueManager.updateIssue(
            loggedInUser,
            thisIssue,
            EventDispatchOption.ISSUE_UPDATED,
            false
        )
    } catch (Exception e) {
        log.debug "Exception: " + e
    }
}