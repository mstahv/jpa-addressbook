package org.example;

import org.example.backend.PhoneBookEntry;

import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import javax.inject.Inject;
import org.example.backend.PhoneBookAddress;
import org.example.backend.PhoneBookService;
import org.vaadin.teemu.switchui.Switch;
import org.vaadin.viritin.fields.ElementCollectionField;
import org.vaadin.viritin.fields.EnumSelect;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.fields.MultiSelectTable;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MMarginInfo;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * This class introduces a Form to edit PhoneBookEntry pojos. It is a good habit
 * to separate logical pieces of your UI code to classes. This will improve
 * re-usability, readability, maintainability, testability.
 *
 * @author Matti Tahvonen <matti@vaadin.com>
 */
public class PhoneBookEntryForm extends AbstractForm<PhoneBookEntry> {

    TextField name = new MTextField("Name");
    TextField number = new MTextField("Number");
    TextField email = new MTextField("Email");
    DateField birthDate = new PopupDateField("Birth date");
    Switch sendChristmasCard = new Switch("Send christmas card");
    MultiSelectTable groups = new MultiSelectTable("Groups")
            .withProperties("name").withColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);

    public static class AddressRow {
        EnumSelect type = new EnumSelect().withWidth("6em");
        TextField street = new MTextField().withInputPrompt("street");
        TextField city = new MTextField().withInputPrompt("city").withWidth("6em");
        TextField zip = new MTextField().withInputPrompt("zip").withWidth("4em");
    }

    ElementCollectionField<PhoneBookAddress> addresses = new ElementCollectionField<>(
            PhoneBookAddress.class, AddressRow.class).withCaption("Addressess");

    @Inject
    PhoneBookService service;

    public PhoneBookEntryForm() {
        setEagerValidation(true);
    }

    @Override
    protected Component createContent() {
        groups.setOptions(service.getGroups());
        return new MVerticalLayout(
                getToolbar(),
                new MHorizontalLayout(
                        new MFormLayout(
                                name,
                                number,
                                email,
                                birthDate,
                                sendChristmasCard
                        ).withMargin(false),
                        groups.withFullHeight()
                ),
                addresses
        ).withMargin(new MMarginInfo(false, true));
    }

}
