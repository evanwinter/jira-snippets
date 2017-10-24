////////////////////////////////////////////////////////////////////////////////
//
//	Location: Administration > Add-ons > Script Fields > Last Comment
//
//	Description: Custom field that holds the latest comment on an issue.
//
////////////////////////////////////////////////////////////////////////////////

import com.atlassian.jira.component.ComponentAccessor

def primaryUser = getCustomFieldValue("Primary User")
def primaryUserName = primaryUser.getDisplayName()

