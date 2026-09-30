package fisei.uta.edu.ec.welcomeapp.domain.repository;

import fisei.uta.edu.ec.welcomeapp.domain.model.WelcomeMessage;

/**
 * Contrato de repositorio en la capa de Dominio (Dependency Inversion Principle - DIP).
 * Define las operaciones abstractas para obtener y manipular mensajes de bienvenida.
 */
public interface WelcomeRepository {

    /**
     * Obtiene el mensaje de bienvenida actual.
     *
     * @return instancia de {@link WelcomeMessage}
     */
    WelcomeMessage getWelcomeMessage();
}
