# JPA based address book example

This is a simple example application about creating JPA backed Java EE application
with rich Vaadin based UI. Its not trying to be a good address book app, but it
demonstrates good technologies to create rich web applications your custom
needs. The domain model is bit more complex than typical one table examples
to demonstrate how to handle relations that you most likely have in you 
own domain model. It also demonstrates a very simple add-on usage with Vaadin 
(the iconic "switch" add-on).

If you are new to Maven and want to try this application, check out this [tiny Maven tutorial](https://vaadin.com/blog/-/blogs/the-maven-essentials-for-the-impatient-developer).

## Run the example

As this application is based on JEE it relies on a JEE capable application server. 
The Maven project setup includes a Wildfly Maven application server for demonstration purposes.

To run the project, you can use

```
mvn wildfly:run
```
to launch this locally on Wildfly server. Afterwards, the application is available
in your local browser at

[http://localhost:8080/jpa-addressbook-1.0-SNAPSHOT](http://localhost:8080/jpa-addressbook-1.0-SNAPSHOT)


Alternatively, you can deploy to a Java EE 7+ server like Wildfly or Glassfish. 
With Liberty you'll need to present a data source 
(other modern servers provide "development datasource" when 
no jta-datasource is present in persistence.xml). 

This is a suitable basis for small to medium sized apps. For larger applications,
consider using MVP or similar to structure your UI code. See e.g. [Vaadin Bakery app starter](https://vaadin.com/start/).

### Older version

The current version of the example uses Vaadin 8, the older Vaadin 7 compatible version is available in the [vaadin7](https://github.com/mstahv/jpa-addressbook/tree/vaadin7) branch.