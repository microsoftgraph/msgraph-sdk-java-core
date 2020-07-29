package com.microsoft.graph.exceptions;

public class Error {

    public String Code;
    public String Message;

    public Error(String code, String message) {
        this.Code = code;
        this.Message = message;
    }
    public Error(String message)
    {
        this.Message = message;
    }


    public String getMessage(){return this.Message;}
    public String getCode(){return this.Code;}

    public void setMessage(String message){ this.Message = message; }
    public void setCode(String code) { this.Code = code; }

    @Override
    public String toString() {
        return "";
    }


}


