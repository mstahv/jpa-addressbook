
package org.example;

import com.vaadin.cdi.CDIView;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import javax.annotation.PostConstruct;
import org.vaadin.cdiviewmenu.ViewMenuItem;
import org.vaadin.viritin.label.RichText;
import org.vaadin.viritin.layouts.MVerticalLayout;

@CDIView
@ViewMenuItem(icon = VaadinIcons.LIFEBUOY, order = ViewMenuItem.END)
public class AboutView extends MVerticalLayout implements View {
    
    @PostConstruct
    void init() {
        addComponent(new RichText().withMarkDownResource("/about.md"));
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }

}
