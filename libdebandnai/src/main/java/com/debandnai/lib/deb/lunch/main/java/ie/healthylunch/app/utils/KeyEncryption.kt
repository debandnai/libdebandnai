package ie.healthylunch.app.utils

import android.util.Base64
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.lang.Exception
import java.nio.charset.StandardCharsets
import java.security.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class KeyEncryption {
    /**
     * @param value data to encrypt
     * @param key a secret key used for encryption
     * @return String result of encryption
     * @throws UnsupportedEncodingException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     */
    @Throws(
        UnsupportedEncodingException::class,
        InvalidKeyException::class,
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun encrypt(value: String, key: String): String {
        val value_bytes = value.toByteArray(charset("UTF-8"))
        val key_bytes = getKeyBytes(key)
        return Base64.encodeToString(encrypt(value_bytes, key_bytes, key_bytes), 0)
        //        return Base64.encode(value_bytes,0);
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun encrypt(
        paramArrayOfByte1: ByteArray?,
        paramArrayOfByte2: ByteArray?,
        paramArrayOfByte3: ByteArray?
    ): ByteArray {
        // setup AES cipher in CBC mode with PKCS #5 padding
        val localCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        // encrypt
//        localCipher.init(1, new SecretKeySpec(paramArrayOfByte2, "AES"), new IvParameterSpec(paramArrayOfByte3));
        localCipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(paramArrayOfByte2, "AES"))
        return localCipher.doFinal(paramArrayOfByte1)
    }

    /**
     *
     * @param value data to decrypt
     * @param key a secret key used for encryption
     * @return String result after decryption
     * @throws KeyException
     * @throws GeneralSecurityException
     * @throws GeneralSecurityException
     * @throws InvalidAlgorithmParameterException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws IOException
     */
    @Throws(GeneralSecurityException::class, IOException::class)
    fun decrypt(value: String?, key: String): String {
        val value_bytes = Base64.decode(value, 0)
        val key_bytes = getKeyBytes(key)
        return String(decrypt(value_bytes, key_bytes, key_bytes), StandardCharsets.UTF_8)
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidKeyException::class,
        InvalidAlgorithmParameterException::class,
        IllegalBlockSizeException::class,
        BadPaddingException::class
    )
    fun decrypt(
        ArrayOfByte1: ByteArray?,
        ArrayOfByte2: ByteArray?,
        ArrayOfByte3: ByteArray?
    ): ByteArray {
        // setup AES cipher in CBC mode with PKCS #5 padding
        val localCipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

        // decrypt
        localCipher.init(2, SecretKeySpec(ArrayOfByte2, "AES"), IvParameterSpec(ArrayOfByte3))
        return localCipher.doFinal(ArrayOfByte1)
    }

    @Throws(UnsupportedEncodingException::class)
    private fun getKeyBytes(paramString: String): ByteArray {
        val arrayOfByte1 = ByteArray(16)
        val arrayOfByte2 = paramString.toByteArray(charset("UTF-8"))
        System.arraycopy(
            arrayOfByte2,
            0,
            arrayOfByte1,
            0,
            arrayOfByte2.size.coerceAtMost(arrayOfByte1.size)
        )
        return arrayOfByte1
    }

    companion object {
        ////////////////////////////////
        private val ivBytes = byteArrayOf(
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00
        )
        private const val key = "8e3b62775a48470e"
        private const val initVector = "e75333a521102cd6"
        fun encrypt(value: String): String? {
            try {
                val iv = IvParameterSpec(initVector.toByteArray(charset("UTF-8")))
                //            IvParameterSpec iv = new IvParameterSpec(ivBytes);
                val skeySpec = SecretKeySpec(key.toByteArray(charset("UTF-8")), "AES")
                val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING") //PKCS5PADDING
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
                val encrypted = cipher.doFinal(value.toByteArray())
                return Base64.encodeToString(encrypted, 0)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return null
        }

        fun decrypt(value: String): String? {
            try {

                val skeySpec = SecretKeySpec(
                    key.toByteArray(charset("UTF-8")), "AES"
                )
                //
                val cipher =
                    Cipher.getInstance("AES/CBC/PKCS5PADDING") //PKCS5PADDING

                val passPhraseBytes = value.toByteArray()
                val encryptionBytes =
                    org.apache.commons.codec.binary.Base64.decodeBase64(passPhraseBytes)
                cipher.init(
                    Cipher.DECRYPT_MODE,
                    skeySpec,
                    IvParameterSpec(initVector.toByteArray())
                )
                val recoveredBytes = cipher.doFinal(encryptionBytes)
                return String(recoveredBytes)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return null
        }

        @Throws(Exception::class)
        fun decrypt(
            cipherText: ByteArray?,
            key: SecretKey,
            password: String,
            IV: ByteArray?
        ): String {
            //Get Cipher Instance
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

            //Create SecretKeySpec
            val keySpec = SecretKeySpec(key.encoded, "AES")
            val passKey = SecretKeySpec(password.toByteArray(charset("UTF-8")), "AES")

            //Create IvParameterSpec
            val ivSpec = IvParameterSpec(IV)

            //Initialize Cipher for DECRYPT_MODE
            cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)

            //Perform Decryption
            val decryptedText = cipher.doFinal(cipherText)
            return String(decryptedText)
        }
    }
}