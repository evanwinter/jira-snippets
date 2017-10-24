////////////////////////////////////////////////////////////////////////////////
// File Name:      fmo-transition-conditions.groovy
//
// Author:         Evan Winter
//
// Context:        FMO
//
// Description:    A collection of conditions on which an FMO issue can transition to a certain status.
//					Essentially, if an issue's Issue Type is present in a Status's corresponding array,
//					the issue is able to transition to that Status.
//
////////////////////////////////////////////////////////////////////////////////

String[] master = [
		"Contracting for Credit Instruction",
		"Gift Cards",
		"Shop@UW",
		"Non-Contract Purchases > \$50,000",
		"Contract Only Request",
		"Purchase Orders & Requisitions - Between \$5,000 & \$50,000",
		"Purchasing Requests < \$5,000",
		"P-Card",
		"UW Corporate Credit Card",
		"e-Re Division Coordinator",
		"e-Reimbursement Request",
		"Group Travel",
		"Travel",
		"Accounts Payable",
		"Payment to Individual (PIR)",
		"ACH and Wire Transfers (In and Out Going)",
		"Conference Services Billing",
		"Custodial Funds",
		"Direct Payment (DP) to Vendors",
		"UWF Check Requests and Deposits (Audit)",
		"New Faculty and Staff - Financial Overview Onboarding",
		"Relocations",
		"Records Retention",
		"General Accounting",
		"Asset Management",
		"Revenue Producing Contracts",
		"Business Operations Systems",
		"WISDM (Setup, Inquiry)",
		"Post-Award Administration",
		"Scholarship Management",
		"Campus Compliance",
		"Foundation Funds",
		"WSB Budget Process",
		"Campus Planning and Reporting",
		"Financial Analysis",
		"Financial Reporting",
		"Budget, Analysis, and Reporting Advisory Services",
		"Cost Transfer (Audit)",
		"Academic Support Services Agreements",
		"Business Services",
		"Accounting and Operations",
		"Budget, Analysis and Reporting",
		"Miscellaneous"
	]

String[] blue = [
		"Direct Payment (DP) to Vendors",
		"P-Card",
		"Shop@UW",
		"UW Corporate Credit Card",
		"Gift Cards",
		"ACH and Wire Transfers (In and Out Going)",
		"Custodial Funds",
		"Cost Transfer (Audit)",
		"General Accounting",
		"Payment to Individual (PIR)",
		"WISDM (Setup, Inquiry)",
		"Campus Compliance"
	]

String[] gray = [
		"Academic Support Services Agreements",
		"Contracting for Credit Instruction",
		"Non-Contract Purchases > \$50,000",
		"Purchase Orders & Requisitions - Between \$5,000 & \$50,000",
		"Purchasing Requests < \$5,000",
		"Post-Award Administration",
		"Revenue Producing Contracts",
		"Campus Planning and Reporting",
		"Financial Analysis",
		"Financial Reporting",
		"Asset Management"
	]

String[] yellow = [
		"e-Re Division Coordinator",
		"UWF Check Requests and Deposits (Audit)",
		"Foundation Funds",
		"Budget, Analysis and Reporting Advisory Services"
	]

String[] orange = [
		"Business Services",
		"Accounts Payable",
		"Conference Services Billing",
		"Contract Only Request",
		"Relocations",
		"Travel",
		"Group Travel",
		"Records Retention",
		"Accounting and Operations",
		"Business Operations Systems",
		"Budget, Analysis and Reporting",
		"WSB Budget Process",
		"Miscellaneous",
		"e-Reimbursement Request",
		"Scholarship Management"
	]

// WSB External = gray + orange
// For "Go to WSB External" transitions
String[] wsbexternal = [
		"Academic Support Services Agreements",
		"Contracting for Credit Instruction",
		"Non-Contract Purchases > \$50,000",
		"Purchase Orders & Requisitions - Between \$5,000 & \$50,000",
		"Purchasing Requests < \$5,000",
		"Post-Award Administration",
		"Revenue Producing Contracts",
		"Campus Planning and Reporting",
		"Financial Analysis",
		"Financial Reporting",
		"Asset Management",
		"Business Services",
		"Accounts Payable",
		"Conference Services Billing",
		"Contract Only Request",
		"Relocations",
		"Travel",
		"Group Travel",
		"Records Retention",
		"Accounting and Operations",
		"Business Operations Systems",
		"Budget, Analysis and Reporting",
		"WSB Budget Process",
		"Miscellaneous",
		"e-Reimbursement Request",
		"Scholarship Management"
	]

(issue.getIssueType().name in wsbexternal) // if current issue's Issue Type is in the WSB External array, allow transitions to the WSB External status

// Foundation = yellow + orange
// For "Go to Foundation" transitions
String[] foundation = [
		"e-Re Division Coordinator",
		"UWF Check Requests and Deposits (Audit)",
		"Foundation Funds",
		"Budget, Analysis and Reporting Advisory Services",
		"Business Services",
		"Accounts Payable",
		"Conference Services Billing",
		"Contract Only Request",
		"Relocations",
		"Travel",
		"Group Travel",
		"Records Retention",
		"Accounting and Operations",
		"Business Operations Systems",
		"Budget, Analysis and Reporting",
		"WSB Budget Process",
		"Miscellaneous",
		"e-Reimbursement Request",
		"Scholarship Management"
	]

(issue.getIssueType().name in foundation) // if current issue's Issue Type is in the Foundation array, allow transitions to the Foundation status

// Campus = blue + gray + yellow + orange
// For "Go to Campus" transitions

String[] campus = [
		"Direct Payment (DP) to Vendors",
		"P-Card",
		"Shop@UW",
		"UW Corporate Credit Card",
		"Gift Cards",
		"ACH and Wire Transfers (In and Out Going)",
		"Custodial Funds",
		"Cost Transfer (Audit)",
		"General Accounting",
		"Payment to Individual (PIR)",
		"WISDM (Setup, Inquiry)",
		"Campus Compliance",
		"Academic Support Services Agreements",
		"Contracting for Credit Instruction",
		"Non-Contract Purchases > \$50,000",
		"Purchase Orders & Requisitions - Between \$5,000 & \$50,000",
		"Purchasing Requests < \$5,000",
		"Post-Award Administration",
		"Revenue Producing Contracts",
		"Campus Planning and Reporting",
		"Financial Analysis",
		"Financial Reporting",
		"Asset Management",
		"e-Re Division Coordinator",
		"UWF Check Requests and Deposits (Audit)",
		"Foundation Funds",
		"Budget, Analysis and Reporting Advisory Services",
		"Business Services",
		"Accounts Payable",
		"Conference Services Billing",
		"Contract Only Request",
		"Relocations",
		"Travel",
		"Group Travel",
		"Records Retention",
		"Accounting and Operations",
		"Business Operations Systems",
		"Budget, Analysis and Reporting",
		"WSB Budget Process",
		"Miscellaneous",
		"e-Reimbursement Request",
		"Scholarship Management"
	]

(issue.getIssueType().name in campus) // if current issue's Issue Type is in the Campus array, allow transitions to the Campus status