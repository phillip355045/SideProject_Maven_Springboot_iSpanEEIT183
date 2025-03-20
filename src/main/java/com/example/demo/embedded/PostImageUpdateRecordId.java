package com.example.demo.embedded;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class PostImageUpdateRecordId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	
	private String updateRecordForImageId;
	private String imageUploadUpdateId;
	@Override
	public int hashCode() {
		return Objects.hash(imageUploadUpdateId, updateRecordForImageId);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PostImageUpdateRecordId other = (PostImageUpdateRecordId) obj;
		return Objects.equals(imageUploadUpdateId, other.imageUploadUpdateId)
				&& Objects.equals(updateRecordForImageId, other.updateRecordForImageId);
	}
	
	
	
	
	
}
