package fisei.uta.edu.ec.doodlz.infrastructure.repositories;

import javax.inject.Inject;
import javax.inject.Singleton;

import fisei.uta.edu.ec.doodlz.application.contracts.repositories.DrawingSettingsRepository;
import fisei.uta.edu.ec.doodlz.domain.entities.DrawingColor;
import fisei.uta.edu.ec.doodlz.domain.entities.DrawingSettings;
import fisei.uta.edu.ec.doodlz.domain.entities.LineWidth;

/**
 * Thread-safe in-memory repository to hold the active drawing settings.
 */
@Singleton
public final class InMemoryDrawingSettingsRepository implements DrawingSettingsRepository {
    private volatile DrawingSettings settings;

    @Inject
    public InMemoryDrawingSettingsRepository() {
        this.settings = DrawingSettings.createDefault();
    }

    @Override
    public synchronized DrawingSettings getSettings() {
        return settings;
    }

    @Override
    public synchronized void updateColor(DrawingColor color) {
        if (color != null) {
            this.settings = settings.withColor(color);
        }
    }

    @Override
    public synchronized void updateLineWidth(LineWidth lineWidth) {
        if (lineWidth != null) {
            this.settings = settings.withLineWidth(lineWidth);
        }
    }
}
