import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption

/* Debugging */
import org.apache.log4j.Logger
def log = Logger.getLogger("com.acme.XXX")

MutableIssue issue = ComponentAccessor.getIssueManager().getIssueObject(event.getIssue().getId())
ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

def userManager = ComponentAccessor.getUserManager()
ApplicationUser jenn = userManager.getUserByKey('asselin')

def watcherManager = ComponentAccessor.getWatcherManager()
if (!watcherManager.isWatching(jenn, issue)) {
    watcherManager.startWatching(jenn, issue)
    try {
		ComponentAccessor.getIssueManager().updateIssue(user, issue, EventDispatchOption.ISSUE_UPDATED, false)
    } catch (Exception e) {
    	log.debug "Exception: " + e
    	return 'An error occured. Please contact your JIRA administrator.'
	}
} else {
    log.debug "Jenn is already a watcher."
}