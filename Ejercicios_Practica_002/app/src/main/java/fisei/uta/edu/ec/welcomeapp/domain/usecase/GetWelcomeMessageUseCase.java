package fisei.uta.edu.ec.welcomeapp.domain.usecase;

import fisei.uta.edu.ec.welcomeapp.domain.model.WelcomeMessage;
import fisei.uta.edu.ec.welcomeapp.domain.repository.WelcomeRepository;

import java.util.Objects;

/**
 * Caso de uso (Clean Architecture / SRP) para obtener el mensaje de bienvenida.
 * Encapsula la regla de negocio y depende exclusivamente de la abstracción {@link WelcomeRepository} (DIP).
 */
public class GetWelcomeMessageUseCase {

    private final WelcomeRepository welcomeRepository;

    public GetWelcomeMessageUseCase(WelcomeRepository welcomeRepository) {
        this.welcomeRepository = Objects.requireNonNull(welcomeRepository, "welcomeRepository cannot be null");
    }

    /**
     * Ejecuta el caso de uso para obtener el mensaje de bienvenida.
     *
     * @return Mensaje de bienvenida del dominio.
     */
    public WelcomeMessage execute() {
        WelcomeMessage message = welcomeRepository.getWelcomeMessage();
        if (message == null) {
            return new WelcomeMessage("Welcome to Android!");
        }
        return message;
    }
}
