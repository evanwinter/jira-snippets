/*
	This is a Script Condition that checks to see if everyone in a specified group has voted for an issue. It compares the number of users in the group specified by the Group Picker custom field "Needs approval from" to the number of votes for the current issue.

	Evaluates to TRUE if number of votes is greater than or equal to the number of users in the group.
*/

import com.atlassian.jira.component.ComponentAccessor

def groupManager = ComponentAccessor.getGroupManager()
def voteManager = ComponentAccessor.getVoteManager()
def customFieldManager = ComponentAccessor.getCustomFieldManager()

// get the group specified by the 'Needs approval from' group picker
def cf = customFieldManager.getCustomFieldObjectByName('Needs approval from')
def group = cf.getValue(issue)

// get number of votes for the current issue, and get number of users in group
int voteCount = voteManager.getVoteCount(issue)
int numOfApprovers = groupManager.getUsersInGroupCount(group)

// if every user in "Needs approval from"
voteCount >= numOfApprovers