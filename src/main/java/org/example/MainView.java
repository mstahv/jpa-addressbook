package org.example;

import com.vaadin.cdi.CDIView;
import com.vaadin.data.HasValue.ValueChangeEvent;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.example.backend.PhoneBookEntry;
import org.example.backend.PhoneBookService;
import org.vaadin.cdiviewmenu.ViewMenuItem;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.grid.MGrid;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

@CDIView("")
@ViewMenuItem(order = ViewMenuItem.BEGINNING, icon = VaadinIcons.USER)
public class MainView extends CssLayout implements View {

    @Inject
    PhoneBookService service;

    @Inject // With Vaadin CDI one can also inject basic ui components
    PhoneBookEntryForm form;

    // Instantiate and configure a Table to list PhoneBookEntries
    MGrid<PhoneBookEntry> entryList = new MGrid<>(PhoneBookEntry.class)
            .withHeight("450px")
            .withFullWidth()
            .withProperties("name", "number")
            .withColumnHeaders("Name", "Phone number");

    // Instanticate buttons, hook directly to listener methods in this class
    Button addNew = new MButton(FontAwesome.PLUS, this::addNew);
    Button delete = new MButton(FontAwesome.TRASH_O, this::deleteSelected);
    TextField filter = new MTextField().withInputPrompt("filter...");

    private void addNew(Button.ClickEvent e) {
        entryList.deselectAll();
        editEntry(new PhoneBookEntry());
    }

    private void deleteSelected(Button.ClickEvent e) {
        service.delete(entryList.asSingleSelect().getValue());
        listEntries();
        entryList.deselectAll();
    }

    private void listEntries(String filter) {
        // An easy, and for many cases just fine solution is to fetch
        // everything from the db
        // entryList.setItems(service.getEntries(filter));
        
        // but if you expect to have a laaarge set of results, 
        // go with lazy binding
        lazyListEntries(filter);
    }

    private void listEntries() {
        listEntries(filter.getValue());
    }

    public void entryEditCanceled(PhoneBookEntry entry) {
        editEntry(entryList.asSingleSelect().getValue());
    }

    public void entrySelected(ValueChangeEvent<PhoneBookEntry> event) {
        editEntry(event.getValue());
    }

    /**
     * Assigns the given entry to form for editing.
     *
     * @param entry
     */
    private void editEntry(PhoneBookEntry entry) {
        if (entry == null) {
            form.setVisible(false);
            delete.setEnabled(false);
        } else {
            boolean persisted = entry.getId() != null;
            if (persisted) {
                // reattach (in case Hibernate is in use, we need to load lazy loaded properties)
                entry = service.loadFully(entry);
            }
            delete.setEnabled(persisted);
            form.setEntity(entry);
            form.focusFirst();
        }
    }

    public void entrySaved(PhoneBookEntry value) {
        try {
            service.save(value);
            form.setVisible(false);
        } catch (Exception e) {
            // Most likely optimistic locking exception
            Notification.show("Saving entity failed!", e.
                    getLocalizedMessage(), Notification.Type.WARNING_MESSAGE);
        }
        // deselect the entity
        entryList.deselectAll();
        // refresh list
        listEntries();

    }

    @PostConstruct
    void init() {
        // Add some event listners, e.g. to hook filter input to actually 
        // filter the displayed entries
        filter.addValueChangeListener(e -> {
            listEntries(e.getValue());
        });
        entryList.asSingleSelect().addValueChangeListener(this::entrySelected);
        form.setSavedHandler(this::entrySaved);
        form.setResetHandler(this::entryEditCanceled);

        addComponents(
                new MVerticalLayout(
                        new MHorizontalLayout(addNew, delete, filter),
                        new MHorizontalLayout(entryList, form)
                )
        );

        // List all entries and select first entry in the list
        listEntries();
        form.setVisible(false);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }

    /**
     * A simple example how to make lazy loading change all the way to the
     * database and save JVM memory with large databases (and/or lots of users).
     * Uses Viritin add-on and its MTable to do lazy binding.
     */
    private void lazyListEntries(String filter) {
        entryList.setDataProvider(
                (sortOrder, offset, limit) -> {
                    return service.getEntriesPaged(filter, offset, limit, sortOrder).stream();
                },
                () -> service.countEntries(filter)
        );
    }

}
