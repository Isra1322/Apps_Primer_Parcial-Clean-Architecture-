package fisei.uta.edu.ec.doodlz.application.contracts.services;

import android.graphics.Bitmap;

/**
 * Contract for saving drawing bitmaps into external gallery or storage.
 */
public interface ImageStorageService {
    boolean saveImage(Bitmap bitmap, String displayName);
}
