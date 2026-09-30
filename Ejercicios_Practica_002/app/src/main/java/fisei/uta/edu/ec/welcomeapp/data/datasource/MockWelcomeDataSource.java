package fisei.uta.edu.ec.welcomeapp.data.datasource;

import fisei.uta.edu.ec.welcomeapp.domain.model.WelcomeMessage;

/**
 * Fuente de datos simulada (Mock/En memoria) que implementa {@link WelcomeDataSource}.
 * Cumple con el principio Open/Closed (OCP), permitiendo alternar o sustituir fuentes
 * sin modificar la lógica de los repositorios ni de los casos de uso.
 */
public class MockWelcomeDataSource implements WelcomeDataSource {

    @Override
    public WelcomeMessage fetchWelcomeMessage() {
        return new WelcomeMessage("Welcome to Android!");
    }
}
