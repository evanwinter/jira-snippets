////////////////////////////////////////////////////////////////////////////////
// File Name:      automate-inprogress.groovy
//
// Author:         Evan Winter
//
// Context:        ITS
//
// Description:    Condition that's true IFF all of the following are true: 
//                  1. Issue Type is not "ITS-Null"
//                  2. ITIL Category is not "Not-Assigned"
//                  3. Assignee is not NULL
//                  4. Status is "Pending"
//
////////////////////////////////////////////////////////////////////////////////

def issueTypeIsNull = (issue.issueType == 'ITS-Null')
def itilCategoryIsNull = (cfValues['ITIL Category']?.value == 'Not-Assigned')
def assigneeIsNull = (issue.assignee == null)
def isPending = (issue.getStatus().getName() == "Pending")

(!issueTypeIsNull && !itilCategoryIsNull && !assigneeIsNull && isPending)