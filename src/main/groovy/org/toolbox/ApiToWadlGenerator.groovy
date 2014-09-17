package com.rackspace.automation.support.mongo.util

/**
 * Created by ravi on 9/15/14.
 */
def apiFilePath = "/home/ravi/dev/projects/as/bpi/bpi/apis/load_balancer_api.py"
def file = new File(apiFilePath)

def gets = []
def puts = []
def posts = []
def deletes = []
def roles = [get: 'bpi_read', put: 'bpi_write', post: 'bpi_write', delete: 'bpi_write']

def myRegularExpression = /@BPI.(\w*)\('(\S*)'\)/

def map = [:]

file.eachLine { String lineOfCode ->
    if (lineOfCode.contains('@BPI')) {
        def matcher = (lineOfCode =~ myRegularExpression)
        if (matcher.matches()) {
            def httpMethod = matcher.group(1)
            def resource = matcher.group(2)
            def currentEntry = (map.get(resource) ?: []) as List<String>
            currentEntry.add(httpMethod)
            map.put(resource, currentEntry)
        }
    }
}

def c = [compare:
    { String a, String b ->
        if (a.equals(b)) {
            0
        } else {
            if (a.contains(b)) {
                -1
            } else {
                1
            }
        }
    }
] as Comparator

def tm = new TreeMap(c)

map.entrySet().each { entry ->
    tm.put(entry.key, entry.value)
}
def foo = """
<?xml version="1.0"?>
<application xmlns="http://wadl.dev.java.net/2009/02" xmlns:rax="http://docs.rackspace.com/api">
    <resources base="http://10.14.211.218:8080">
"""

println foo
tm.entrySet().each { entry ->
    def resource = entry.key.replace('<', '{').replace('>', '}')
    def httpMethods = entry.value
    println "<resource path=\"$resource\" rax:roles=\"Automation_Integration\">"
    def params = resource.findAll("\\{(\\w*)\\}")
    params.each { String currParam ->
        println "<param required=\"true\" style=\"template\" name=\"${currParam.replace('{', '').replace('}', '')}\"/>"
    }
    httpMethods.each { String method ->
        def methodStr =
            """
            <method name="${method.toUpperCase()}" rax:roles="${roles.get(method)}">
                <request>
                    <representation mediaType="application/json"/>
                </request>
            </method>
        """
        println methodStr
    }
    println "</resource>"
}

def bar = """
    </resources>
</application>
"""
println bar

def urlNodeHelper = new UrlNodeHelper()
UrlNode urlNode
tm.keySet().each { String url ->
    if (urlNode) {
        urlNode = urlNodeHelper.parseUrl(url, urlNodeHelper.getRoot(urlNode))
    } else {
        urlNode = urlNodeHelper.parseUrl(url, urlNode)
    }
}