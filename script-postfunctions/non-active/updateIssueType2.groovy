/*
*   Name:       mergeIssues.groovy
*   Author:     Evan Winter
*   
*   @brief Grabs description and comments from specified issue(s) and adds them to the 
*			current issue.
*
*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.Issue
import java.util.ArrayList
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.link.IssueLinkManager
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.comments.CommentManager
import java.text.SimpleDateFormat

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

/* Initialize variables */
MutableIssue thisIssue = issue
ApplicationUser loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

def description = thisIssue.getDescription()