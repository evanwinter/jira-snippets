/*
*	@name 	ITCM_parentToDeniedStatus_prod.groovy
*	@type 	Post function
*	@brief 	If ANY of this issue's sibling subtasks are 'Denied', move its parent
* 			issue to status 'Denied'
*/


import com.opensymphony.workflow.WorkflowContext
import com.atlassian.jira.config.SubTaskManager
import com.atlassian.jira.workflow.WorkflowTransitionUtil
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.component.ComponentAccessor
import org.apache.log4j.Logger
import org.apache.log4j.Level

def log = Logger.getLogger("com.acme.XXX")
log.setLevel(Level.DEBUG)
 
String currentUser = ((WorkflowContext) transientVars.get("context")).getCaller()
WorkflowTransitionUtil workflowTransitionUtil = (WorkflowTransitionUtil) JiraUtils.loadComponent(WorkflowTransitionUtilImpl.class)
MutableIssue parent = issue.getParentObject() as MutableIssue
 
String originalParentStatus  = parent.getStatus().getSimpleStatus().getName()
def isWaitingForApproval = originalParentStatus in ['Waiting for approval']

// for current ITCM workflow
int denyID = 21
 
if (isWaitingForApproval) {
    workflowTransitionUtil.setIssue(parent)
    workflowTransitionUtil.setUserkey(currentUser)
    workflowTransitionUtil.setAction(denyID)    
    if (workflowTransitionUtil.validate()) {
        workflowTransitionUtil.progress()
    }
} else {
    log.debug("Not in Waiting for approval.")
}