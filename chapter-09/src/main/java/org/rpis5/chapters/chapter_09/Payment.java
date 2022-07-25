package org.rpis5.chapters.chapter_09;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
	@Id String id;
	String user;

	public Payment withUser(String user) {
		return this.user.equals(user) ? this : new Payment(this.id, user);
	}
}
