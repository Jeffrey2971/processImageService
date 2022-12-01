import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author jeffrey
 * @since JDK 1.8
 */


public class JavaSDK {
    private static final String PUBLIC_KEY = "your 32-bit length public key";
    private static final String PRIVATE_SECRET = "your 128-bit length private key";
    private static final File TARGET_IAMGE_PATH = new File("upload image file path");
    private static final String TARGET_URL = "http://localhost/single-upload";


    public static void main(String[] args) throws IOException {

        Map<String, String> requestParams = new HashMap<>();

        StringBuilder salt = new StringBuilder(8);

        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int num = random.nextInt(9);
            if (num == 0) {
                i--;
                continue;
            }
            salt.append(num);
        }

        DigestEngine digestEngine = DigestEngine.md5();

        String imageMd5 = digestEngine.digestString(TARGET_IAMGE_PATH).toLowerCase();

        String sign = digestEngine.digestString(PUBLIC_KEY + imageMd5 + salt + PRIVATE_SECRET).toLowerCase();

        HashMap<String, String> headers = new HashMap<>();

        requestParams.put("demo_image_md5", imageMd5);
        requestParams.put("demo_public_key", PUBLIC_KEY);
        requestParams.put("demo_salt", salt.toString());

        headers.put("Content-Type", "multipart/form-data; boundary=#");
        headers.put("x-jeffrey-api-sign", sign);

        ByteArrayOutputStream baos = null;
        byte[] body_data = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(TARGET_IAMGE_PATH.toPath()));
            baos = new ByteArrayOutputStream();
            int c;
            byte[] buffer = new byte[8 * 1024];

            while ((c = bis.read(buffer)) != -1) {
                baos.write(buffer, 0, c);
                baos.flush();
            }
            body_data = baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                baos.close();
            }
        }

        String result = HttpPostUtil.doPostSubmitBody(TARGET_URL, requestParams, headers, TARGET_IAMGE_PATH.toString(), body_data, "utf-8");

        System.out.println(result);
    }


}

abstract class DigestEngine {
    private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    static class JavaDigestEngine extends DigestEngine {
        private final MessageDigest messageDigest;

        JavaDigestEngine(final String algorithm) {
            try {
                this.messageDigest = MessageDigest.getInstance(algorithm);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public byte[] digest(final byte[] byteArray) {
            messageDigest.update(byteArray);
            return messageDigest.digest();
        }

        @Override
        public byte[] digest(final File file) throws IOException {
            FileInputStream fis = null;
            BufferedInputStream bis = null;
            DigestInputStream dis = null;

            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                dis = new DigestInputStream(bis, messageDigest);

                while (dis.read() != -1) {
                }
            } finally {
                close(dis);
                close(bis);
                close(fis);
            }
            return messageDigest.digest();
        }
    }

    /**
     * Creates new MD5 digest.
     */
    public static DigestEngine md5() {
        return new JavaDigestEngine("MD5");
    }

    /**
     * Returns byte-hash of input byte array.
     */
    public abstract byte[] digest(byte[] input);

    /**
     * Returns byte-hash of input string.
     */
    private byte[] digest(final String input) {
        return digest(getBytes(input));
    }

    /**
     * Returns digest of a file. Implementations may not read the whole
     * file into the memory.
     */
    public abstract byte[] digest(final File file) throws IOException;

    /**
     * Returns string hash of input string.
     */
    String digestString(final String input) {
        return toHexString(digest(input));
    }

    String digestString(final File file) throws IOException {
        return toHexString(digest(file));
    }

    /**
     * Returns String bytes using Jodds default encoding.
     */
    private static byte[] getBytes(final String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Converts bytes to hex string.
     */
    private static String toHexString(final byte[] bytes) {
        char[] chars = new char[bytes.length * 2];
        int i = 0;
        for (byte b : bytes) {
            chars[i++] = int2hex((b & 0xF0) >> 4);
            chars[i++] = int2hex(b & 0x0F);
        }
        return new String(chars);
    }

    /**
     * Converts integer digit to heck char.
     */
    private static char int2hex(final int i) {
        return HEX_CHARS[i];
    }

    /**
     * Closes silently the closable object. If it is {@link Flushable}, it
     * will be flushed first. No exception will be thrown if an I/O error occurs.
     */
    private static void close(final Closeable closeable) {
        if (closeable != null) {
            if (closeable instanceof Flushable) {
                try {
                    ((Flushable) closeable).flush();
                } catch (IOException ignored) {
                }
            }
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }
}

class HttpPostUtil {

    public static String doPostSubmitBody(String url, Map<String, String> requestParams, Map<String, String> headers,
                                          String filePath, byte[] body_data, String charset) {

        final String NEWLINE = "\r\n";
        final String PREFIX = "--";
        final String BOUNDARY = "#";
        HttpURLConnection httpConn;
        BufferedInputStream bis = null;
        DataOutputStream dos = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {

            URL urlObj = new URL(url);
            httpConn = (HttpURLConnection) urlObj.openConnection();
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            httpConn.setRequestMethod("POST");
            httpConn.setUseCaches(false);

            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpConn.setRequestProperty(header.getKey(), header.getValue());
            }


            httpConn.connect();


            dos = new DataOutputStream(httpConn.getOutputStream());

            if (requestParams != null && !requestParams.isEmpty()) {
                for (Map.Entry<String, String> entry : requestParams.entrySet()) {
                    String key = entry.getKey();
                    String value = requestParams.get(key);
                    dos.writeBytes(PREFIX + BOUNDARY + NEWLINE);
                    dos.writeBytes("Content-Disposition: form-data; "
                            + "name=\"" + key + "\"" + NEWLINE);
                    dos.writeBytes(NEWLINE);
                    dos.writeBytes(URLEncoder.encode(value, charset));
                    dos.writeBytes(NEWLINE);
                }
            }

            if (body_data != null && body_data.length > 0) {
                dos.writeBytes(PREFIX + BOUNDARY + NEWLINE);
                String fileName = filePath.substring(filePath
                        .lastIndexOf(File.separatorChar));
                dos.writeBytes("Content-Disposition: form-data; " + "name=\""
                        + "uploadFile" + "\"" + "; filename=\"" + fileName
                        + "\"" + NEWLINE);
                dos.writeBytes(NEWLINE);
                dos.write(body_data);
                dos.writeBytes(NEWLINE);
            }

            dos.writeBytes(PREFIX + BOUNDARY + PREFIX + NEWLINE);
            dos.flush();

            byte[] buffer = new byte[8 * 1024];
            int c;

            if (httpConn.getResponseCode() == 200) {
                bis = new BufferedInputStream(httpConn.getInputStream());
                while ((c = bis.read(buffer)) != -1) {
                    baos.write(buffer, 0, c);
                    baos.flush();
                }
            }

            return baos.toString(charset);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dos != null) {
                    dos.close();
                }
                if (bis != null) {
                    bis.close();
                }
                baos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}