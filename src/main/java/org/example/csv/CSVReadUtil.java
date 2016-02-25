package org.example.csv;

import com.opencsv.CSVReader;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class CSVReadUtil {

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

    public static class CSV {
        private int columns;
        private int rows;

        List<String> header;
        List<List<String>> content;

        public CSV(List<String[]> content ) {
            this( generateHeader(content), content);
        }

        public CSV(String[] header, List<String[]> content ) {
            this.header = Arrays.asList(header);

            this.content = new ArrayList<>(content.size());
            content.forEach( array -> this.content.add( Arrays.asList(array)));
        }

        private static String[] generateHeader(List<String[]> content) {
            Optional<Integer> optCols = checkEqualNumberOfColumns(content);
            String[] header;
            if (optCols.isPresent()) {
                int n = optCols.get();
                header = new String[n];
                for (int i = 0; i < n; i++) {
                    header[i] = "Column " + Integer.toString(i);
                }
            } else {
                throw new IllegalArgumentException(("Column size is not identical for each row."));
            }
            return header;
        }

        private static Optional<Integer> checkEqualNumberOfColumns(List<String[]> data) {
            if (data.size() == 0) {
                return Optional.empty();
            }

            int expected = data.get(0).length;
            return data.stream().filter( p -> p.length != expected ).count() > 0
                    ? Optional.of(expected) : Optional.empty();
        }

        public List<String> getColumnNames() {
            return header;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append( fixedLengthFormat( header ) );
            content.forEach(row -> sb.append( fixedLengthFormat( row ) ));

            return sb.toString();

        }

        public static String fixedLengthFormat(List<String> ss) {
            return ss.stream()
                    .map(s -> turnIntoFixedSize(s, 20))
                    .collect(Collectors.joining("|", "", ""));
        }

        public static String turnIntoFixedSize(String input, int s) {
            int n = input.length();

            return (n <= s) ? input + createNSpaces( n-s ) : input.substring(0, s - 1) + "…";
        }

        public static String createNSpaces( int n ) {


            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < n; i++) {
                sb.append(" ");
            }
            return sb.toString();
        }

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