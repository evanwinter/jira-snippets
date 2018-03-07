import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption
import java.util.ArrayList

def issueFactory = ComponentAccessor.getIssueFactory()
def constantsManager = ComponentAccessor.getConstantsManager()
def issueManager = ComponentAccessor.getIssueManager()
def subTaskManager = ComponentAccessor.getSubTaskManager()

ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
MutableIssue parent = event.issue as MutableIssue
def type = parent.issueType.name

if (!(type == 'Onboarding' || type == 'Offboarding')) {
    return 'This script only runs on issues of Onboarding and Offboarding issue types.'
}

if (parent.getSubTaskObjects().size() != 0) {
    return 'This script only runs when the parent issue has no existing sub-tasks.'
}

def onboardChecklist = ["New Grainger account", "New or refreshed computer / extra software needed?", "Schedule onboard meeting", "Email address", "Create VOIP line", "Add to Wisclist", "Invite to service calendars", "Populate AD with email address, office number, title and phone number", "If Faculty or PhD, add research drive", "Send Follow-up Summary"]
def offboardChecklist = ["Disable Grainger account", "Remove from WiscLists", "Remove from service calendars", "Are they in LRM_Users group? If so, move ticket to LRM Team", "If faculty or lecturer > disable in Digital Measures: Joannie Bonazza", "Remove VoIP account"]
def checklist = (type == 'Onboarding') ? onboardChecklist : offboardChecklist

// For each item in the asserted checklist
for (task in checklist) {
    
    // Get an issue object for the new sub-task
    MutableIssue taskObj = issueFactory.getIssue()
    
    // Set necessary fields
    taskObj.setSummary(task)
    taskObj.setDescription('For issue ' + parent.getKey())
    taskObj.setParentObject(parent)
    taskObj.setProjectObject(parent.getProjectObject())
    
    // Make it a sub-task by setting the issue type ID
    taskObj.setIssueTypeId(constantsManager.getAllIssueTypeObjects().find{
        it.getName() == "Sub-task"
    }.id)
    
    Map<String,Object> newIssueParams = ["issue" : taskObj] as Map<String,Object>
    issueManager.createIssueObject(user, newIssueParams)
    subTaskManager.createSubTaskIssueLink(parent, taskObj, user)
    
}

// Store to DB.
try {
    issueManager.updateIssue(
        user,
        parent,
        EventDispatchOption.ISSUE_UPDATED,
        false
    )
} catch (Exception e) {
    log.debug "Exception: " + e
}