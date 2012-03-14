/**
 * 
 */
package org.eclipse.jubula.examples.aut.dvdtool;

/**
 * @author al
 *
 * The class maintains an artificial development state which is used to
 * trigger certain behaviors which simulated development states.
 */
public final class DevelopmentState {
    /**
     * Symbolic names for development states
     */
    public enum State { V0, V1, V2, V3 }
    
    /** Singleton instance variable */
    private static DevelopmentState instance;
    /** simulated development state */
    private State m_state;

    /**
     * private constructor required by the Singleton pattern.
     */
    private DevelopmentState() {
        m_state = State.V0;
    }
    
    /**
     * Singleton
     * @return The only instance of {@link DevelopmentState}
     */
    public static DevelopmentState instance() {
        if (instance == null) {
            instance = new DevelopmentState();         
        }
        return instance;
    }

    /**
     * @return the m_state
     */
    public State getState() {
        return m_state;
    }

    /**
     * @param state the m_state to set
     */
    public void setState(State state) {
        m_state = state;
    }
    
    /**
     * Check whether a certain state is set
     * @return true if the state from the method name is set
     */
    public boolean isV0() {
        return m_state == State.V0;
    }
    
    /**
     * Check whether a certain state is set
     * @return true if the state from the method name is set
     */
    public boolean isV1() {
        return m_state == State.V1;
    }
    
    /**
     * Check whether a certain state is set
     * @return true if the state from the method name is set
     */
    public boolean isV2() {
        return m_state == State.V2;
    }
    
    /**
     * Check whether a certain state is set
     * @return true if the state from the method name is set
     */
    public boolean isV3() {
        return m_state == State.V3;
    }
    
}
