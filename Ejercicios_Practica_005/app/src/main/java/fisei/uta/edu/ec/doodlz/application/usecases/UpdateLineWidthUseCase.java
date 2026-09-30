package fisei.uta.edu.ec.doodlz.application.usecases;

import fisei.uta.edu.ec.doodlz.application.contracts.repositories.DrawingSettingsRepository;
import fisei.uta.edu.ec.doodlz.domain.entities.LineWidth;

/**
 * Use case to update the stroke line width.
 */
public final class UpdateLineWidthUseCase {
    private final DrawingSettingsRepository repository;

    public UpdateLineWidthUseCase(DrawingSettingsRepository repository) {
        this.repository = repository;
    }

    public void execute(LineWidth lineWidth) {
        repository.updateLineWidth(lineWidth);
    }
}
