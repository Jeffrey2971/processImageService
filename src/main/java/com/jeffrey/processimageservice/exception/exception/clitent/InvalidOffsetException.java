package com.jeffrey.processimageservice.exception.exception.clitent;

import com.jeffrey.processimageservice.entities.response.Point;

import java.util.ArrayList;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class InvalidOffsetException extends ClientException{
    public InvalidOffsetException(String message, ArrayList<Point> rectangles) {
        super(message);
    }


}
