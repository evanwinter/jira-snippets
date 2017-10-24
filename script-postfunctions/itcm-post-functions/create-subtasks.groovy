////////////////////////////////////////////////////////////////////////////////
//
//	Name: create-subtasks.groovy	
//
//	Location: Workflows > ITCM Main Request Workflow > (Transition: Create) && (Transition: Re-submit) > 
//		Add Post Function > Script Post-Function > Custom script post-function
//
//	Description: Creates one subtask for each user in Approver Group.
//
////////////////////////////////////////////////////////////////////////////////

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue 
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.crowd.embedded.api.Group
import com.atlassian.jira.user.ApplicationUsers
import com.atlassian.jira.user.util.UserUtil
import com.atlassian.crowd.embedded.api.User
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.XXX")
log.setLevel(Level.DEBUG)

Issue parent = issue

// don't run on subtasks
if (parent.isSubTask()) {
	log.debug "Sub-task ignored."
	return
}

def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

// define managers
def cfManager = ComponentAccessor.getCustomFieldManager()
def groupManager = ComponentAccessor.getGroupManager()
def issueManager = ComponentAccessor.getIssueManager()
def issueFactory = ComponentAccessor.getIssueFactory()
def subTaskManager = ComponentAccessor.getSubTaskManager()
def constantsManager = ComponentAccessor.getConstantsManager()

// get information about the approver group
def approversCf = cfManager.getCustomFieldObjectByName("Approver Group")
def approvers = issue.getCustomFieldValue(approversCf)
def approversName = approvers ? approvers[0].name : log.debug("Approver Group is null.")
int approversSize = groupManager.getUsersInGroup(approversName).size()

def assignee
def assigneeName

// define custom fields so they can be copied from parent and set for subtasks later
def category = cfManager.getCustomFieldObject('customfield_11401')
def impact = cfManager.getCustomFieldObject('customfield_10804')
def risk = cfManager.getCustomFieldObject('customfield_10806')
def changeStartDate = cfManager.getCustomFieldObject('customfield_10808')
def changeEndDate = cfManager.getCustomFieldObject('customfield_10809')
def businessCase = cfManager.getCustomFieldObject('customfield_11500')
def changeType = cfManager.getCustomFieldObject('customfield_10805')

// for each user in the approver group
for (int i = 0; i < approversSize; i++) {
    
    assignee = groupManager.getUsersInGroup(approvers).get(i).getName()
    assigneeName = groupManager.getUsersInGroup(approvers).get(i).getDisplayName()

    log.debug "Creating subtask for " + assigneeName
    
    // create a new mutable issue (soon to be a subtask)
    MutableIssue newSubTask = issueFactory.getIssue()

    // set summary and copy fields from parent to each subtask
    newSubTask.setSummary(assigneeName+": Approval requested")
    newSubTask.setDescription("*"+parent.getSummary()+"* \n\n" + parent.getDescription())
	newSubTask.setAssigneeId(assignee)
	newSubTask.setReporter(user)
	newSubTask.setParentObject(parent)
	newSubTask.setProjectObject(parent.getProjectObject())

	// copy custom fields from parent to each subtask
	newSubTask.setCustomFieldValue( category, issue.getCustomFieldValue(category) )
	newSubTask.setCustomFieldValue( impact, issue.getCustomFieldValue(impact) )
	newSubTask.setCustomFieldValue( risk, issue.getCustomFieldValue(risk) )
	newSubTask.setCustomFieldValue( changeStartDate, issue.getCustomFieldValue(changeStartDate) )
	newSubTask.setCustomFieldValue( changeEndDate, issue.getCustomFieldValue(changeEndDate) )
	newSubTask.setCustomFieldValue( businessCase, issue.getCustomFieldValue(businessCase) )
	newSubTask.setCustomFieldValue( changeType, issue.getCustomFieldValue(changeType) )
    
    // set this issue's Issue Type ID to that of a sub-task
    newSubTask.setIssueTypeId(constantsManager.getAllIssueTypeObjects().find{
		it.getName() == "Sub-task"
	}.id)

	Map<String,Object> newIssueParams = ["issue" : newSubTask] as Map<String,Object>
	issueManager.createIssueObject(user, newIssueParams)
	subTaskManager.createSubTaskIssueLink(parent, newSubTask, user)

	log.debug "Subtask created for " + assigneeName
    
}