package ca.corefacility.bioinformatics.irida.ria.web.models.datatables;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.context.MessageSource;

import ca.corefacility.bioinformatics.irida.model.joins.impl.ProjectSampleJoin;
import ca.corefacility.bioinformatics.irida.model.project.Project;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.DataTablesExportable;
import ca.corefacility.bioinformatics.irida.ria.web.components.datatables.models.DataTablesResponseModel;

/**
 * DataTables response object for {@link ProjectSampleJoin}
 */
public class DTProjectSamples implements DataTablesResponseModel, DataTablesExportable {
	private final String dataPattern = "MMM dd, yyyy";
	private final DateFormat dateFormatter = new SimpleDateFormat(dataPattern);

	private Long id;
	private Long projectId;
	private String sampleName;
	private String organism;
	private String projectName;
	private Date createdDate;
	private Date modifiedDate;
	private List<String> qcEntries;
	private boolean owner;

	public DTProjectSamples(ProjectSampleJoin projectSampleJoin, List<String> qcEntries) {
		Project project = projectSampleJoin.getSubject();
		Sample sample = projectSampleJoin.getObject();

		this.id = sample.getId();
		this.sampleName = sample.getSampleName();
		this.organism = sample.getOrganism();
		this.projectName = project.getName();
		this.projectId = project.getId();
		this.createdDate = sample.getCreatedDate();
		this.modifiedDate = sample.getModifiedDate();
		this.qcEntries = qcEntries;
		this.owner = projectSampleJoin.isOwner();
	}

	public Long getId() {
		return this.id;
	}

	public String getSampleName() {
		return sampleName;
	}

	public String getOrganism() {
		return organism;
	}

	public Long getProjectId() {
		return projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public List<String> getQcEntries() {
		return qcEntries;
	}
	
	public boolean isOwner(){
		return owner;
	}

	@Override
	public List<String> toTableRow() {
		List<String> data = new ArrayList<>();
		data.add(String.valueOf(this.getId()));
		data.add(this.getSampleName());
		data.add(this.getOrganism());
		data.add(String.valueOf(this.getProjectId()));
		data.add(this.getProjectName());
		data.add(dateFormatter.format(this.getCreatedDate()));
		data.add(dateFormatter.format(this.getModifiedDate()));
		return data;
	}

	@Override
	public List<String> getTableHeaders(MessageSource messageSource, Locale locale) {
		List<String> headers = new ArrayList<>();
		headers.add(messageSource.getMessage("iridaThing.id", new Object[] {}, locale));
		headers.add(messageSource.getMessage("project.samples.table.name", new Object[] {}, locale));
		headers.add(messageSource.getMessage("project.samples.table.organism", new Object[] {}, locale));
		headers.add(messageSource.getMessage("project.samples.table.project-id", new Object[] {}, locale));
		headers.add(messageSource.getMessage("project.samples.table.project", new Object[] {}, locale));
		headers.add(messageSource.getMessage("project.samples.table.created", new Object[] {}, locale));
		headers.add(messageSource.getMessage("project.samples.table.modified", new Object[] {}, locale));
		return headers;
	}
}
