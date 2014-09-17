package com.rackspace.automation.support.mongo.util

/**
 * Created by ravi on 9/16/14.
 */
class UrlNode {
    UrlNode parent
    List<UrlNode> children = []
    List<String> httpMethods = []
    String url
    String partUrl
}
