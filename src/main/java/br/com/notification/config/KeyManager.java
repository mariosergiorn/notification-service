package br.com.notification.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

import static br.com.notification.utils.Constantes.PATH_CHAVE_PRIVADA;
import static br.com.notification.utils.Constantes.PATH_CHAVE_PUBLICA;

@Component
@Slf4j
public class KeyManager {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public String decryptAndVerify(String signedAndEncryptedMessage) throws Exception {

        //Carrega as chaves no sistema
        loadKeys();

        // Decodificar mensagem
        byte[] signedAndEncryptedMessageBytes = Base64.getDecoder().decode(signedAndEncryptedMessage);

        // Separar assinatura e mensagem criptografada
        int signatureLength = 256; // Tamanho da assinatura (256 bytes para RSA com SHA-256)
        byte[] signedMessage = new byte[signatureLength];
        byte[] encryptedMessage = new byte[signedAndEncryptedMessageBytes.length - signatureLength];
        System.arraycopy(signedAndEncryptedMessageBytes, 0, signedMessage, 0, signatureLength);
        System.arraycopy(signedAndEncryptedMessageBytes, signatureLength, encryptedMessage, 0, encryptedMessage.length);

        // Verificar assinatura
        Signature verifySignature = Signature.getInstance("SHA256withRSA");
        verifySignature.initVerify(publicKey);
        verifySignature.update(encryptedMessage);
        boolean isValidSignature = verifySignature.verify(signedMessage);

        if (isValidSignature) {log.info("Signature Valid!!");}

        // Descriptografar mensagem
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedMessage = cipher.doFinal(encryptedMessage);

        return new String(decryptedMessage);
    }

    public void loadKeys() throws Exception {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PUBLICA))) {
            this.publicKey = (PublicKey) inputStream.readObject();
        }

        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PRIVADA))) {
            this.privateKey = (PrivateKey) inputStream.readObject();
        }
    }
}
