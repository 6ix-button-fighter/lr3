package lsb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LSB {

    public static byte[] readBmp(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    public static void writeMessageToBmp(byte[] message, byte[] bytes, String to) throws IOException {
        int startPos = bytes[10];

        int i;
        byte temp;
        int j = 0;
        for (i = Math.abs(startPos); i < bytes.length - 4; i += 4) {
            temp = message[j];
            hideByteIntoPixel(bytes, i, temp);

            if (++j == message.length)
                break;

        }
        temp = (byte) 255;
        ++i;
        hideByteIntoPixel(bytes, i, temp);

        Files.write(Path.of(to), bytes);
    }

    private static void hideByteIntoPixel(byte[] bytes, int i, byte temp) {
        bytes[i] &= (0xFC);
        bytes[i] |= (temp >> 6) & 0x03;

        bytes[i + 1] &= (0xFC);
        bytes[i + 1] |= (temp >> 4) & 0x03;

        bytes[i + 2] &= (0xFC);
        bytes[i + 2] |= (temp >> 2) & 0x03;

        bytes[i + 3] &= (0xFC);
        bytes[i + 3] |= (temp) & 0x03;
    }

    public static List<Byte> getMessageFromBmp(byte[] bytes) {

        List<Byte> outputMessage = new ArrayList<>();
        int startPos = bytes[10];

        for (int i = Math.abs(startPos); i < bytes.length - 4; i += 4) {
            byte b = (byte) (((bytes[i] & 0x03) << 6) | ((bytes[i + 1] & 0x03) << 4) | ((bytes[i + 2] & 0x03) << 2) | (bytes[i + 3] & 0x03));

            outputMessage.add(b);
            if ((b & 0xFF) == 255) {
                System.out.println(i);
                break;
            }

        }
        return outputMessage;
    }


    public static void main(String[] args) throws IOException {
        String pathTo;
        String pathFrom;

        var scanner = new Scanner(System.in);
        System.out.println("1. Encode message\n" +
                "2. Decode message\n" +
                "Your choice: ");

        switch (Integer.parseInt(scanner.nextLine())) {
            case 1 -> {
                String pathToMessage = "message.txt";
                pathFrom = "1.bmp";
                pathTo = "encrypted.bmp";
                byte[] message = Files.readAllBytes(Path.of(pathToMessage));
                writeMessageToBmp(message, readBmp(pathFrom), pathTo);
            }
            case 2 -> {
                pathFrom = "10.bmp";
                pathTo = "decodedMessage.txt";
                byte[] bytes = readBmp(pathFrom);
                List<Byte> outArray = getMessageFromBmp(bytes);
                Files.write(Path.of(pathTo), castToPrimitiveByteArray(outArray));
            }
        }

    }

    public static byte[] castToPrimitiveByteArray(List<Byte> list) {
        byte[] result = new byte[list.size()];

        for (int i = 0; i < list.size(); i++)
            result[i] = list.get(i);

        return result;
    }
}
