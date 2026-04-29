package com.google.gson;

public class GsonBuilder {
    private boolean prettyPrinting;

    public GsonBuilder setPrettyPrinting() {
        this.prettyPrinting = true;
        return this;
    }

    public Gson create() {
        return new Gson(prettyPrinting);
    }
}
