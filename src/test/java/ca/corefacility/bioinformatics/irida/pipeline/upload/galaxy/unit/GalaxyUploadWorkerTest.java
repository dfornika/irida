package ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.unit;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import ca.corefacility.bioinformatics.irida.exceptions.UploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.ChangeLibraryPermissionsException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.CreateLibraryException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNoRoleException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.GalaxyUserNotFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.LibraryUploadException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoGalaxyContentFoundException;
import ca.corefacility.bioinformatics.irida.exceptions.galaxy.NoLibraryFoundException;
import ca.corefacility.bioinformatics.irida.model.upload.UploadSample;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyAccountEmail;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyProjectName;
import ca.corefacility.bioinformatics.irida.model.upload.galaxy.GalaxyUploadResult;

import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyAPI;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploadResultUtils.UploadExceptionRunnerTest;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploadResultUtils.UploadFinishedRunnerTest;
import ca.corefacility.bioinformatics.irida.pipeline.upload.galaxy.GalaxyUploadWorker;

/**
 * Unit tests for GalaxyUploadWorker.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class GalaxyUploadWorkerTest {
	private GalaxyAccountEmail userName;
	private GalaxyProjectName dataLocation;
	private List<UploadSample> samples;

	@Mock
	private GalaxyAPI galaxyAPI;
	
	@Mock
	private GalaxyUploadResult uploadResult;
	
	/**
	 * Setup objects for test.
	 * @throws MalformedURLException
	 * @throws NoGalaxyContentFoundException 
	 * @throws GalaxyUserNoRoleException 
	 * @throws NoLibraryFoundException 
	 * @throws GalaxyUserNotFoundException 
	 * @throws ChangeLibraryPermissionsException 
	 * @throws CreateLibraryException 
	 * @throws LibraryUploadException 
	 * @throws ConstraintViolationException 
	 */
	@Before
	public void setup() throws MalformedURLException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, GalaxyUserNotFoundException, NoLibraryFoundException, GalaxyUserNoRoleException, NoGalaxyContentFoundException {
		MockitoAnnotations.initMocks(this);

		userName = new GalaxyAccountEmail("admin@localhost");
		dataLocation = new GalaxyProjectName("Test");
		
		samples = new ArrayList<UploadSample>();
	}
	
	/**
	 * Tests general upload without overriding behaviours on finished upload or on exceptions.
	 * @throws InterruptedException
	 * @throws ConstraintViolationException
	 * @throws LibraryUploadException
	 * @throws CreateLibraryException
	 * @throws ChangeLibraryPermissionsException
	 * @throws GalaxyUserNotFoundException
	 * @throws NoLibraryFoundException
	 * @throws GalaxyUserNoRoleException
	 * @throws NoGalaxyContentFoundException
	 */
	@Test
	public void testUpload() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, GalaxyUserNotFoundException, NoLibraryFoundException, GalaxyUserNoRoleException, NoGalaxyContentFoundException {
		when(galaxyAPI.uploadSamples(samples, dataLocation, userName)).thenReturn(uploadResult);
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		worker.run();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
	}
	
	/**
	 * Tests successful upload and running of finished method.
	 * @throws InterruptedException
	 * @throws ConstraintViolationException
	 * @throws LibraryUploadException
	 * @throws CreateLibraryException
	 * @throws ChangeLibraryPermissionsException
	 * @throws GalaxyUserNotFoundException
	 * @throws NoLibraryFoundException
	 * @throws GalaxyUserNoRoleException
	 * @throws NoGalaxyContentFoundException
	 */
	@Test
	public void testUploadSuccess() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, GalaxyUserNotFoundException, NoLibraryFoundException, GalaxyUserNoRoleException, NoGalaxyContentFoundException {
		when(galaxyAPI.uploadSamples(samples, dataLocation, userName)).thenReturn(uploadResult);
		
		UploadFinishedRunnerTest finishedRunnerTest = new UploadFinishedRunnerTest();
		UploadExceptionRunnerTest exceptionRunnerTest = new UploadExceptionRunnerTest();
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		worker.runOnUploadFinished(finishedRunnerTest);
		worker.runOnUploadException(exceptionRunnerTest);
		
		worker.run();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
		assertEquals(uploadResult, finishedRunnerTest.getFinishedResult());
		assertEquals(uploadResult, worker.getUploadResult());
		assertFalse(worker.exceptionOccured());
		assertNull(worker.getUploadException());
		assertNull(exceptionRunnerTest.getException());
	}
	
	/**
	 * Tests successful upload and running of finished method in separate thread.
	 * @throws InterruptedException
	 * @throws ConstraintViolationException
	 * @throws LibraryUploadException
	 * @throws CreateLibraryException
	 * @throws ChangeLibraryPermissionsException
	 * @throws GalaxyUserNotFoundException
	 * @throws NoLibraryFoundException
	 * @throws GalaxyUserNoRoleException
	 * @throws NoGalaxyContentFoundException
	 */
	@Test
	public void testUploadSuccessSeparateThread() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, GalaxyUserNotFoundException, NoLibraryFoundException, GalaxyUserNoRoleException, NoGalaxyContentFoundException {
		when(galaxyAPI.uploadSamples(samples, dataLocation, userName)).thenReturn(uploadResult);
		
		UploadFinishedRunnerTest finishedRunnerTest = new UploadFinishedRunnerTest();
		UploadExceptionRunnerTest exceptionRunnerTest = new UploadExceptionRunnerTest();
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		worker.runOnUploadFinished(finishedRunnerTest);
		worker.runOnUploadException(exceptionRunnerTest);
		Thread t = new Thread(worker);
		t.run();
		t.join();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
		assertEquals(uploadResult, finishedRunnerTest.getFinishedResult());
		assertEquals(uploadResult, worker.getUploadResult());
		assertFalse(worker.exceptionOccured());
		assertNull(worker.getUploadException());
		assertNull(exceptionRunnerTest.getException());
	}
	
	/**
	 * Tests failed upload and running of exception methods.
	 * @throws InterruptedException
	 * @throws ConstraintViolationException
	 * @throws LibraryUploadException
	 * @throws CreateLibraryException
	 * @throws ChangeLibraryPermissionsException
	 * @throws GalaxyUserNotFoundException
	 * @throws NoLibraryFoundException
	 * @throws GalaxyUserNoRoleException
	 * @throws NoGalaxyContentFoundException
	 */
	@Test
	public void testUploadException() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, GalaxyUserNotFoundException, NoLibraryFoundException, GalaxyUserNoRoleException, NoGalaxyContentFoundException {
		UploadException uploadException = new LibraryUploadException("exception");
		
		when(galaxyAPI.uploadSamples(samples, dataLocation, userName)).thenThrow(uploadException);
		
		UploadFinishedRunnerTest finishedRunnerTest = new UploadFinishedRunnerTest();
		UploadExceptionRunnerTest exceptionRunnerTest = new UploadExceptionRunnerTest();
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		worker.runOnUploadFinished(finishedRunnerTest);
		worker.runOnUploadException(exceptionRunnerTest);
		
		worker.run();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
		assertEquals(uploadException, exceptionRunnerTest.getException());
		assertTrue(worker.exceptionOccured());
		assertEquals(uploadException, worker.getUploadException());
		assertNull(worker.getUploadResult());
		assertNull(finishedRunnerTest.getFinishedResult());
	}
	
	/**
	 * Tests failed upload and running of exception methods in separate thread.
	 * @throws InterruptedException
	 * @throws ConstraintViolationException
	 * @throws LibraryUploadException
	 * @throws CreateLibraryException
	 * @throws ChangeLibraryPermissionsException
	 * @throws GalaxyUserNotFoundException
	 * @throws NoLibraryFoundException
	 * @throws GalaxyUserNoRoleException
	 * @throws NoGalaxyContentFoundException
	 */
	@Test
	public void testUploadExceptionSeparateThread() throws InterruptedException, ConstraintViolationException, LibraryUploadException, CreateLibraryException, ChangeLibraryPermissionsException, GalaxyUserNotFoundException, NoLibraryFoundException, GalaxyUserNoRoleException, NoGalaxyContentFoundException {
		UploadException uploadException = new LibraryUploadException("exception");
		
		when(galaxyAPI.uploadSamples(samples, dataLocation, userName)).thenThrow(uploadException);
		
		UploadFinishedRunnerTest finishedRunnerTest = new UploadFinishedRunnerTest();
		UploadExceptionRunnerTest exceptionRunnerTest = new UploadExceptionRunnerTest();
		
		GalaxyUploadWorker worker = new GalaxyUploadWorker(galaxyAPI, samples, dataLocation, userName);
		worker.runOnUploadFinished(finishedRunnerTest);
		worker.runOnUploadException(exceptionRunnerTest);
		
		Thread t = new Thread(worker);
		t.start();
		t.join();
		
		verify(galaxyAPI).uploadSamples(samples, dataLocation, userName);
		assertEquals(uploadException, exceptionRunnerTest.getException());
		assertTrue(worker.exceptionOccured());
		assertEquals(uploadException, worker.getUploadException());
		assertNull(worker.getUploadResult());
		assertNull(finishedRunnerTest.getFinishedResult());
	}
}
