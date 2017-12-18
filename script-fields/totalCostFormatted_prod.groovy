/*
*   @name   totalCostFormatted_prod.groovy
*   @type   Script Field
*   @brief  Return the value in custom field "Total Cost", adding a 
*           dollar sign at the front if not already present.
*/

import com.atlassian.jira.user.ApplicationUser

def totalCost = getCustomFieldValue("Total Cost") as String

if (!totalCost) { return }

if (totalCost == "\$") {
    // if Total Cost is only a dollar sign w/ no value, clear it.
    return ''
} else if (totalCost[0] != "\$") {
    // if Total Cost has chars and the first char is not a dollar sign, add one.
    return ("\$" + totalCost)
} else {
    // if Total Cost has chars and the first char is a dollar sign, do nothing.
    return totalCost
}