/*
*	@name 	ITS_generateChecklist_prod.groovy
*	@type 	Script Listener (Condition)	
*	@brief 	Return true if all of the following conditions are true:
*				- This issue has no subtasks.
*				- Issue Type = "Onboarding" or "Offboarding"
*/

return (issue.subTaskObjects.size() == 0 && (issue.issueType.name == "Onboarding" || issue.issueType.name == "Offboarding"))