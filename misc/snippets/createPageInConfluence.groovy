package examples.docs

import com.atlassian.applinks.api.ApplicationLink
import com.atlassian.applinks.api.ApplicationLinkService
import com.atlassian.applinks.api.application.confluence.ConfluenceApplicationType
import com.atlassian.sal.api.component.ComponentLocator
import com.atlassian.sal.api.net.Request
import com.atlassian.sal.api.net.Response
import com.atlassian.sal.api.net.ResponseException
import com.atlassian.sal.api.net.ResponseHandler
import groovy.json.JsonBuilder
import org.apache.log4j.Logger
import org.apache.log4j.Level
def log = Logger.getLogger("com.acme.XXX")
log.setLevel(Level.DEBUG)

/**
 * Retrieve the primary confluence application link
 * @return confluence app link
 */
ApplicationLink getPrimaryConfluenceLink() {
    def applicationLinkService = ComponentLocator.getComponent(ApplicationLinkService.class)
    final ApplicationLink conflLink = applicationLinkService.getPrimaryApplicationLink(ConfluenceApplicationType.class);
    conflLink
}

// if you don't want to create confluence pages based on some criterion like issue type, handle this, eg:
if (! issue.getIssueType().getName() == "Bug") {
    return
}

def confluenceLink = getPrimaryConfluenceLink()
assert confluenceLink // must have a working app link set up

log.debug "Confluence Link: " + (confluenceLink as String)

def authenticatedRequestFactory = confluenceLink.createAuthenticatedRequestFactory()

// set the page title - this should be unique in the space or page creation will fail
def pageTitle = issue.key + " Discussion"
def pageBody = """<p> ${issue.summary}<p>

<p>${issue.description}</p>

Use this page to discuss the above...
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
        storage: [
            value: pageBody,
            representation: "storage"
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