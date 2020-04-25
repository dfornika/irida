package ca.corefacility.bioinformatics.irida.ria.web.projects.settings.dto;

/**
 * Used to send information to the server about a user to be added as a project member
 */
public class NewProjectMemberRequest {
	/*
	User Identifier
	 */
	private Long id;

	/*
	Role to add the user to the project as
	 */
	private String role;

	public NewProjectMemberRequest() {
	}

	public NewProjectMemberRequest(Long id, String role) {
		this.id = id;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
}
