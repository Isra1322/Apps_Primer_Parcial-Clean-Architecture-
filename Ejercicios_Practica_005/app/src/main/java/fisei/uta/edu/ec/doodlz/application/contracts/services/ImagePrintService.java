package fisei.uta.edu.ec.doodlz.application.contracts.services;

import android.graphics.Bitmap;

/**
 * Contract for printing drawing bitmaps or exporting them to PDF format.
 */
public interface ImagePrintService {
    boolean printImage(String jobName, Bitmap bitmap);
}
