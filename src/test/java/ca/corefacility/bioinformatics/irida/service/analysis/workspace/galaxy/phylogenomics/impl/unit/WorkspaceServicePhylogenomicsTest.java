package ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.phylogenomics.impl.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.corefacility.bioinformatics.irida.exceptions.ExecutionManagerException;
import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.WorkflowPreprationException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyDatasetException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.joins.Join;
import ca.corefacility.bioinformatics.irida.model.project.ReferenceFile;
import ca.corefacility.bioinformatics.irida.model.sample.Sample;
import ca.corefacility.bioinformatics.irida.model.sample.SampleSequenceFileJoin;
import ca.corefacility.bioinformatics.irida.model.workflow.InputFileType;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.PreparedWorkflowGalaxy;
import ca.corefacility.bioinformatics.irida.model.workflow.galaxy.phylogenomics.RemoteWorkflowPhylogenomics;
import ca.corefacility.bioinformatics.irida.model.workflow.submission.galaxy.phylogenomics.AnalysisSubmissionPhylogenomics;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyHistoriesService;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyWorkflowService;
import ca.corefacility.bioinformatics.irida.repositories.joins.sample.SampleSequenceFileJoinRepository;
import ca.corefacility.bioinformatics.irida.service.analysis.workspace.galaxy.phylogenomics.impl.WorkspaceServicePhylogenomics;

import com.github.jmchilton.blend4j.galaxy.beans.Dataset;
import com.github.jmchilton.blend4j.galaxy.beans.History;
import com.github.jmchilton.blend4j.galaxy.beans.WorkflowDetails;
import com.github.jmchilton.blend4j.galaxy.beans.collection.request.CollectionDescription;
import com.github.jmchilton.blend4j.galaxy.beans.collection.response.CollectionResponse;

