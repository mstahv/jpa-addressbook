package org.example;

import com.vaadin.cdi.CDIView;
import com.vaadin.cdi.UIScoped;
import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import org.example.backend.PhoneBookEntry;
import org.example.backend.PhoneBookService;
import org.example.csv.CSVUtil;
import org.vaadin.cdiviewmenu.ViewMenuItem;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@UIScoped
@CDIView("csv_imports")
@ViewMenuItem(order = 2)
public class CSVImportView extends VerticalLayout implements View {

    @Inject
    PhoneBookService service;
    
    Upload upload = new Upload();
    
    Grid grid = new Grid();
    Button saveButton;

    static class UploadReceptor implements Upload.Receiver, Upload.SucceededListener {

        private File tempFile;
        
        private Consumer<Reader> reader;
        
        public UploadReceptor( Consumer<Reader> reader) {
            this.reader = reader;
            
            try {
                tempFile = File.createTempFile("temp", ".csv");
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Can't create temporary file.");
            }
      }

        @Override
        public OutputStream receiveUpload(String s, String s1) {
            try {
                return new FileOutputStream(tempFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("Temporary file not found.");
            }
        }

        @Override
        public void uploadSucceeded(Upload.SucceededEvent event) {
            try {
                System.out.println("Got file '" + event.getFilename() + 
                        "' and mimetype '" + event.getMIMEType() + "'.");
                FileReader fileReader = new FileReader(tempFile);
                reader.accept( fileReader );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException("Problem with upload.");
            }
            
        }
    }
    
    
    static class SaveableGrid extends VerticalLayout {
        private Grid grid;
        private Button cancelButton;
        private Button saveButton;

        public SaveableGrid() {
            grid = new Grid();
            saveButton = new Button("Save");
            cancelButton = new Button("Cancel");
        }
    }
    
    @PostConstruct
    void init() {

        /**
         * External handler, after upload was successful.
         */
        Consumer<Reader> consumeReader = reader -> {
            System.out.println(reader);
            try {
                IndexedContainer csvContainer = CSVUtil.buildContainerFromCSV(reader);
                grid.setContainerDataSource(csvContainer);
                grid.setVisible(true);
                saveButton.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        UploadReceptor uploadReceptor = new UploadReceptor(consumeReader);
        upload.setReceiver( uploadReceptor );
        upload.setCaption("Upload CSV");
        upload.addSucceededListener(uploadReceptor);
        
        saveButton = new Button("Save");
        saveButton.addClickListener(clickEvent -> {
            List<PhoneBookEntry> entries = createEntries(grid.getContainerDataSource());
            saveEntries( entries );
        });

        grid.setVisible(false);
        saveButton.setVisible(false);
        
        addComponent(upload);
        addComponent(grid);
        addComponent(saveButton);
    }

    private List<PhoneBookEntry> createEntries(Container.Indexed container ) {
        String NAME = "name";
        String NUMBER = "number";
        String EMAIL = "email";
        
        List<PhoneBookEntry> entries = new ArrayList<>();
        int n = container.size();
        
        // TODO: Improve hard-wired error-prone mapping
        for ( Object itemId : container.getItemIds() ) {
            String name = (String) container.getContainerProperty(itemId, NAME).getValue();
            String number = (String) container.getContainerProperty(itemId, NUMBER).getValue();
            String email = (String) container.getContainerProperty(itemId, EMAIL).getValue();
            PhoneBookEntry entry = new PhoneBookEntry(name, number, email);
            entries.add(entry);
        }
        return entries;
    }
    
    private void saveEntries( List<PhoneBookEntry> entries ) {
        System.out.println("Before saving entries.");
        for (PhoneBookEntry entry : entries) {
            service.save(entry);
        }
        System.out.println("After saving entries.");
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
    }
}
