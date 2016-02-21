package org.example.csv;

import com.opencsv.CSVReader;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import java.io.*;


public class CSVUtil {

    /**
     * Uses http://opencsv.sourceforge.net/ to read the entire contents of a CSV
     * file, and creates an IndexedContainer from it
     *
     * @param reader
     * @return
     * @throws IOException
     */
    public static IndexedContainer buildContainerFromCSV(Reader reader) throws IOException {
        IndexedContainer container = new IndexedContainer();
        CSVReader csvReader = new CSVReader(reader);
        String[] columnHeaders = null;
        String[] record;
        while ((record = csvReader.readNext()) != null) {
            // Headerline
            if (columnHeaders == null) {
                columnHeaders = record;
                addItemProperties(container, columnHeaders);
            } else {
                addItem(container, columnHeaders, record);
            }
        }
        return container;
    }


    /**
     * Set's up the item property ids for the container. Each is a String (of course,
     * you can create whatever data type you like, but I guess you need to parse the whole file
     * to work it out)
     *
     * @param container The container to set
     * @param columnHeaders The column headers, i.e. the first row from the CSV file
     */
    private static void addItemProperties(IndexedContainer container, String[] columnHeaders) {
        for (String propertyName : columnHeaders) {
            container.addContainerProperty(propertyName, String.class, null);
        }
    }

    /**
     * Adds an item to the given container, assuming each field maps to it's corresponding property id.
     * Again, note that I am assuming that the field is a string.
     *
     * @param container
     * @param propertyIds
     * @param fields
     */
    private static void addItem(IndexedContainer container, String[] propertyIds, String[] fields) {
        if (propertyIds.length != fields.length) {
            throw new IllegalArgumentException("Hmmm - Different number of columns to fields in the record");
        }
        Object itemId = container.addItem();
        Item item = container.getItem(itemId);
        for (int i = 0; i < fields.length; i++) {
            String propertyId = propertyIds[i];
            String field = fields[i];
            item.getItemProperty(propertyId).setValue(field);
        }
    }
}