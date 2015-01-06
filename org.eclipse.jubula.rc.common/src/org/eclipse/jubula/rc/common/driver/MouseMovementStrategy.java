/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.common.driver;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;

/**
 * Encapsulates the pathing for the mouse pointer during testing. 
 *
 * @author BREDEX GmbH
 * @created Oct 13, 2008
 */
public class MouseMovementStrategy {

    /**
     * Private constructor for utility class
     */
    private MouseMovementStrategy() {
        // Do nothing
    }
       
    /**
     * 
     * @param from The point from which the mouse pointer is being moved.
     *             May not be <code>null</code>. Coordinates must be 
     *             non-negative.
     * @param to The point to which the mouse pointer is being moved.
     *           May not be <code>null</code>. Coordinates must be 
     *           non-negative.
     * @param isMoveInSteps <code>true</code> if the movement strategy should 
     *                      be executed in steps. Otherwise, <code>false</code>.
     * @param firstHorizontal <code>true</code> if the movement strategy should
     *                      be executed using first the x axis. Otherwise, 
     *                      <code>false</code>.
     * @return an array of <code>Point</code>s indicating the path the pointer
     *         should follow in order to reach the destination point 
     *         <code>to</code>. This path includes the point <code>to</code>, 
     *         but does not contain <code>from</code>.
     */
    public static Point [] getMovementPath(Point from, Point to, 
            boolean isMoveInSteps, boolean firstHorizontal) {
        Validate.notNull(from, "Initial point must not be null."); //$NON-NLS-1$
        Validate.notNull(to, "End point must not be null."); //$NON-NLS-1$
        Validate.isTrue(to.x >= 0, "End x-coordinate must not be negative."); //$NON-NLS-1$
        Validate.isTrue(to.y >= 0, "End y-coordinate must not be negative."); //$NON-NLS-1$
        Validate.isTrue(from.x >= 0, "Initial x-coordinate must not be negative."); //$NON-NLS-1$
        Validate.isTrue(from.y >= 0, "Initial y-coordinate must not be negative."); //$NON-NLS-1$
        
        if (!isMoveInSteps) {
            // Adjacent point followed by target point
            return new Point [] {new Point(to.x - 1, to.y - 1), to};
        }
        
        List<Point> path = new ArrayList<Point>();
        int [] xCoords = getMovementPath(from.x, to.x);
        int [] yCoords = getMovementPath(from.y, to.y);
        if (firstHorizontal) {
            for (int i = 0; i < xCoords.length; i++) {
                path.add(new Point(xCoords[i], from.y));
            }
            for (int i = 0; i < yCoords.length; i++) {
                path.add(new Point(to.x, yCoords[i]));
            }            
        } else {
            for (int i = 0; i < yCoords.length; i++) {
                path.add(new Point(from.x, yCoords[i]));
            }
            for (int i = 0; i < xCoords.length; i++) {
                path.add(new Point(xCoords[i], to.y));
            }
        }

        if (path.isEmpty() || !to.equals(path.get(path.size() - 1))) {
            path.add(new Point(to));
        }

        List<Point> optimizedPath = optimizePath(from, to, firstHorizontal,
                path, xCoords, yCoords);
        
        return optimizedPath.toArray(new Point [optimizedPath.size()]);
    }

    /**
     * @param from mouse move start point
     * @param to from mouse move end point
     * @param firstHorizontal whether to move horizontal first
     * @param path the path from start to end
     * @param xCoords x-coordinates from start to end
     * @param yCoords y-coordinates from start to end
     * @return optimized path containing not all points (more accurate near start and end).
     */
    private static List<Point> optimizePath(Point from, Point to,
            boolean firstHorizontal, List<Point> path, int[] xCoords,
            int[] yCoords) {
        List<Point> optimizedPath = new ArrayList<Point>();
        int totalSteps = path.size();
        int stepFactor = 10;
        int amountOfSteps = 1 + Math.round(totalSteps / stepFactor);
        boolean turningPointInserted = false;
        
        for (int i = 1; i < amountOfSteps; i++) {
            double distance = (-2d / Math.pow(amountOfSteps, 3))
                    * Math.pow(i, 3) + (3d / Math.pow(amountOfSteps, 2))
                    * Math.pow(i, 2);
            int index = Math.min((int) Math.round((distance * totalSteps)),
                    totalSteps - 1);
            Point nextPoint = path.get(index);
            if (!turningPointInserted) {
                if (firstHorizontal) {
                    float turningPointDistance = ((float) xCoords.length)
                            / totalSteps;
                    if (distance > turningPointDistance) {
                        optimizedPath.add(new Point(to.x, from.y));
                        turningPointInserted = true;
                    }
                } else {
                    float turningPointDistance = ((float) yCoords.length)
                            / totalSteps;
                    if (distance > turningPointDistance) {
                        optimizedPath.add(new Point(from.x, to.y));
                        turningPointInserted = true;
                    }
                }
            }
            // Make sure that next point is not already in optimized path
            if (!optimizedPath.isEmpty()
                    && !(optimizedPath.get(optimizedPath.size() - 1)
                    .equals(nextPoint))) {
                optimizedPath.add(nextPoint);
            }
        }
        // Make sure that end point is not already in optimized path
        Point end = new Point(to.x, to.y);
        if (optimizedPath.isEmpty()
                || (!(optimizedPath.get(optimizedPath.size() - 1)
                        .equals(end)))) {
            optimizedPath.add(end);
        }
        return optimizedPath;
    }

    /**
     * 
     * @param from The number at which we are starting.
     * @param to The number to which the path must lead.
     * @return a path between the given numbers.
     */
    private static int [] getMovementPath(int from, int to) {
        int diff = to - from;
        
        // Should the movement be in the positive, or the negative "direction"
        int direction = diff != 0 ? diff / Math.abs(diff) : 1;

        int current = from;
        int [] retVal = new int [Math.abs(diff)];

        for (int i = 0; i < retVal.length; i++) {
            current += direction;
            retVal[i] = current;
        }

        return retVal;
    }
}
