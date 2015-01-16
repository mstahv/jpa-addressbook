package org.example;


import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;
import org.example.backend.PhoneBookGroup;
import org.vaadin.viritin.fields.MTextField;
import org.vaadin.viritin.form.AbstractForm;
import org.vaadin.viritin.layouts.MFormLayout;
import org.vaadin.viritin.layouts.MMarginInfo;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 * This class introduces a Form to edit PhoneBookEntry pojos. It is a good habit
 * to separate logical pieces of your UI code to classes. This will improve
 * re-usability, readability, maintainability, testability.
 *
 * @author Matti Tahvonen <matti@vaadin.com>
 */
public class PhoneBookGroupForm extends AbstractForm<PhoneBookGroup> {

    TextField name = new MTextField("Name");

    public PhoneBookGroupForm() {
        setEagerValidation(true);
    }

    @Override
    protected Component createContent() {
        return new MVerticalLayout(
                new MFormLayout(
                        name
                ).withMargin(false),
                getToolbar()
        ).withMargin(new MMarginInfo(false, true));
    }

}
