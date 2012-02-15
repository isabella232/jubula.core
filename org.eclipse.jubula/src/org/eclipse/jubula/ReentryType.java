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
 * A representation of the literals of the enumeration '<em><b>Reentry Type</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipse.jubula.JubulaPackage#getReentryType()
 * @model
 * @generated
 */
public enum ReentryType implements Enumerator {
    /**
     * The '<em><b>Pause</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #PAUSE_VALUE
     * @generated
     * @ordered
     */
    PAUSE(0, "Pause", "Pause"),

    /**
     * The '<em><b>Continue</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #CONTINUE_VALUE
     * @generated
     * @ordered
     */
    CONTINUE(1, "Continue", "Continue"),

    /**
     * The '<em><b>Break</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #BREAK_VALUE
     * @generated
     * @ordered
     */
    BREAK(2, "Break", "Break"),

    /**
     * The '<em><b>Retry</b></em>' literal object.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #RETRY_VALUE
     * @generated
     * @ordered
     */
    RETRY(3, "Retry", "Retry");

    /**
     * The '<em><b>Pause</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Pause</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #PAUSE
     * @model name="Pause"
     * @generated
     * @ordered
     */
    public static final int PAUSE_VALUE = 0;

    /**
     * The '<em><b>Continue</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Continue</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #CONTINUE
     * @model name="Continue"
     * @generated
     * @ordered
     */
    public static final int CONTINUE_VALUE = 1;

    /**
     * The '<em><b>Break</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Break</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #BREAK
     * @model name="Break"
     * @generated
     * @ordered
     */
    public static final int BREAK_VALUE = 2;

    /**
     * The '<em><b>Retry</b></em>' literal value.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of '<em><b>Retry</b></em>' literal object isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @see #RETRY
     * @model name="Retry"
     * @generated
     * @ordered
     */
    public static final int RETRY_VALUE = 3;

    /**
     * An array of all the '<em><b>Reentry Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static final ReentryType[] VALUES_ARRAY =
        new ReentryType[] {
            PAUSE,
            CONTINUE,
            BREAK,
            RETRY,
        };

    /**
     * A public read-only list of all the '<em><b>Reentry Type</b></em>' enumerators.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static final List<ReentryType> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

    /**
     * Returns the '<em><b>Reentry Type</b></em>' literal with the specified literal value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ReentryType get(String literal) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            ReentryType result = VALUES_ARRAY[i];
            if (result.toString().equals(literal)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Reentry Type</b></em>' literal with the specified name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ReentryType getByName(String name) {
        for (int i = 0; i < VALUES_ARRAY.length; ++i) {
            ReentryType result = VALUES_ARRAY[i];
            if (result.getName().equals(name)) {
                return result;
            }
        }
        return null;
    }

    /**
     * Returns the '<em><b>Reentry Type</b></em>' literal with the specified integer value.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ReentryType get(int value) {
        switch (value) {
            case PAUSE_VALUE: return PAUSE;
            case CONTINUE_VALUE: return CONTINUE;
            case BREAK_VALUE: return BREAK;
            case RETRY_VALUE: return RETRY;
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
    private ReentryType(int value, String name, String literal) {
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
    
} //ReentryType
