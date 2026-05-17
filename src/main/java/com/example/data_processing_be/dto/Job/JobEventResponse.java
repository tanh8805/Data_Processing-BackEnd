package com.example.data_processing_be.dto.Job;

import lombok.Data;

import java.util.*;
import java.io.*;

@Data
public class JobEventResponse {
  private String type;
  private String conversation_id;
  private String user_id;
  private Object payload;
}
