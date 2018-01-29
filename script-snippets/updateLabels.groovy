/*
*	@name	updateLabels.groovy
*	@type	script snippet
*	@brief	Add labels to a custom field of type Labels for a specified issue.
*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.label.LabelManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.fields.CustomField

/* Debugging */
import org.apache.log4j.Logger
Logger log = Logger.getLogger("com.acme.XXX")

LabelManager labelManager = ComponentAccessor.getComponent(LabelManager)
ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject('ISSUE-100')

if (issue) {
	CustomField customLabelField = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectByName('AT Labels')
	if (customLabelField) {
		def customLabels = labelManager.getLabels(issue.id, customLabelField.getIdAsLong()).collect{ it.getLabel() }
	    customLabels += 'your-new-label'
	    labelManager.setLabels(user, issue.id, customLabelField.getIdAsLong(), customLabels.toSet(), false, false)
        return "No errors occurred while updating labels."
	} else {
	    log.debug('customLabelField is null, meaning the field name referenced is not valid.')
	    return "Couldn't find the field you're looking for. Please contact a JIRA administrator."
	}   
} else {
	log.debug('issue is null, meaning the issue key referenced is not valid.')
	return "There was an error finding this Issue. Please contact a JIRA administrator."
}