package fisei.uta.edu.ec.welcomeapp.domain.model;

import java.util.Objects;

/**
 * Entidad del dominio que representa un mensaje de bienvenida simplificado.
 * Contiene únicamente el mensaje de texto principal.
 */
public class WelcomeMessage {

    private final String message;

    public WelcomeMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WelcomeMessage that = (WelcomeMessage) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public String toString() {
        return "WelcomeMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
