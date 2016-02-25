package org.example;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.UIScoped;
import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import org.example.backend.PhoneBookEntry;
import org.example.backend.PhoneBookService;
import org.example.csv.CSVReadUtil;
import org.example.csv.FileBasedUploadReceptor;
import org.vaadin.cdiviewmenu.ViewMenuItem;
import org.vaadin.viritin.label.Header;
import org.vaadin.viritin.layouts.MVerticalLayout;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;

@UIScoped
@CDIView("csv_imports")
@ViewMenuItem(order = 2)
public class CSVImportView extends MVerticalLayout implements View {

    @Inject
    PhoneBookService service;
    
    Upload upload = new Upload();
    Grid grid;
    Button cancelButton;
    Button saveButton;
    Header header;
    
    @PostConstruct
    void init() {

        header = new Header("CSV Import");
        
        /**
         * External handler, after upload was successful.
         */
        Consumer<FileBasedUploadReceptor.FileAndInfo> consumeReader = reader -> {
            System.out.println(reader);
            try {
                IndexedContainer csvContainer = CSVReadUtil.buildContainerFromCSV(new FileReader(reader.getFile()));
                // this is intentional, as attaching new datasource to existing grid seems cause problems in this Vaadin version
                grid = new Grid();
                grid.setWidth(100, Unit.PERCENTAGE);
                grid.setContainerDataSource(csvContainer);
                boolean columnNamesValid = checkEntries(grid.getContainerDataSource());
                if ( !columnNamesValid ) {
                    Notification.show("Save is not possible. Schema ( column names ) of CSV not correct.", Notification.Type.WARNING_MESSAGE);    
                }
                displayUploadedData( columnNamesValid );
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        FileBasedUploadReceptor fileBasedUploadReceptor = new FileBasedUploadReceptor(consumeReader);
        upload.setReceiver(fileBasedUploadReceptor);
        upload.addSucceededListener(fileBasedUploadReceptor);
        upload.setImmediate(true);
        upload.setButtonCaption("Upload CSV.");

        saveButton = new Button("Save");
        saveButton.addClickListener(clickEvent -> {
            Container.Indexed container = grid.getContainerDataSource();
            List<PhoneBookEntry> entries = createEntries(container);
            saveEntries( entries );
            Notification.show( entries.size() + " entries have been imported.");
            displayUpload();
        });
        
        cancelButton = new Button("Cancel");
        cancelButton.addClickListener(clickEvent -> {
                    displayUpload();
                }
        );

        displayUpload();
    }

    // ==========================================================
    // ===== View related methods
    // ==========================================================
    public void displayUpload() {
        removeAllComponents();
        
        Label label = new Label("Please upload your CSV, that contains columns 'name', 'number' and 'email'");
        
        addComponent(header);
        addComponent(label);
        addComponent(upload);
    }
    
    public void displayUploadedData( boolean activateSave ) {
        removeAllComponents();
        
        saveButton.setEnabled( activateSave );

        HorizontalLayout hl = new HorizontalLayout();
        hl.addComponent(cancelButton);
        hl.addComponent(saveButton);
        
        HorizontalLayout bottom = new HorizontalLayout();
        bottom.setWidth(100, Unit.PERCENTAGE);
        bottom.addComponent(hl);
        bottom.setComponentAlignment(hl, Alignment.MIDDLE_RIGHT);

        addComponent(header);
        addComponent(grid);
        setExpandRatio(grid, 1);
        addComponent(bottom);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        // nothing to do on enter
    }

    // ==========================================================
    // ===== Entry related methods
    // ==========================================================
    // TODO: To be moved to separate class, but left here to keep everything "close together".

    private String NAME = "name";
    private String NUMBER = "number";
    private String EMAIL = "email";

    /**
     * Check if all property ids NAME, NUMBER and EMAIL are present in given container (as read from CSV).
     * @param containerDataSource
     * @return true if so
     */
    private boolean checkEntries(Container.Indexed containerDataSource) {
        Collection<String> pids = (Collection<String>)containerDataSource.getContainerPropertyIds();

        Set<String> actualSchema = new HashSet<>(pids);
        Set<String> expectedSchema = new HashSet<>(Arrays.asList(NAME, NUMBER, EMAIL));
        
        Set<String> intersect = (new HashSet<>(actualSchema));
        intersect.retainAll(expectedSchema);
        
        if (actualSchema.size() == 3) {
            return true; // everything is fine
        } else {
            return false;
        }
    }

    /**
     * Create entries based on container content.
     * 
     * @param container use for construction of {@link PhoneBookEntry}s.
     * @return list of entries
     */
    private List<PhoneBookEntry> createEntries(Container.Indexed container ) {

        List<PhoneBookEntry> entries = new ArrayList<>();
        int n = container.size();
        
        // TODO: Introduce real mapping functionality
        for ( Object itemId : container.getItemIds() ) {
            String name = (String) container.getContainerProperty(itemId, NAME).getValue();
            String number = (String) container.getContainerProperty(itemId, NUMBER).getValue();
            String email = (String) container.getContainerProperty(itemId, EMAIL).getValue();
            PhoneBookEntry entry = new PhoneBookEntry(name, number, email);
            entries.add(entry);
        }
        return entries;
    }

    /**
     * Persist given {@link PhoneBookEntry}s. 
     * @param entries are the entries to persist.
     */
    private int saveEntries( List<PhoneBookEntry> entries ) {
        for (PhoneBookEntry entry : entries) {
            service.save(entry);
        }
        return entries.size();
    }
}
