/*
*	@name 	fullNameOfPrimaryUser_prod.groovy
*	@type 	Script Field	
*	@brief 	Return the full (display) name of the user in the Primary User custom field.
*/

import com.atlassian.jira.user.ApplicationUser

ApplicationUser primaryUser = getCustomFieldValue("Primary User") as ApplicationUser
return (primaryUser) ? primaryUser.getDisplayName() : null