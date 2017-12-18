////////////////////////////////////////////////////////////////////////////////
// File Name:      generate-checklist.groovy
//
// Author:         Evan Winter
//
// Context:        ITS
//
// Description:    When new request created, generate checklist IFF the following are true:
//					1. Issue doesn't have any subtasks.
//					2. Issue Type = "Onboarding" OR "Offboarding"
//
////////////////////////////////////////////////////////////////////////////////

(issue.subTaskObjects.size() == 0 && (issue.issueType.name == "Onboarding" || issue.issueType.name == "Offboarding"))