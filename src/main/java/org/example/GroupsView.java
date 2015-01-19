package org.example;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.UIScoped;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.example.backend.PhoneBookGroup;
import org.example.backend.PhoneBookService;
import org.vaadin.viritin.button.MButton;
import org.vaadin.viritin.fields.MTable;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.fields.MValueChangeEvent;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

@UIScoped
@CDIView("groups")
public class GroupsView extends CssLayout implements View {

    @Inject
    PhoneBookService service;

    @Inject // With Vaadin CDI one can also inject basic ui components
    PhoneBookGroupForm form;

    // Instantiate and configure a Table to list PhoneBookEntries
    MTable<PhoneBookGroup> entryList = new MTable<>(PhoneBookGroup.class)
            .withHeight("450px")
            .withFullWidth()
            .withProperties("name")
            .withColumnHeaders("Name");

    // Instanticate buttons, hook directly to listener methods in this class
    Button addNew = new MButton(FontAwesome.PLUS, this::addNew);
    Button delete = new MButton(FontAwesome.TRASH_O, this::deleteSelected);
    TextField filter = new MTextField().withInputPrompt("filter...");

    private void addNew(Button.ClickEvent e) {
        entryList.setValue(null);
        editEntry(new PhoneBookGroup());
    }

    private void deleteSelected(Button.ClickEvent e) {
        service.delete(entryList.getValue());
        listEntries();
        entryList.setValue(null);
    }

    private void listEntries(String filter) {
        entryList.setBeans(service.getGroups(filter));
    }

    private void listEntries() {
        listEntries(filter.getValue());
    }

    public void entryEditCanceled(PhoneBookGroup entry) {
        editEntry(entryList.getValue());
    }

    public void entrySelected(MValueChangeEvent<PhoneBookGroup> event) {
        editEntry(event.getValue());
    }

    /**
     * Assigns the given entry to form for editing.
     *
     * @param entry
     */
    private void editEntry(final PhoneBookGroup entry) {
        if (entry == null) {
            form.setVisible(false);
            delete.setEnabled(false);
        } else {
            boolean persisted = entry.getId() != null;
            delete.setEnabled(persisted);
            form.setEntity(entry);
            form.focusFirst();
        }
    }

    public void entrySaved(PhoneBookGroup value) {
        try {
            service.save(value);
            form.setVisible(false);
        } catch (Exception e) {
            Notification.show("Saving entity failed due to " + e.
                    getLocalizedMessage(), Notification.Type.WARNING_MESSAGE);
        }
        // deselect the entity
        entryList.setValue(null);
        // refresh list
        listEntries();
    }
    
    @PostConstruct
    void init() {
        // Add some event listners, e.g. to hook filter input to actually 
        // filter the displayed entries
        filter.addTextChangeListener(e -> {
            listEntries(e.getText());
        });
        entryList.addMValueChangeListener(this::entrySelected);
        form.setSavedHandler(this::entrySaved);
        form.setResetHandler(this::entryEditCanceled);

        addComponents(
                new MVerticalLayout(
                        new Header("PhoneBook"),
                        new MHorizontalLayout(addNew, delete, filter),
                        new MHorizontalLayout(entryList, form)
                )
        );

        // List all entries and select first entry in the list
        listEntries();
        entryList.setValue(entryList.firstItemId());        
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

    }

}
