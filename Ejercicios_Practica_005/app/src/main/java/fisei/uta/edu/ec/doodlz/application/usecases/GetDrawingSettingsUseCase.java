package fisei.uta.edu.ec.doodlz.application.usecases;

import fisei.uta.edu.ec.doodlz.application.contracts.repositories.DrawingSettingsRepository;
import fisei.uta.edu.ec.doodlz.domain.entities.DrawingSettings;

/**
 * Use case to retrieve current drawing settings.
 */
public final class GetDrawingSettingsUseCase {
    private final DrawingSettingsRepository repository;

    public GetDrawingSettingsUseCase(DrawingSettingsRepository repository) {
        this.repository = repository;
    }

    public DrawingSettings execute() {
        return repository.getSettings();
    }
}
