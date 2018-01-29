/*
*	@name	getLinkedIssues.groovy
*	@type	script snippet
*	@brief	Get all users in this instance of JIRA and print out each username.
*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserManager

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

UserManager userManager = ComponentAccessor.getUserManager()
userManager.getAllApplicationUsers().each{ u ->
    log.debug u.username
}