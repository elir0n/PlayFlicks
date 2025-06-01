package com.example.myapplication.utils;

import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


 // Utility class for file-related operations.
public class FileManager {

    /**
     * Converts a given URI to a File object stored in the app's cache directory.
     *
     * @param context The application context.
     * @param uri The URI to be converted.
     * @return The File object created from the URI.
     * @throws Exception If an error occurs during file conversion.
     */
    public static File convertUriToFile(Context context, Uri uri) throws Exception {
        // Retrieve the file extension based on the URI's MIME type or file name
        String extension = getFileExtension(context, uri);

        // Create a temporary file in the cache directory with an appropriate extension
        String fileName = "temp_file" + (extension != null ? "." + extension : "");
        File file = new File(context.getCacheDir(), fileName);

        // Try-with-resources ensures streams are closed properly
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(file)) {

            if (inputStream == null) {
                throw new Exception("Unable to open InputStream from URI.");
            }

            // Read data from the input stream and write it to the output file
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return file;
    }

    /**
     * Retrieves the file extension from a given URI by checking its MIME type or file name.
     *
     * @param context The application context.
     * @param uri The URI from which to extract the file extension.
     * @return The file extension as a string, or null if it cannot be determined.
     */
    private static String getFileExtension(Context context, Uri uri) {
        String extension = null;

        // Obtain the MIME type from the URI
        String mimeType = context.getContentResolver().getType(uri);
        if (mimeType != null) {
            extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }

        // If MIME type is unavailable, attempt to extract the extension from the file name
        if (extension == null) {
            String fileName = uri.getLastPathSegment();
            if (fileName != null && fileName.contains(".")) {
                extension = fileName.substring(fileName.lastIndexOf('.') + 1);
            }
        }

        return extension;
    }
}
