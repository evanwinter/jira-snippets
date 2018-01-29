/*
*	@name	getAllUsersNotInGroup.groovy
*	@type	script snippet
*	@brief	Get all users in this instance of JIRA that aren't in a specified group, and print out each username.
*/

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.security.groups.GroupManager

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

UserManager userManager = ComponentAccessor.getUserManager()
GroupManager groupManager = ComponentAccessor.getGroupManager()

userManager.getAllApplicationUsers().each{ u ->
    if (groupManager.isUserInGroup(u.username, 'group-name')) {
    	// Do something with each user.
    	log.debug u.username
    }
}