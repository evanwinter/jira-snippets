////////////////////////////////////////////////////////////////////////////////
// File Name:      update-fmo-requesttype.groovy
//
// Author:         Evan Winter
//
// Context:        FMO
//
// Description:    When an issue is created or updated, compare Issue Type and FMO Request Type.
//                 If they don't match, update FMO Request Type to match the Issue Type.
//
////////////////////////////////////////////////////////////////////////////////

import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.ModifiedValue
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.component.ComponentAccessor
import org.apache.log4j.Logger
import org.apache.log4j.Level
def log = Logger.getLogger("com.acme.XXX")
log.setLevel(Level.DEBUG)

def BUSINESS_SERVICES_ARRAY = [
	"Academic Support Services Agreements",
	"Accounts Payable",
	"ACH and Wire Transfers (In and Out Going)",
	"Business Services",
	"Conference Services Billing",
	"Contract Only Request",
	"Contracting for Credit Instruction",
	"Custodial Funds",
	"Direct Payment (DP) to Vendors",
	"e-Re Division Coordinator",
	"e-Reimbursement Request",
	"Gift Cards",
	"Group Travel",
	"New Faculty and Staff - Financial Overview Onboarding",
	"Non-Contract Purchases > \$50,000",
	"Payment to Individual (PIR)",
	"P-Card",
	"Purchase Orders & Requisitions - Between \$5,000 & \$50,000",
	"Purchasing Requests < \$5,000",
	"Records Retention",
	"Relocations",
	"Shop@UW",
	"Travel",
	"UW Corporate Credit Card",
	"UWF Check Requests and Deposits (Audit)"
]

def ACCOUNTING_AND_OPERATIONS_ARRAY = [
	"Accounting and Operations",
	"Asset Management",
	"Business Operations Systems",
	"Campus Compliance",
	"Cost Transfer (Audit)",
	"Foundation Funds",
	"General Accounting",
	"Post-Award Administration",
	"Revenue Producing Contracts",
	"Scholarship Management",
	"WISDM (Setup, Inquiry)"
]

def BUDGET_ANALYSIS_REPORTING_ARRAY = [
	"Budget, Analysis and Reporting",
	"Budget, Analysis, and Reporting Advisory Services",
	"Campus Planning and Reporting",
	"Financial Analysis",
	"Financial Reporting",
	"WSB Budget Process"
]

def MISCELLANEOUS_ARRAY = [ "Miscellaneous" ]

def issue = event.issue as Issue
def customFieldManager = ComponentAccessor.getCustomFieldManager()
def issueManager = ComponentAccessor.getIssueManager()
def cf = customFieldManager.getCustomFieldObjectByName("FMO Request Type")
def cfValue = issue.getCustomFieldValue(cf) as String
def fieldConfig = cf.getRelevantConfig(issue)
def options = ComponentAccessor.optionsManager.getOptions(fieldConfig)

def businessServices = options.find { it.toString() == 'Business Services' }
def accountingOperations = options.find { it.toString() == 'Accounting and Operations' }
def budgetAnalysisReporting = options.find { it.toString() == 'Budget, Analysis and Reporting' }
def miscellaneous = options.find { it.toString() == 'Miscellaneous' }

def issueType = issue.getIssueType().name

if (issueType in BUSINESS_SERVICES_ARRAY && cfValue != "Business Services") {
	log.debug "Business Services"
	issue.setCustomFieldValue( cf, businessServices )
} else if (issueType in ACCOUNTING_AND_OPERATIONS_ARRAY && cfValue != "Accounting and Operations") {
	log.debug "Accounting and Operations"
	issue.setCustomFieldValue( cf, accountingOperations )
} else if (issueType in BUDGET_ANALYSIS_REPORTING_ARRAY && cfValue != "Budget, Analysis and Reporting") {
	log.debug "BAR"
	issue.setCustomFieldValue( cf, budgetAnalysisReporting )
} else if (issueType in MISCELLANEOUS_ARRAY && cfValue != "Miscellaneous") {
	log.debug "MISC"
	issue.setCustomFieldValue( cf, miscellaneous )
}

try {
	issueManager.updateIssue(event.getUser(), issue, EventDispatchOption.ISSUE_UPDATED, false)
} catch (Exception e) {
	log.debug e as String
}