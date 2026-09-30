package fisei.uta.edu.ec.doodlz.application.usecases;

import fisei.uta.edu.ec.doodlz.application.contracts.repositories.DrawingSettingsRepository;
import fisei.uta.edu.ec.doodlz.domain.entities.DrawingColor;

/**
 * Use case to update the stroke color.
 */
public final class UpdateColorUseCase {
    private final DrawingSettingsRepository repository;

    public UpdateColorUseCase(DrawingSettingsRepository repository) {
        this.repository = repository;
    }

    public void execute(DrawingColor color) {
        repository.updateColor(color);
    }
}
