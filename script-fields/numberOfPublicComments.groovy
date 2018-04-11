/*
*   @name   numberOfPublicComments.groovy
*   @type   Script Field
*   @brief  The number of public (non-internal/visible to customer) comments on this issue.
*/


import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.issue.comment.property.CommentPropertyService
import com.atlassian.jira.issue.comments.Comment
import groovy.json.JsonSlurper

final SD_PUBLIC_COMMENT = "sd.public.comment"

def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def commentPropertyService = ComponentAccessor.getComponent(CommentPropertyService)
def commentManager = ComponentAccessor.getCommentManager()

def isInternal = { Comment c ->
    def commentProperty = commentPropertyService.getProperty(user, c.id, SD_PUBLIC_COMMENT)
        .getEntityProperty().getOrNull()

    if (commentProperty) {
        def props = new JsonSlurper().parseText(commentProperty.getValue())
        props['internal'].toBoolean()
    }
    else {
        null
    }
}

def numComments = 0

def comments = commentManager.getComments(issue)
if (comments) {
    for (c in comments) {
        if (!(isInternal(c))) {
            numComments++
        }
    }
}

return numComments