package fisei.uta.edu.ec.welcomeapp.data.datasource;

import fisei.uta.edu.ec.welcomeapp.domain.model.WelcomeMessage;

/**
 * Interfaz para las fuentes de datos (locales, remotas, base de datos o en memoria).
 * Aplica el principio de segregación de interfaces (ISP) y de inversión de dependencias (DIP).
 */
public interface WelcomeDataSource {

    /**
     * Suministra los datos del mensaje de bienvenida desde el origen concreto.
     *
     * @return instancia de {@link WelcomeMessage}
     */
    WelcomeMessage fetchWelcomeMessage();
}
