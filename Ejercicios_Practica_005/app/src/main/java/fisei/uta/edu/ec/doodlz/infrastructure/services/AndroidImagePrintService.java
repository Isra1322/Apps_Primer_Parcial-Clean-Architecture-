package fisei.uta.edu.ec.doodlz.infrastructure.services;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import fisei.uta.edu.ec.doodlz.application.contracts.services.ImagePrintService;

/**
 * Concrete implementation of ImagePrintService that exports drawings to PDF documents.
 */
@Singleton
public final class AndroidImagePrintService implements ImagePrintService {
    private static final String TAG = "AndroidImagePrintService";
    private final Context context;

    @Inject
    public AndroidImagePrintService(@ApplicationContext Context context) {
        this.context = context;
    }

    @Override
    public boolean printImage(String jobName, Bitmap bitmap) {
        if (bitmap == null || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
            return false;
        }

        String baseName = (jobName != null && !jobName.trim().isEmpty())
                ? jobName
                : "Doodlz_" + System.currentTimeMillis();

        String fileName = baseName.toLowerCase().endsWith(".pdf") ? baseName : baseName + ".pdf";

        PdfDocument pdfDocument = null;
        try {
            pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                    bitmap.getWidth(),
                    bitmap.getHeight(),
                    1
            ).create();

            PdfDocument.Page page = pdfDocument.startPage(pageInfo);
            Canvas canvas = page.getCanvas();
            canvas.drawBitmap(bitmap, 0, 0, null);
            pdfDocument.finishPage(page);

            return savePdfDocument(pdfDocument, fileName);
        } catch (Exception e) {
            Log.e(TAG, "Error generating PDF: " + e.getMessage(), e);
            return false;
        } finally {
            if (pdfDocument != null) {
                pdfDocument.close();
            }
        }
    }

    private boolean savePdfDocument(PdfDocument pdfDocument, String fileName) {
        ContentResolver resolver = context.getContentResolver();

        // Strategy 1: Android Q+ MediaStore Downloads (using Environment.DIRECTORY_DOWNLOADS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Doodlz");

                Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    try (OutputStream out = resolver.openOutputStream(uri)) {
                        if (out != null) {
                            pdfDocument.writeTo(out);
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Failed inserting PDF into MediaStore.Downloads: " + e.getMessage());
            }

            // Strategy 2: Android Q+ MediaStore Files (using Environment.DIRECTORY_DOCUMENTS)
            try {
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/Doodlz");

                Uri uri = resolver.insert(MediaStore.Files.getContentUri("external"), values);
                if (uri != null) {
                    try (OutputStream out = resolver.openOutputStream(uri)) {
                        if (out != null) {
                            pdfDocument.writeTo(out);
                            return true;
                        }
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Failed inserting PDF into MediaStore.Files: " + e.getMessage());
            }
        }

        // Strategy 3: Public External Storage Directory (Downloads/Doodlz)
        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File doodlzDir = new File(downloadsDir, "Doodlz");
            if (doodlzDir.exists() || doodlzDir.mkdirs()) {
                File pdfFile = new File(doodlzDir, fileName);
                try (OutputStream out = new FileOutputStream(pdfFile)) {
                    pdfDocument.writeTo(out);
                    return true;
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed saving PDF to public Downloads directory: " + e.getMessage());
        }

        // Strategy 4: App-specific External Storage Directory (Always granted without permissions)
        try {
            File appDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (appDir == null) {
                appDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            }
            if (appDir != null) {
                File doodlzDir = new File(appDir, "Doodlz");
                if (doodlzDir.exists() || doodlzDir.mkdirs()) {
                    File pdfFile = new File(doodlzDir, fileName);
                    try (OutputStream out = new FileOutputStream(pdfFile)) {
                        pdfDocument.writeTo(out);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed saving PDF to app external directory: " + e.getMessage(), e);
        }

        return false;
    }
}
