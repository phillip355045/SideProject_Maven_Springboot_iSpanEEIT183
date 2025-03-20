package com.example.demo.embedded;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class PostImageBelongsRecordId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	private String belongsPostId;
	private String belongsImageId;
	
	@Override
	public int hashCode() {
		return Objects.hash(belongsImageId, belongsPostId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PostImageBelongsRecordId other = (PostImageBelongsRecordId) obj;
		return Objects.equals(belongsImageId, other.belongsImageId)
				&& Objects.equals(belongsPostId, other.belongsPostId);
	}

	
	
}
