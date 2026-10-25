package util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

// Hash de senha com PBKDF2 (já vem no Java, não precisa baixar biblioteca).
// Formato guardado no banco:  iterações:salt:hash  (cerca de 75 caracteres).
// Cada senha ganha um "salt" aleatório: senhas iguais geram hashes diferentes.
public final class SenhaUtil {

    private static final int ITERACOES = 65_536;
    private static final int TAMANHO_SALT_BYTES = 16;
    private static final int TAMANHO_HASH_BITS = 256;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private SenhaUtil() {
    }

    public static String gerarHash(String senha) {
        byte[] salt = new byte[TAMANHO_SALT_BYTES];
        ALEATORIO.nextBytes(salt);

        byte[] hash = pbkdf2(senha.toCharArray(), salt, ITERACOES);

        return ITERACOES + ":"
                + Base64.getEncoder().encodeToString(salt) + ":"
                + Base64.getEncoder().encodeToString(hash);
    }

    // Para o login: confere a senha digitada contra o hash guardado no banco.
    public static boolean verificar(String senha, String hashArmazenado) {
        String[] partes = hashArmazenado.split(":");

        if (partes.length != 3) {
            return false;
        }

        int iteracoes = Integer.parseInt(partes[0]);
        byte[] salt = Base64.getDecoder().decode(partes[1]);
        byte[] esperado = Base64.getDecoder().decode(partes[2]);

        byte[] calculado = pbkdf2(senha.toCharArray(), salt, iteracoes);

        return MessageDigest.isEqual(esperado, calculado);
    }

    private static byte[] pbkdf2(char[] senha, byte[] salt, int iteracoes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha, salt, iteracoes, TAMANHO_HASH_BITS);

            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec)
                    .getEncoded();

        } catch (GeneralSecurityException erro) {
            throw new IllegalStateException("Erro ao gerar o hash da senha.", erro);
        }
    }
}
