package com.ticket_pipeline.simple_utils;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPPBEEncryptedData;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBEDataDecryptorFactoryBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePBEKeyEncryptionMethodGenerator;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.util.io.Streams;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;

/**
 * Simple routine to encrypt and decrypt using a passphrase.
 * This service routine provides the basic PGP services between
 * byte arrays.
 * <p>
 * Note: this code plays no attention to -CONSOLE in the file name
 * the specification of "_CONSOLE" in the filename.
 * It also expects that a single pass phrase will have been used.
 */
public class CryptUtil {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private CryptUtil() {
    }

    public static String encode(String data) {
        char[] passArray = "{{Client.crypt_key}}".toCharArray();
        byte[] original = null;

        if (StringUtils.isEmpty(data)) {
            original = "".getBytes();
        } else {
            original = data.getBytes();
        }
        try {
            return new String(org.bouncycastle.util.encoders.Hex.encode(
                    encrypt(original, passArray, "iway", SymmetricKeyAlgorithmTags.TRIPLE_DES, false)));
        } catch (Exception e) {
            return data;
        }
    }

    public static String decode(String encryptedHex) {
        char[] passArray = "{{Client.crypt_key}}".toCharArray();
        try {
            return new String(decrypt(org.bouncycastle.util.encoders.Hex.decode(encryptedHex), passArray), StandardCharsets.UTF_8);
        } catch (Exception e) {
            return encryptedHex;
        }
    }


    /**
     * decrypt the passed in message stream
     *
     * @param encrypted  The message to be decrypted.
     * @param passPhrase Pass phrase (key)
     * @return Clear text as a byte array. I18N considerations are not handled by this routine
     */
    public static byte[] decrypt(byte[] encrypted, char[] passPhrase) throws IOException, PGPException {
        JcaPGPObjectFactory pgpF = new JcaPGPObjectFactory(PGPUtil.getDecoderStream(new ByteArrayInputStream(encrypted)));
        Object o = pgpF.nextObject();

        PGPEncryptedDataList enc;
        if (o instanceof PGPEncryptedDataList) {
            enc = (PGPEncryptedDataList) o;
        } else {
            enc = (PGPEncryptedDataList) pgpF.nextObject();
        }

        PGPPBEEncryptedData pbe = (PGPPBEEncryptedData) enc.get(0);
        InputStream clear = pbe.getDataStream(
                new JcePBEDataDecryptorFactoryBuilder
                        (new JcaPGPDigestCalculatorProviderBuilder()
                                .setProvider("BC")
                                .build())
                        .setProvider("BC")
                        .build(passPhrase));
        JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(clear);
        PGPCompressedData cData = (PGPCompressedData) pgpFact.nextObject();
        pgpFact = new JcaPGPObjectFactory(cData.getDataStream());
        PGPLiteralData ld = (PGPLiteralData) pgpFact.nextObject();
        return Streams.readAll(ld.getInputStream());
    }

    /**
     * Simple PGP encryptor between byte[].
     *
     * @param clearData  The test to be encrypted
     * @param passPhrase The pass phrase (key).  This method assumes that the
     *                   key is a simple pass phrase, and does not yet support
     *                   RSA or more sophisiticated keying.
     * @param fileName   File name. This is used in the Literal Data Packet (tag 11)
     *                   which is really inly important if the data is to be
     *                   related to a file to be recovered later.  Because this
     *                   routine does not know the source of the information, the
     *                   caller can set something here for file name use that
     *                   will be carried.  If this routine is being used to
     *                   encrypt SOAP MIME bodies, for example, use the file name from the
     *                   MIME type, if applicable. Or anything else appropriate.
     * @return encrypted data.
     */
    public static byte[] encrypt(byte[] clearData, char[] passPhrase, String fileName, int algorithm, boolean armor)
            throws IOException, PGPException {
        if (fileName == null) {
            fileName = PGPLiteralData.CONSOLE;
        }

        byte[] compressedData = compress(clearData, fileName);

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(new JcePGPDataEncryptorBuilder(algorithm).setSecureRandom(new SecureRandom()).setProvider("BC"));
        encGen.addMethod(new JcePBEKeyEncryptionMethodGenerator(passPhrase).setProvider("BC"));

        OutputStream out = getArmoredOutputStream(bOut, armor);
        try (OutputStream encOut = encGen.open(out, compressedData.length)) {
            encOut.write(compressedData);
        }
        closeArmoredOutputStream(out, armor);

        return bOut.toByteArray();
    }

    private static OutputStream getArmoredOutputStream(ByteArrayOutputStream bOut, boolean armor) {
        if (armor) {
            return new ArmoredOutputStream(bOut);
        } else {
            return bOut;
        }
    }

    private static void closeArmoredOutputStream(OutputStream out, boolean armor) throws IOException {
        if (armor) {
            out.close();
        }
    }

    private static byte[] compress(byte[] clearData, String fileName) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP);
        try {
            // we want to generate compressed data. This might be a user option later,
            // in which case we would pass in bOut.
            try (OutputStream pOut = new PGPLiteralDataGenerator().open(
                    comData.open(bOut), // the compressed output stream,  open it with the final destination
                    PGPLiteralData.BINARY,
                    fileName,  // "filename" to store
                    clearData.length, // length of clear data
                    new Date()  // current time
            )) {
                pOut.write(clearData);
            }
        } finally {
            comData.close();
        }

        return bOut.toByteArray();
    }
}
