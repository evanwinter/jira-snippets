/*
*	@name 	ITS_automateInProgress_prod.groovy
*	@type 	Script Listener (Condition)	
*	@brief 	Return true if all of the following conditions are true:
*				- Issue Type is not "ITS-Null"
*				- ITIL Category is not "Not-Assigned"
*				- Assignee is not null
*				- Status is "Pending"
*/

def issueTypeIsNull = (issue.issueType == "ITS-Null")
def itilCategoryIsNull = (cfValues["ITIL Category"]?.value == "Not-Assigned")
def assigneeIsNull = (issue.assignee == null)
def isPending = (issue.getStatus().getName() == "Pending")

return (!issueTypeIsNull && !itilCategoryIsNull && !assigneeIsNull && isPending)