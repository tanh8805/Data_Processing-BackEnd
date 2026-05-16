package com.example.data_processing_be.exception;

import java.util.*;
import java.io.*;

public class EmailAlreadyExistsException extends RuntimeException{
    public EmailAlreadyExistsException(String message){
        super(message);
    }
}
