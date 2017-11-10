////////////////////////////////////////////////////////////////////////////////
// 
//	File Name:      addReporterAsParticipant.groovy
//	Author:         Evan Winter
//	Context:        FES Facilities Service Request Workflow on create
//	Description:    Get current reporter, add them to "Request Participants."
//
////////////////////////////////////////////////////////////////////////////////

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.IssueManager
import java.util.ArrayList
import com.atlassian.jira.user.ApplicationUser
import org.apache.log4j.Logger
import org.apache.log4j.Level
def log = Logger.getLogger("com.acme.XXX")
log.setLevel(Level.DEBUG)

MutableIssue myIssue = issue
def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def REQUEST_PARTICIPANTS_FIELD = "customfield_10600"

CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager()
IssueManager issueManager = ComponentAccessor.getIssueManager()

// Get current reporter.
ApplicationUser reporter = myIssue.getReporter()

// Store in an ArrayList
ArrayList<ApplicationUser> participantsToAdd = []
participantsToAdd.add(reporter)

// Update the Request Participants field.
myIssue.setCustomFieldValue(
    customFieldManager.getCustomFieldObject(REQUEST_PARTICIPANTS_FIELD), 
    participantsToAdd
)

// Persist to database.
issueManager.updateIssue(
        currentUser,
        myIssue,
        EventDispatchOption.ISSUE_UPDATED,
        false
)