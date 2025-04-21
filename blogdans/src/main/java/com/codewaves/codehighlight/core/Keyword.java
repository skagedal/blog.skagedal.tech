package com.codewaves.codehighlight.core;

/**
 * Created by Sergej Kravcenko on 5/14/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public class Keyword {
   protected String[] valueArray;
   protected String className;
   protected int relevance;

   public Keyword(String className, String value) {
      this.valueArray = value.split(" ");
      this.className = className;
   }

   public Keyword(String className, String[] valueArray) {
       this.valueArray = valueArray;
       this.className = className;
    }

   public Keyword(String value, String className, int relevance) {
       this.valueArray = value.split(" ");
      this.className = className;
      this.relevance = relevance;
   }
}
