package com.eventra.member.dto;

import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {
	@Size(max = 50)
	private String fullName;
	@Size(max = 15)
	private String phoneNumber;
	@Size(max = 255)
	private String address;
	private byte[] profilePic;

	// getters/setters
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public byte[] getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(byte[] profilePic) {
		this.profilePic = profilePic;
	}
}
