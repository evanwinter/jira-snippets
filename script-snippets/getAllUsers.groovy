import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserManager

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

UserManager userManager = ComponentAccessor.getUserManager()

userManager.getAllApplicationUsers().each{ u ->
    log.debug u.username
}