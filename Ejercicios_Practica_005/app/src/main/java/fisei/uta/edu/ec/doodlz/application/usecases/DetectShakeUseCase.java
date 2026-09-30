package fisei.uta.edu.ec.doodlz.application.usecases;

/**
 * Use case that evaluates accelerometer sensor readings and detects shake gestures.
 * Pure Java logic, completely decoupled from Android UI and hardware sensors.
 */
public final class DetectShakeUseCase {
    public static final float DEFAULT_GRAVITY = 9.80665f;
    public static final float DEFAULT_THRESHOLD = 4.0f;

    private final float threshold;
    private float acceleration;
    private float currentAcceleration;
    private float lastAcceleration;

    public DetectShakeUseCase() {
        this(DEFAULT_THRESHOLD);
    }

    public DetectShakeUseCase(float threshold) {
        this.threshold = threshold;
        reset();
    }

    public void reset() {
        acceleration = 0.0f;
        currentAcceleration = DEFAULT_GRAVITY;
        lastAcceleration = DEFAULT_GRAVITY;
    }

    public boolean evaluate(float x, float y, float z) {
        lastAcceleration = currentAcceleration;
        currentAcceleration = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = currentAcceleration - lastAcceleration;
        acceleration = acceleration * 0.9f + delta;

        if (acceleration > threshold) {
            acceleration = 0.0f;
            return true;
        }
        return false;
    }

    public float getAcceleration() {
        return acceleration;
    }
}
