package fr.nocturlab.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.beans.factory.annotation.Autowired;

import fr.nocturlab.manager.AccountManager;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Account {

	@Autowired
	AccountManager accountManager;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(insertable = false, updatable = false)
	private Integer id;
	private String pseudo;
	private String email;
	private Float difficulty;
	@Column(insertable = false, updatable = false)
	@JsonIgnore
	private UUID token;
	@JsonIgnore
	private byte[] pass;
	@NotNull private boolean isAdmin;
	@Column(name = "creation_date", insertable = false, updatable = false)
	private LocalDateTime creationDate;

	public Account(){
		this.difficulty = 1f;
		this.token = UUID.randomUUID();
		this.pass = accountManager.encryptPassword(accountManager.generatePassword());
		this.creationDate = LocalDateTime.now();
	}
	public Account(String pseudo) {
		this();
		this.pseudo = pseudo;
	}
	public Account(String pseudo, String email) {
		this(pseudo);
		this.email = email;
	}
	public Account(String pseudo, String email, byte[] pass) {
		this(pseudo, email);
		this.pass = pass;
	}
	public Account(String pseudo, String email, byte[] pass, boolean isAdmin) {
		this(pseudo, email, pass);
		this.isAdmin = isAdmin;
	}
	@Transient
	public void incDifficulty(Float questionDifficulty){
		this.difficulty+=(questionDifficulty)*0.1f;
	}
	@Transient
	public void decDifficulty(Float questionDifficulty){
		this.difficulty-=(questionDifficulty)*0.1f;
	}
}