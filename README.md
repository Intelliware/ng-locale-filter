ng-locale-filter
================

A simple filter for rewriting angular locale resource requests.

Motivation
----------

When building a localized angular application there are 2 recommended approaches:

1. Cat the contents of the core angular javascript with the desired angular locale javascript file and then serving the file based on some criteria, perhaps using the 'Accept-Languge' header.
2. Serve the angular javascript and the desired locale javascript file as 2 requests.

In both cases we would most likely be required to implement server side templating logic.  This filter helps keeps logic out of the angular templates by rewriting a request for a generic javascript resource to a request for an angular locale javascript resource.

tl;dr
------------

- Reference the core angular javascript file and a generic javascript file in your angular shell page:

```html
  <html>
    <head>
      <script src="scripts/angular.js"></script>
      <script src="scripts/a-locale-resource-that-does-not-exist"></script>
    </head>
  </html>
```
- Include this ng-locale-filter artifact in your web application or at the container level.  i.e., configured as a filter for all web apps.

- Configure the filter in your web.xml:

```xml
	<filter>
		<filter-name>NGLocaleFilter</filter-name>
		<filter-class>com.intelliware.rewrite.NGLocaleResourceFilter</filter-class>
		<init-param>
			<param-name>rewritePath</param-name>
			<param-value>scripts/a-locale-resource-that-does-not-exist</param-value>
		</init-param>
		<init-param>
		  <!--The path to the angular js locale files-->
			<param-name>resourcePath</param-name>
			<param-value>/scripts/locales</param-value>
		</init-param>
		<init-param>
		  <!--The name of a cookie containing the user selected locale overriding the "Accept-Language" header-->
			<param-name>localeCookieName</param-name>
			<param-value>locale</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>NGLocaleFilter</filter-name>
		<url-pattern>/*</url-pattern>
		<dispatcher>REQUEST</dispatcher>
		<dispatcher>FORWARD</dispatcher>		
	</filter-mapping>
```
How it works
------------
When a request is made for a resource matching the "rewritePath" parameter the filter will first look for a locale specified in a cookie in the form of langcode-country code.  If not found the "Accept-Language" header will be used.  The filter then forwards the request to the resource at the following path "resourcePath" + "/angular-locale_" + langcode[-countrycode] + ".js".
