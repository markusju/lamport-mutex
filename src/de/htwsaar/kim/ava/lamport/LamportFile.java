package de.htwsaar.kim.ava.lamport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by markus on 29.01.17.
 */
public class LamportFile {

    private File file;
    private RandomAccessFile randomAccessFile;

    public LamportFile(String fileName) throws IOException {
        file = new File(fileName);

        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        randomAccessFile = new RandomAccessFile(file, "rw");
        initFile();
    }


    private void initFile() throws IOException {
        randomAccessFile.seek(0);
        randomAccessFile.writeBytes("000001000\n");
    }


    public void attachId(int id) throws IOException {
        randomAccessFile.seek(randomAccessFile.length());
        randomAccessFile.writeBytes(String.valueOf(id)+"\n");
    }

    public void incrementValue() throws IOException {
        writeNumber(readNumber()+1);
    }


    private void writeNumber(int number) throws IOException {
        if ((int)(Math.log10(number)+1) > 9)
            throw new IllegalArgumentException();
        String numberString = String.format("%09d", number);
        byte[] buffer = numberString.getBytes();
        randomAccessFile.seek(0);
        randomAccessFile.write(buffer, 0, 9);

    }

    private int readNumber() throws IOException {
        byte[] buffer = new byte[9];
        randomAccessFile.seek(0);
        randomAccessFile.read(buffer, 0, 9);
        int num = Integer.valueOf(new String(buffer));
        return num;
    }

    public void decrementValue() throws IOException {
        writeNumber(readNumber()-1);
    }



}
