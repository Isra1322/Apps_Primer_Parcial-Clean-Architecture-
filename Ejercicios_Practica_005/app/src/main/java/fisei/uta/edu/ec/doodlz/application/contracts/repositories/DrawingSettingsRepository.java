package fisei.uta.edu.ec.doodlz.application.contracts.repositories;

import fisei.uta.edu.ec.doodlz.domain.entities.DrawingColor;
import fisei.uta.edu.ec.doodlz.domain.entities.DrawingSettings;
import fisei.uta.edu.ec.doodlz.domain.entities.LineWidth;

/**
 * Contract for managing current drawing settings persistence in memory or storage.
 */
public interface DrawingSettingsRepository {
    DrawingSettings getSettings();
    void updateColor(DrawingColor color);
    void updateLineWidth(LineWidth lineWidth);
}
