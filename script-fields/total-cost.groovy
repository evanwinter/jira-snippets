    ////////////////////////////////////////////////////////////////////////////////
//
//	Location: Administration > Add-ons > Script Fields > Total Cost ($)
//
//	Description: Custom field that adds a '$' to the Cost field if not already present.
//
////////////////////////////////////////////////////////////////////////////////

import com.atlassian.jira.component.ComponentAccessor

// cost == '$' by default
def cost = getCustomFieldValue("Total Cost") as String
def formattedCost

if (cost) {
    // if cost doesn't start with '$' add it
    if (cost[0] != "\$") {
    	formattedCost = "\$" + cost
	} 
    // if cost is only '$' (unchanged from default), clear it
    else if (cost == "\$") {
        formattedCost = ""
    } 
    // if cost is already formatted correctly ('$XXXX.XX'), leave as is
    else {
        formattedCost = cost
    }
}
