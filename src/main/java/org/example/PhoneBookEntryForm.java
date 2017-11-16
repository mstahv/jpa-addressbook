package org.example;

import org.example.backend.PhoneBookEntry;

import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Grid;
import com.vaadin.ui.MultiSelect;
import java.util.List;
import javax.inject.Inject;
import org.example.backend.PhoneBookAddress;
import org.example.backend.PhoneBookAddress.AddressType;
import org.example.backend.PhoneBookGroup;
import org.example.backend.PhoneBookService;
import org.vaadin.teemu.switchui.Switch;
import org.vaadin.viritin.fields.ElementCollectionField;
import org.vaadin.viritin.fields.EnumSelect;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.grid.MGrid;
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
    DateField birthDate = new DateField("Birth date");
    Switch sendChristmasCard = new Switch("Send christmas card");
    MGrid<PhoneBookGroup> groupsGrid = new MGrid<>(PhoneBookGroup.class)
            .withFullHeight()
            .withWidth("250px")
            .withCaption("Groups");
    
    // The multiselect view of teh groupsGrid, used for namebased binding
    private final MultiSelect<PhoneBookGroup> groups;
    
    public static class AddressRow {
        EnumSelect<AddressType> type = (EnumSelect) new EnumSelect(AddressType.class).withWidth("6em");
        TextField street = new MTextField().withPlaceholder("street");
        TextField city = new MTextField().withPlaceholder("city").withWidth("6em");
        TextField zip = new MTextField().withPlaceholder("zip").withWidth("4em");
    }

    ElementCollectionField<PhoneBookAddress,List<PhoneBookAddress>> addresses = new ElementCollectionField<PhoneBookAddress,List<PhoneBookAddress>>(
            PhoneBookAddress.class, AddressRow.class);

    @Inject
    PhoneBookService service;

    public PhoneBookEntryForm() {
        super(PhoneBookEntry.class);
        groupsGrid.setColumns("name");
        groupsGrid.setHeaderVisible(false);
        groupsGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        groups = groupsGrid.asMultiSelect();
    }

    @Override
    public void setEntity(PhoneBookEntry entity) {
        // populate dynamic fields here, so that they are updated
        // also, in many real world cases, available option and other
        // configuration in the form might depend on the entity state
        groupsGrid.setItems(service.getGroups());
        super.setEntity(entity);
    }
    
    @Override
    protected Component createContent() {
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
                        groupsGrid
                ),
                addresses
        ).withMargin(new MMarginInfo(false, true));
    }

}
