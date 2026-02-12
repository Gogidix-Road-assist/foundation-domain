package com.gogidix.rapidassist.ai.imagerecognition.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model representing a bounding box around a detected object.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoundingBox {

    private Double x;
    private Double y;
    private Double width;
    private Double height;

    /**
     * Business logic: Get center point
     */
    public Point getCenter() {
        if (x == null || y == null || width == null || height == null) {
            return null;
        }
        return new Point(x + width / 2, y + height / 2);
    }

    /**
     * Business logic: Get area
     */
    public double getArea() {
        return width != null && height != null ? width * height : 0;
    }

    /**
     * Business logic: Check if point is inside bounding box
     */
    public boolean contains(Point point) {
        if (point == null || x == null || y == null || width == null || height == null) {
            return false;
        }
        return point.getX() >= x && point.getX() <= x + width &&
               point.getY() >= y && point.getY() <= y + height;
    }

    @Data
    @AllArgsConstructor
    public static class Point {
        private Double x;
        private Double y;
    }
}