/**
 * Tests out preparing a Galaxy Phylogenomics Pipeline workflow for execution.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class WorkspaceServicePhylogenomicsTest {

	@Mock private GalaxyHistoriesService galaxyHistoriesService;
	@Mock private GalaxyWorkflowService galaxyWorkflowService;
	@Mock private SampleSequenceFileJoinRepository sampleSequenceFileJoinRepository;
	@Mock private List<Dataset> sequenceDatasets;
	@Mock private Dataset refDataset;
	
	private WorkspaceServicePhylogenomics
		workflowPreparation;
	
	private Set<SequenceFile> inputFiles;
	private ReferenceFile referenceFile;
	private Path refFile;
	private RemoteWorkflowPhylogenomics remoteWorkflow;
	private AnalysisSubmissionPhylogenomics submission;
	private CollectionResponse collectionResponse;
	private WorkflowDetails workflowDetails;
		
	private History workflowHistory;
	
	private static final String HISTORY_ID = "10";
	private static final String WORKFLOW_ID = "11";
	private static final String SEQUENCE_FILE_LABEL = "sequence_files";
	private static final String REFERENCE_FILE_LABEL = "reference_files";
	private static final String SEQUENCE_FILE_ID = "12";
	private static final String REFERENCE_FILE_ID = "13";
	
	private static final String TREE_LABEL = "tree";
	private static final String MATRIX_LABEL = "snp_matrix";
	private static final String TABLE_LABEL = "snp_table";
	
	private SequenceFile sFileA;
	private SequenceFile sFileB;
	private SequenceFile sFileC;
	
	private Dataset datasetA;
	private Dataset datasetB;
	private Dataset datasetC;
	
	private Join<Sample, SequenceFile> sampleAJoin;
	private Join<Sample, SequenceFile> sampleBJoin;
	private Join<Sample, SequenceFile> sampleCJoin;
	
	private Join<Sample, SequenceFile> sampleAJoinWithB;
	
	/**
	 * Sets up variables for testing.
	 * @throws IOException
	 * @throws GalaxyDatasetException 
	 * @throws UploadException 
	 */
	@Before
	public void setup() throws IOException, UploadException, GalaxyDatasetException {
		MockitoAnnotations.initMocks(this);

		sFileA = new SequenceFile(createTempFile("fileA" , "fastq"));
		sFileB = new SequenceFile(createTempFile("fileB" , "fastq"));
		sFileC = new SequenceFile(createTempFile("fileC" , "fastq"));
		
		Sample sampleA = new Sample();
		sampleA.setSampleName("SampleA");
		
		Sample sampleB = new Sample();
		sampleB.setSampleName("SampleB");
		
		Sample sampleC = new Sample();
		sampleC.setSampleName("SampleC");
		
		sampleAJoin = new SampleSequenceFileJoin(sampleA, sFileA);
		sampleBJoin = new SampleSequenceFileJoin(sampleB, sFileB);
		sampleCJoin = new SampleSequenceFileJoin(sampleC, sFileC);
		
		sampleAJoinWithB = new SampleSequenceFileJoin(sampleA, sFileB);
		
		datasetA = new Dataset();
		datasetA.setId("1");
		datasetB = new Dataset();
		datasetB.setId("2");
		datasetC = new Dataset();
		datasetC.setId("3");
		
		refFile = createTempFile("reference", "fasta");
		referenceFile = new ReferenceFile(refFile);
		
		inputFiles = new HashSet<>();
		inputFiles.addAll(Arrays.asList(sFileA, sFileB, sFileC));
		
		String workflowChecksum = "1";
		
		remoteWorkflow = new RemoteWorkflowPhylogenomics(WORKFLOW_ID,
				workflowChecksum, SEQUENCE_FILE_LABEL, REFERENCE_FILE_LABEL,
				TREE_LABEL, MATRIX_LABEL, TABLE_LABEL);
		
		submission = new AnalysisSubmissionPhylogenomics(
			inputFiles, referenceFile, remoteWorkflow);
		
		workflowHistory = new History();
		workflowHistory.setId(HISTORY_ID);
		
		collectionResponse = new CollectionResponse();
		collectionResponse.setId("1");
		
		workflowDetails = new WorkflowDetails();
		workflowDetails.setId(WORKFLOW_ID);
		
		workflowPreparation = 
				new WorkspaceServicePhylogenomics(
						galaxyHistoriesService, galaxyWorkflowService, sampleSequenceFileJoinRepository);
	}
	
	private Path createTempFile(String prefix, String suffix) throws IOException {
		File file = File.createTempFile(prefix, suffix);
		file.deleteOnExit();
		
		return file.toPath();
	}
	
	/**
	 * Tests out successfully to preparing an analysis
	 * @throws ExecutionManagerException
	 */
	@Test
	public void testPrepareAnalysisWorkspaceSuccess() throws ExecutionManagerException {
		when(galaxyHistoriesService.newHistoryForWorkflow()).thenReturn(workflowHistory);
		
		when(galaxyHistoriesService.fileToHistory(sFileA.getFile(), InputFileType.FASTQ_SANGER,
				workflowHistory)).thenReturn(datasetA);
		when(galaxyHistoriesService.fileToHistory(sFileB.getFile(), InputFileType.FASTQ_SANGER,
				workflowHistory)).thenReturn(datasetB);
		when(galaxyHistoriesService.fileToHistory(sFileC.getFile(), InputFileType.FASTQ_SANGER,
				workflowHistory)).thenReturn(datasetC);
		
		when(sampleSequenceFileJoinRepository.getSampleForSequenceFile(sFileA))
				.thenReturn(sampleAJoin);
		when(sampleSequenceFileJoinRepository.getSampleForSequenceFile(sFileB))
				.thenReturn(sampleBJoin);
		when(sampleSequenceFileJoinRepository.getSampleForSequenceFile(sFileC))
				.thenReturn(sampleCJoin);
		
		when(galaxyHistoriesService.fileToHistory(
				refFile, InputFileType.FASTA, workflowHistory)).thenReturn(refDataset);
		when(galaxyHistoriesService.constructCollection(
				any(CollectionDescription.class), eq(workflowHistory))).thenReturn(collectionResponse);
		when(galaxyWorkflowService.getWorkflowDetails(WORKFLOW_ID)).thenReturn(workflowDetails);
		when(galaxyWorkflowService.getWorkflowInputId(
				workflowDetails, SEQUENCE_FILE_LABEL)).thenReturn(SEQUENCE_FILE_ID);
		when(galaxyWorkflowService.getWorkflowInputId(
				workflowDetails, REFERENCE_FILE_LABEL)).thenReturn(REFERENCE_FILE_ID);
		
		PreparedWorkflowGalaxy preparedWorkflow = workflowPreparation.prepareAnalysisWorkspace(submission);
		assertEquals("preparedWorflow history id not equal to " + HISTORY_ID,
				HISTORY_ID, preparedWorkflow.getRemoteAnalysisId());
		assertNotNull("workflowInputs in preparedWorkflow is null", preparedWorkflow.getWorkflowInputs());
	}
	
	/**
	 * Tests out failing to prepare an analysis.
	 * @throws ExecutionManagerException
	 */
	@Test(expected=UploadException.class)
	public void testPrepareAnalysisWorkspaceFail() throws ExecutionManagerException {
		when(galaxyHistoriesService.newHistoryForWorkflow()).thenReturn(workflowHistory);
		
		when(galaxyHistoriesService.fileToHistory(sFileA.getFile(), InputFileType.FASTQ_SANGER,
				workflowHistory)).thenThrow(new UploadException());
		when(galaxyHistoriesService.fileToHistory(sFileB.getFile(), InputFileType.FASTQ_SANGER,
				workflowHistory)).thenReturn(datasetB);
		when(galaxyHistoriesService.fileToHistory(sFileC.getFile(), InputFileType.FASTQ_SANGER,
				workflowHistory)).thenReturn(datasetC);

		when(sampleSequenceFileJoinRepository.getSampleForSequenceFile(sFileA))
				.thenReturn(sampleAJoin);
		when(sampleSequenceFileJoinRepository.getSampleForSequenceFile(sFileB))
				.thenReturn(sampleBJoin);
		when(sampleSequenceFileJoinRepository.getSampleForSequenceFile(sFileC))
				.thenReturn(sampleCJoin);
		
		workflowPreparation.prepareAnalysisWorkspace(submission);
	}
	
	/**
	 * Tests out failing to prepare an analysis due to two different sequence files with the same sample.
	 * @throws ExecutionManagerException
	 */
	@Test(expected=WorkflowPreprationException.class)
	public void testPrepareAnalysisWorkspaceFailDuplicateSamples() throws ExecutionManagerException {
		when(galaxyHistoriesService.newHistoryForWorkflow()).thenReturn(workflowHistory);
		
		when(galaxyHistoriesService.fileToHistory(sFileA.getFile(), InputFileType.FASTQ_SANGER,
				workflowHistory)).thenReturn(datasetA);
		when(galaxyHistoriesService.fileToHistory(sFileB.getFile(), InputFileType.FASTQ_SANGER,
				workflowHistory)).thenReturn(datasetB);
		when(galaxyHistoriesService.fileToHistory(sFileC.getFile(), InputFileType.FASTQ_SANGER,
				workflowHistory)).thenReturn(datasetC);

		when(sampleSequenceFileJoinRepository.getSampleForSequenceFile(sFileA))
				.thenReturn(sampleAJoin);
		when(sampleSequenceFileJoinRepository.getSampleForSequenceFile(sFileB))
				.thenReturn(sampleAJoinWithB);
		when(sampleSequenceFileJoinRepository.getSampleForSequenceFile(sFileC))
				.thenReturn(sampleCJoin);
		
		workflowPreparation.prepareAnalysisWorkspace(submission);
	}
}