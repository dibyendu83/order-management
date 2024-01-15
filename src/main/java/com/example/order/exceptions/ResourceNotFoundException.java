package com.example.order.exceptions;

import lombok.NoArgsConstructor;


@NoArgsConstructor
public class ResourceNotFoundException extends Exception{
    public ResourceNotFoundException(String msg){
        super(msg);
    }
}
