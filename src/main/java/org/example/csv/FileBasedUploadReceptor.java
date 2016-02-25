package org.example.csv;

import com.vaadin.ui.Upload;

import java.io.*;
import java.util.function.Consumer;

/**
 * Purpose of this class: A helper class to allow storage of upload (via com.vaadin.ui.Upload) in a file.
 */
public class FileBasedUploadReceptor implements Upload.Receiver, Upload.SucceededListener {

    private File tempFile;

    private Consumer<FileAndInfo> fileConsumer;

    public static class FileAndInfo {
        private File file;
        private String filename;
        private String mimetype;

        public FileAndInfo(File file, String filename, String mimetype) {
            this.file = file;
            this.filename = filename;
            this.mimetype = mimetype;
        }

        public File getFile() {
            return file;
        }

        public String getFilename() {
            return filename;
        }

        public String getMimetype() {
            return mimetype;
        }
    }

    /**
     * Constructor.
     * @param fileConsumer is a (consuming) function of type ( Reader -> () ) that is called, when upload was successful.
     */
    public FileBasedUploadReceptor(Consumer<FileAndInfo> fileConsumer) {
        this.fileConsumer = fileConsumer;

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
        System.out.println("Got file '" + event.getFilename() +
                    "' and mimetype '" + event.getMIMEType() + "'.");
        fileConsumer.accept( new FileAndInfo(tempFile, event.getFilename(), event.getMIMEType()) );
    }
}