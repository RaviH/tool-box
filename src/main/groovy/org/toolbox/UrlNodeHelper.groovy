package com.rackspace.automation.support.mongo.util

/**
 * Created by ravi on 9/16/14.
 */
class UrlNodeHelper {

    def parseUrl(String url, UrlNode urlNode = null) {
        if (!url) {
            return urlNode
        } else {
            def urlParts = url.split("/")
            if (urlParts[0] == "") {
                urlParts = urlParts.tail()
            }
            def newNode = new UrlNode(parent: urlNode, url: url, partUrl: "/${urlParts.head()}")
            def parent = findMyParent(url, urlNode)
            newNode.parent = parent
            parent.children << newNode
            def restOfUrl = urlParts.tail().join("/")
            parseUrl(restOfUrl, newNode)
        }
    }

    def getRoot(UrlNode urlNode) {
        urlNode.parent ? getRoot(urlNode.parent) : urlNode
    }

    UrlNode findMyParent(String url, UrlNode urlNode) {
        if (urlNode.url == url) {
            urlNode
        } else {
            if (urlNode.children) {
                urlNode.children?.each { UrlNode child ->
                    if (url.contains(child.url)) {
                        findMyParent(url, child)
                    }
                }
            } else {
                urlNode
            }
        }
    }
}
