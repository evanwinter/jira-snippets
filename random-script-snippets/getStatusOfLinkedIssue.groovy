import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.CustomFieldManager;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.UpdateIssueRequest

MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject('ATW-28')

IssueManager issueManager = ComponentAccessor.getIssueManager();
CustomFieldManager customFieldManager = ComponentAccessor.getCustomFieldManager();

def statusName = ComponentAccessor.issueLinkManager.getInwardLinks(issue.getId())[0].getSourceObject().getStatus().getName()

return statusName

// For LRM cloning process
// 	When Issue Updated
//		If LRMIssue.status = "Closed"
//			ITSLRMIssue.update 
// OR in post function
//	When transition to Closed
//		update linked issue accordingly