package fisei.uta.edu.ec.flagquiz.data.repository;

import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class FlagRepository {
    private static final String TAG = "FlagQuiz Activity";
    private final AssetManager assets;

    public FlagRepository(AssetManager assets) {
        this.assets = assets;
    }

    public List<String> getFlags(Set<String> regions) {
        List<String> flags = new ArrayList<>();
        try {
            if (regions != null) {
                for (String region : regions) {
                    String[] paths = assets.list(region);
                    if (paths != null) {
                        for (String path : paths) {
                            flags.add(path.replace(".png", ""));
                        }
                    }
                }
            }
        } catch (IOException exception) {
            Log.e(TAG, "Error loading image file names", exception);
        }
        return flags;
    }

    public InputStream openFlag(String region, String flagName) throws IOException {
        return assets.open(region + "/" + flagName + ".png");
    }
}
