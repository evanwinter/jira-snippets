import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import java.util.ArrayList
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.index.IssueIndexManager
import org.apache.log4j.Logger
import org.apache.log4j.Level
def log = Logger.getLogger("com.acme.XXX")
log.setLevel(Level.DEBUG)

def PRIMARY_USER_FIELD = "customfield_10913"
def REQUEST_PARTICIPANTS_FIELD = "customfield_10600"

def customFieldManager = ComponentAccessor.getCustomFieldManager()
def userManager = ComponentAccessor.getUserUtil()
def issueManager = ComponentAccessor.getIssueManager())

MutableIssue issue = issueManager.getIssueObject()

def primaryUserField = customFieldManager.getCustomFieldObject(PRIMARY_USER_FIELD)
def requestParticipantsField = customFieldManager.getCustomFieldObject(REQUEST_PARTICIPANTS_FIELD)

def primaryUserName = issue.getCustomFieldValue(primaryUserField) as String
ApplicationUser user = userManager.getUserByName(primaryUser)

thisIssue.setCustomFieldValue(requestParticipantsField, user)

issue.getCustomFieldValue(componentManager.getCustomFieldManager().getCustomFieldObject("customfield_190"))?.displayName

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser
import java.util.ArrayList
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.issue.util.DefaultIssueChangeHolder
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.index.IssueIndexManager
import org.apache.log4j.Logger
import org.apache.log4j.Level
def log = Logger.getLogger("com.acme.XXX")
log.setLevel(Level.DEBUG)

def PRIMARY_USER_FIELD = "customfield_10913"
def REQUEST_PARTICIPANTS_FIELD = "customfield_10600"

def customFieldManager = ComponentAccessor.getCustomFieldManager()
def userManager = ComponentAccessor.getUserUtil()
def issueManager = ComponentAccessor.getIssueManager()

MutableIssue issue = issueManager.getIssueObject("ATW-31")

def requestParticipantsField = customFieldManager.getCustomFieldObject(REQUEST_PARTICIPANTS_FIELD)


def primaryUserField = customFieldManager.getCustomFieldObject(PRIMARY_USER_FIELD)
def primaryUserName = issue.getCustomFieldValue(primaryUserField)

ApplicationUser newReporter = userManager.getUserByName("ewinter_wsb")

log.debug "PRIMARY USER FIELD: " + primaryUserField
log.debug "PRIMARY USER NAME: " + primaryUserName

ApplicationUser oldReporter = issue.getReporter()

if (newReporter) {
	try {
	//  issue.setCustomFieldValue(requestParticipantsField, reporter) 
   		log.debug newReporter.getName()
    	issue.setReporter(newReporter)
	} catch (Exception e) {
    	log.debug "Error"
    	log.debug e as String
	}
} else { log.debug "newReporter is null." }
