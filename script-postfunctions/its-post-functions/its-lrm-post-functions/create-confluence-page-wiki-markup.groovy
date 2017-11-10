package examples.docs

import com.atlassian.applinks.api.ApplicationLink
import com.atlassian.applinks.api.ApplicationLinkService
import com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType
import com.atlassian.jira.issue.Issue
import com.atlassian.sal.api.component.ComponentLocator
import com.atlassian.sal.api.net.Request
import com.atlassian.sal.api.net.Response
import com.atlassian.sal.api.net.ResponseException
import com.atlassian.sal.api.net.ResponseHandler
import groovy.json.JsonBuilder

/**
 * Retrieve the primary confluence application link
 * @return confluence app link
 */
def ApplicationLink getPrimaryConfluenceLink() {
    def applicationLinkService = ComponentLocator.getComponent(ApplicationLinkService.class)
    final ApplicationLink conflLink = applicationLinkService.getPrimaryApplicationLink(ConfluenceApplicationType.class);
    conflLink
}

// the issue provided to us in the binding
Issue issue = issue

// if you don't want to create confluence pages based on some criterion like issue type, handle this, eg:
// if (! issue.issueTypeObject.name == "Bug") {
//     return
// }

def confluenceLink = getPrimaryConfluenceLink()
assert confluenceLink // must have a working app link set up

def authenticatedRequestFactory = confluenceLink.createAuthenticatedRequestFactory()

// set the page title - this should be unique in the space or page creation will fail
def pageTitle = issue.key + " Discussion"
def pageBody = """h3. ${issue.summary}
{quote}${issue.description}{quote}
Yada yada, use this page to discuss the above...
"""

def params = [
    type: "page",
    title: pageTitle,
    space: [
        key: "TEST" // set the space key - or calculate it from the project or something
    ],
    // if you want to specify create the page under another, do it like this:
    // ancestors: [
    //     [
    //         type: "page",
    //         id: "14123220",
    //     ]
    // ],
    body: [
        wiki: [
            value: pageBody
        ]
    ]
]

authenticatedRequestFactory
    .createRequest(Request.MethodType.POST, "rest/api/content")
    .addHeader("Content-Type", "application/json")
    .setRequestBody(new JsonBuilder(params).toString())
    .execute(new ResponseHandler<Response>() {
        @Override
        void handle(Response response) throws ResponseException {
            if(response.statusCode != HttpURLConnection.HTTP_OK) {
                throw new Exception(response.getResponseBodyAsString())
            }
        }
})