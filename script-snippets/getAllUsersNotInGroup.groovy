import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.security.groups.GroupManager

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

UserManager userManager = ComponentAccessor.getUserManager()
GroupManager groupManager = ComponentAccessor.getGroupManager()

userManager.getAllApplicationUsers().each{ u ->
    if (groupManager.isUserInGroup(u.username, 'netid-users')) {
    	log.debug u.username
    }
}