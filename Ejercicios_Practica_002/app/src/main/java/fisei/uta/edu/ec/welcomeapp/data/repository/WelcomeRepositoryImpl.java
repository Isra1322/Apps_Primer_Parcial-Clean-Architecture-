package fisei.uta.edu.ec.welcomeapp.data.repository;

import fisei.uta.edu.ec.welcomeapp.data.datasource.WelcomeDataSource;
import fisei.uta.edu.ec.welcomeapp.domain.model.WelcomeMessage;
import fisei.uta.edu.ec.welcomeapp.domain.repository.WelcomeRepository;

import java.util.Objects;

/**
 * Implementación concreta del contrato {@link WelcomeRepository} en la capa de datos.
 * Aplica el principio de Inversión de Dependencias (DIP) y Responsabilidad Única (SRP).
 */
public class WelcomeRepositoryImpl implements WelcomeRepository {

    private final WelcomeDataSource welcomeDataSource;

    public WelcomeRepositoryImpl(WelcomeDataSource welcomeDataSource) {
        this.welcomeDataSource = Objects.requireNonNull(welcomeDataSource, "welcomeDataSource cannot be null");
    }

    @Override
    public WelcomeMessage getWelcomeMessage() {
        return welcomeDataSource.fetchWelcomeMessage();
    }
}
