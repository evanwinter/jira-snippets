import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.MutableIssue
import java.text.SimpleDateFormat

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

/* Get timestamp */
def date = new Date()
def sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
def formattedDate = sdf.format(date)

// The current issue
MutableIssue mergingIssue = ComponentAccessor.getIssueManager().getIssueObject('AT-3082')

CustomField mainIssueField = ComponentAccessor.getCustomFieldManager().getCustomFieldObject('customfield_12108')
String mainIssueValue = mergingIssue.getCustomFieldValue(mainIssueField)
MutableIssue mainIssue = ComponentAccessor.getIssueManager().getIssueObject(mainIssueValue)

// Merge styles
String mergeHeader = '\n\n*MERGE* ' + mergingIssue.getKey() + ' | ' + formattedDate + '\n---------------------------------------\n'

// Merge descriptions
String description = mainIssue.getDescription()
description += (mergeHeader + mergingIssue.getDescription())
mainIssue.setDescription(description)

// Merge comments
def mergingIssueComments = ComponentAccessor.getCommentManager().getComments(mergingIssue)
for (comment in mergingIssueComments) {
    log.debug comment.getBody()
}