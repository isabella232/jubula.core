/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.jubula;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;

/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Event Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.jubula.JubulaPackage#getEventType()
 * @model
 * @generated
 */
public enum EventType implements Enumerator {
    /**
     * The '<em><b>Component Not Found</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #COMPONENT_NOT_FOUND_VALUE
     * @generated
     * @ordered
     */
    COMPONENT_NOT_FOUND(0, "ComponentNotFound", "ComponentNotFound"),

    /**
     * The '<em><b>Action Error</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #ACTION_ERROR_VALUE
     * @generated
     * @ordered
     */
    ACTION_ERROR(1, "ActionError", "ActionError"),

    /**
     * The '<em><b>Configuration Error</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #CONFIGURATION_ERROR_VALUE
     * @generated
     * @ordered
     */
    CONFIGURATION_ERROR(2, "ConfigurationError", "ConfigurationError"),

    /**
     * The '<em><b>Check Failed</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #CHECK_FAILED_VALUE
     * @generated
     * @ordered
     */
    CHECK_FAILED(3, "CheckFailed", "CheckFailed");

    /**
     * The '<em><b>Component Not Found</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Component Not Found</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #COMPONENT_NOT_FOUND
     * @model name="ComponentNotFound"
     * @generated
     * @ordered
     */
    public static final int COMPONENT_NOT_FOUND_VALUE = 0;

    /**
     * The '<em><b>Action Error</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Action Error</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #ACTION_ERROR
     * @model name="ActionError"
     * @generated
     * @ordered
     */
    public static final int ACTION_ERROR_VALUE = 1;

    /**
     * The '<em><b>Configuration Error</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Configuration Error</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #CONFIGURATION_ERROR
     * @model name="ConfigurationError"
     * @generated
     * @ordered
     */
    public static final int CONFIGURATION_ERROR_VALUE = 2;

    /**
     * The '<em><b>Check Failed</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Check Failed</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #CHECK_FAILED
     * @model name="CheckFailed"
     * @generated
     * @ordered
     */
    public static final int CHECK_FAILED_VALUE = 3;

    /**
     * An array of all the '<em><b>Event Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final EventType[] VALUES_ARRAY =
        new EventType[] {
            COMPONENT_NOT_FOUND,
            ACTION_ERROR,
            CONFIGURATION_ERROR,
            CHECK_FAILED,
        };

    /**
     * A public read-only list of all the '<em><b>Event Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List<EventType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Event Type</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static EventType get(String literal) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            EventType result = VALUES_ARRAY[i];
            if (result.toString().equals(literal)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Event Type</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static EventType getByName(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            EventType result = VALUES_ARRAY[i];
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Event Type</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static EventType get(int value) {
        switch (value) {
            case COMPONENT_NOT_FOUND_VALUE: return COMPONENT_NOT_FOUND;
            case ACTION_ERROR_VALUE: return ACTION_ERROR;
            case CONFIGURATION_ERROR_VALUE: return CONFIGURATION_ERROR;
            case CHECK_FAILED_VALUE: return CHECK_FAILED;
        }
        return null;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final int value;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final String name;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private final String literal;

    /**
     * Only this class can construct instances.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EventType(int value, String name, String literal) {
        this.value = value;
        this.name = name;
        this.literal = literal;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getValue() {
      return value;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
      return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getLiteral() {
      return literal;
    }

    /**
     * Returns the literal value of the enumerator, which is its string representation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        return literal;
    }
    
} //EventType
