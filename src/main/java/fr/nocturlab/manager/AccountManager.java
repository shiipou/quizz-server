package fr.nocturlab.manager;

import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import fr.nocturlab.entities.Account;
import fr.nocturlab.entities.Resultat;
import fr.nocturlab.repository.AccountRepository;
import fr.nocturlab.repository.ResultatRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AccountManager {
	private static String ENCODE = "HmacSHA256";

	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private ResultatRepository resultatRepository;

	public Account login(String pseudo, String pass) {
		if (StringUtils.isEmpty(pseudo) || StringUtils.isEmpty(pass)) {
			log.info("Tentative de connexion sans pseudo ou sans mot de passe");
			return null;
		}
		Account a = accountRepository.getByPseudoAndPass(pseudo.toLowerCase().trim(), encryptPassword(pass));
		if (a == null) {
			log.info("Connexion avec mot de passe ou Login incorrect.");
			return null;
		}
		return a;
	}

	public List<Resultat> getAnswers(Account account) {
		return resultatRepository.findByAccount(account);
	}

	public static byte[] encryptPassword(String value) {
		byte[] ret;
		try {
			// génération de la clé secrète pour HMAC
			SecretKeySpec sk = new SecretKeySpec(new byte[]{3, 54 ,86, 51 ,65, 8, 65, 9, 1, 65, 87, 6, 13 ,9, 98, 24}, "RawBytes");

			// initailisation avec la clé secrète
			Mac mac = Mac.getInstance(ENCODE);
			mac.init(sk);
			ret = mac.doFinal(value.getBytes());
		}
		catch (Exception e) {
			log.error("Erreur d'encodage de mot de passe", e);
			return null;
		}
		return ret;
	}

	public String[] parseAuth(String auth) {
		if(auth == null)
			return new String[0];
		return auth.split(":");
	}

	public String generatePassword() {
		String allowedChars = "abcdefghijklmnopqrstuvwxyz";
		String[] biblio = new String[] {
			allowedChars,
			allowedChars.toUpperCase(),
			"1234567890"
		};
		String pwd = "";
		for (int i = 0;i<8;++i) {
			int id = (int)(Math.random()*(double)biblio.length);
			if (id == biblio.length)
				id--;
			pwd += biblio[id].charAt((int)(Math.random()*(double)biblio[id].length()));
		}

		return pwd;
	}
}