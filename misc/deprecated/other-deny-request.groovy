////////////////////////////////////////////////////////////////////////////////
//
//	Name: deny-request.groovy
//
//	Location: Workflows > ITCM Approver Feedback Workflow > Transition: Denied
//		> Add Post Function > Script Post-Function > Custom script post-function 
//
//	Description: If any of an issue's sub-tasks are denied, execute the parent's "Deny" transition too.
//
////////////////////////////////////////////////////////////////////////////////

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
def isAwaitingApproval = originalParentStatus in ['Awaiting approval']

// for current ITCM workflow
int approveID = 21
 
if (isAwaitingApproval) {
    workflowTransitionUtil.setIssue(parent)
    workflowTransitionUtil.setUserkey(currentUser)
    workflowTransitionUtil.setAction(approveID)	
    if (workflowTransitionUtil.validate()) {
        workflowTransitionUtil.progress()
    }
}