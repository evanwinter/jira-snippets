import argparse
import getpass
import sys

import json
import keyring
import requests

#-----------------------------------------------------------------------------
# Globals

BASE_URL = "https://wiki.bus.wisc.edu/rest/api/content"

VIEW_URL = "https://wiki.bus.wisc.edu/pages/viewpage.action?pageId="


def pprint(data):
    '''
    Pretty prints json data.
    '''
    print json.dumps(
        data,
        sort_keys = True,
        indent = 4,
        separators = (', ', ' : '))


def get_page_ancestors(auth, pageid):

    # Get basic page information plus the ancestors property

    url = '{base}/{pageid}?expand=ancestors'.format(
        base = BASE_URL,
        pageid = pageid)

    r = requests.get(url, auth = auth)

    r.raise_for_status()

    return r.json()['ancestors']


def get_page_info(auth, pageid):

    url = '{base}/{pageid}'.format(
        base = BASE_URL,
        pageid = pageid)

    r = requests.get(url, auth = auth)

    r.raise_for_status()

    return r.json()


def write_data(auth, html, pageid, title = None):

    info = get_page_info(auth, pageid)

    ver = int(info['version']['number']) + 1

    ancestors = get_page_ancestors(auth, pageid)

    anc = ancestors[-1]
    del anc['_links']
    del anc['_expandable']
    del anc['extensions']

    if title is not None:
        info['title'] = title

    data = {
        'id' : str(pageid),
        'type' : 'page',
        'title' : info['title'],
        'version' : {'number' : ver},
        'ancestors' : [anc],
        'body'  : {
            'storage' :
            {
                'representation' : 'storage',
                'value' : str(html),
            }
        }
    }

    data = json.dumps(data)

    url = '{base}/{pageid}'.format(base = BASE_URL, pageid = pageid)

    r = requests.put(
        url,
        data = data,
        auth = auth,
        headers = { 'Content-Type' : 'application/json' }
    )

    r.raise_for_status()

    print "Wrote '%s' version %d" % (info['title'], ver)
    print "URL: %s%d" % (VIEW_URL, pageid)


def get_login(username = None):
    '''
    Get the password for username out of the keyring.
    '''

    if username is None:
        username = getpass.getuser()

    passwd = keyring.get_password('confluence_script', username)

    if passwd is None:
        passwd = getpass.getpass()
        keyring.set_password('confluence_script', username, passwd)

    return (username, passwd)


def main():

    parser = argparse.ArgumentParser()

    parser.add_argument(
        "-u",
        "--user",
        default = getpass.getuser(),
        help = "Specify the username to log into Confluence")

    parser.add_argument(
        "-t",
        "--title",
        default = None,
        type = str,
        help = "Specify a new title")

    parser.add_argument(
        "-f",
        "--file",
        default = None,
        type = str,
        help = "Write the contents of FILE to the confluence page")

    parser.add_argument(
        "pageid",
        type = int,
        help = "Specify the Conflunce page id to overwrite")

    parser.add_argument(
        "html",
        type = str,
        default = None,
        nargs = '?',
        help = "Write the immediate html string to confluence page")

    options = parser.parse_args()

    auth = get_login(options.user)

    if options.html is not None and options.file is not None:
        raise RuntimeError(
            "Can't specify both a file and immediate html to write to page!")

    if options.html:
        html = options.html

    else:

        with open(options.file, 'r') as fd:
            html = fd.read()

    write_data(auth, html, options.pageid, options.title)


if __name__ == "__main__" : main()