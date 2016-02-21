# JPA Addressbook Example

This is a simple example application about creating JPA backed Java EE application
with rich Vaadin based UI. Its not trying to be a good address book, but it
demonstrates good technologies to create rich web applications your your custom
needs. The domain model is bit more complex than typical one table examples
to to demonstrated how to handle relations that you most likely have in you 
own domain model. It also demonstrates a very simple add-on usage with Vaadin 
(the iconic "switch" add-on).

The source code for this example is available 
[from github](https://github.com/mstahv/jpa-addressbook).

## Views
Currently, four different views are avaiable.

### Main

Main view for *viewing* phone entries and *adding* new entries.

### Groups

Management of group to whom phone entries can be assigned.

### CSVImport
View that allows for importing phone entries from a CSV file 
An example CSV file is given at [/phonebook_example_entries.csv](/phonebook_example_entries.csv)

### About
This about dialogue.

