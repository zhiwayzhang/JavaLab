package com.qst.dms.entity;

import java.io.*;

public class AppendObjectOutputStream extends ObjectOutputStream {
    public static File file = null;
    public AppendObjectOutputStream(File file) throws IOException {
        super(new FileOutputStream(file, true));
        System.out.println("Initial Successful");
    }
    public void writeStreamHeader() throws IOException {
        if (file != null) {
            if (file.length() == 0) {
                super.writeStreamHeader();
            } else {
                this.reset();
            }
        } else {
            super.writeStreamHeader();
        }
    }
}
