package com.microsoft.graph.exceptions;

public class Error {

    public String Code;
    public String Message;
    public String Target;

    public Error()
    {}


    public Error(String message)
    {
        this.Message = message;
    }

    public String getMessage(){return this.Message;}
    public String getCode(){return this.Code;}
    public String getTarget(){return this.Target;}

    public void setMessage(String message){ this.Message = message; }
    public void setCode(String code) { this.Code = code; }
    public void setTarget(String target) { this.Target = target; }

    @Override
    public String toString() {
        return "";
    }


}


