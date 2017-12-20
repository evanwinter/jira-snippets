/*
*	@name 	FMO_syncReqTypeToIssueType_prod.groovy
*	@type 	Script Listener
*	@brief 	Update "FMO Request Type" based on the current Issue Type.
*/

import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.event.type.EventDispatchOption

/* Debugging */
import org.apache.log4j.Logger
import org.apache.log4j.Level
def log = Logger.getLogger("com")
log.setLevel(Level.DEBUG)

/* These are the four main categories of requests ("FMO Request Type") and their underlying Issue Types. */
/* Each Issue Type listed here must be IDENTICAL to the corresponding Issue Type object in FMO's Issue Type Scheme. */
def BUSINESS_SERVICES_ISSUETYPES = ["Academic Support Services Agreements", "Accounts Payable", "ACH and Wire Transfers (In and Out Going)", "Business Services", "Conference Services Billing", "Contract Only Request", "Contracting for Credit Instruction", "Custodial Funds", "Direct Payment (DP) to Vendors", "e-Re Division Coordinator", "e-Reimbursement Request", "Gift Cards", "Group Travel", "New Faculty and Staff - Financial Overview Onboarding", "Non-Contract Purchases > \$50,000", "Payment to Individual (PIR)", "P-Card", "Purchase Orders & Requisitions - Between \$5,000 & \$50,000", "Purchasing Requests < \$5,000", "Records Retention", "Relocations", "Shop@UW", "Travel", "UW Corporate Credit Card", "UWF Check Requests and Deposits (Audit)"]
def ACCOUNTING_AND_OPERATIONS_ISSUETYPES = ["Accounting and Operations", "Asset Management", "Business Operations Systems", "Campus Compliance", "Cost Transfer (Audit)", "Foundation Funds", "General Accounting", "Post-Award Administration", "Revenue Producing Contracts", "Scholarship Management", "WISDM (Setup, Inquiry)"]
def BUDGET_ANALYSIS_REPORTING_ISSUETYPES = ["Budget, Analysis and Reporting", "Budget, Analysis, and Reporting Advisory Services", "Campus Planning and Reporting", "Financial Analysis", "Financial Reporting", "WSB Budget Process"]
def MISCELLANEOUS_ISSUETYPES = ["Miscellaneous"]

MutableIssue thisIssue = ComponentAccessor.getIssueManager().getIssueObject("FMO-761")
ApplicationUser loggedInUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

def reqTypeField = ComponentAccessor.getCustomFieldManager().getCustomFieldObjectByName("FMO Request Type")
def reqTypeValue = thisIssue.getCustomFieldValue(reqTypeField)
def reqTypeConfig = reqTypeField.getRelevantConfig(thisIssue)
def reqTypeOptions = ComponentAccessor.getOptionsManager().getOptions(reqTypeConfig)

def businessServices = reqTypeOptions.find{it.toString() == 'Business Services'}
def accountingOperations = reqTypeOptions.find{it.toString() == 'Accounting and Operations'}
def budgetAnalysisReporting = reqTypeOptions.find{it.toString() == 'Budget, Analysis and Reporting'}
def miscellaneous = reqTypeOptions.find{it.toString() == 'Miscellaneous'}

def thisIssueTypeName = thisIssue.getIssueType().getName()
def newReqType

if (thisIssueTypeName in BUSINESS_SERVICES_ISSUETYPES) {
    newReqType = businessServices
} else if (thisIssueTypeName in ACCOUNTING_AND_OPERATIONS_ISSUETYPES) {
	newReqType = accountingOperations
} else if (thisIssueTypeName in BUDGET_ANALYSIS_REPORTING_ISSUETYPES) {
	newReqType = budgetAnalysisReporting
} else if (thisIssueTypeName in MISCELLANEOUS_ISSUETYPES) {
	newReqType = miscellaneous
} else {
    return "Issue Type was not located within any FMO Request Type list of issue types."
}

log.debug "Issue Type is '$thisIssueTypeName', so $reqTypeField should be '$newReqType'"

if (thisIssue.getCustomFieldValue(reqTypeField) != newReqType) {
    log.debug "$reqTypeField is already correct. No update necessary"
    return
}

log.debug "Updating $reqTypeField from '$reqTypeValue' to '$newReqType'"
thisIssue.setCustomFieldValue(reqTypeField, newReqType)

// Store to DB.
try {
	ComponentAccessor.getIssueManager().updateIssue(
		loggedInUser,
		thisIssue,
		EventDispatchOption.ISSUE_UPDATED,
		false
	)
} catch (Exception e) {
	log.debug "Exception: " + e
}