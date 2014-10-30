package ca.corefacility.bioinformatics.irida.ria.integration.projects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import ca.corefacility.bioinformatics.irida.config.IridaApiPropertyPlaceholderConfig;
import ca.corefacility.bioinformatics.irida.config.data.IridaApiJdbcDataSourceConfig;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.BasePage;
import ca.corefacility.bioinformatics.irida.ria.integration.pages.projects.ProjectSamplesPage;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.google.common.collect.ImmutableList;

/**
 * <p> Integration test to ensure that the Project Details Page. </p>
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes = { IridaApiJdbcDataSourceConfig.class,
		IridaApiPropertyPlaceholderConfig.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class, DbUnitTestExecutionListener.class })
@ActiveProfiles("it")
@DatabaseSetup("/ca/corefacility/bioinformatics/irida/ria/web/projects/ProjectSamplesView.xml")
@DatabaseTearDown("classpath:/ca/corefacility/bioinformatics/irida/test/integration/TableReset.xml")
public class ProjectSamplesPageIT {
	private static final Logger logger = LoggerFactory.getLogger(ProjectSamplesPageIT.class);

	private WebDriver driver;
	private ProjectSamplesPage page;

	@Before
	public void setUp() {
		driver = BasePage.initializeDriver();
		this.page = new ProjectSamplesPage(driver);
	}

	@After
	public void destroy() {
		BasePage.destroyDriver(driver);
	}

	@Test
	public void testInitialPageSetUp() {
		logger.info("Testing page set up for: Project Samples");
		page.goTo();
		assertTrue(page.getTitle().contains("Samples"));
		assertEquals(10, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testPaging() {
		logger.info("Testing paging for: Project Samples");
		page.goTo();

		// Initial setup
		assertFalse(page.isFirstButtonEnabled());
		assertFalse(page.isPreviousButtonEnabled());
		assertTrue(page.isNextButtonEnabled());
		assertTrue(page.isLastButtonEnabled());
		assertEquals(1, page.getGetSelectedPageNumber());

		// Second Page
		page.selectPage(2);
		assertEquals(2, page.getGetSelectedPageNumber());
		assertTrue(page.isFirstButtonEnabled());
		assertTrue(page.isPreviousButtonEnabled());
		assertTrue(page.isNextButtonEnabled());
		assertTrue(page.isLastButtonEnabled());
		assertEquals(10, page.getNumberOfSamplesDisplayed());

		// Third Page (1 element)
		page.selectPage(3);
		assertTrue(page.isFirstButtonEnabled());
		assertTrue(page.isPreviousButtonEnabled());
		assertFalse(page.isNextButtonEnabled());
		assertFalse(page.isLastButtonEnabled());
		assertEquals(3, page.getGetSelectedPageNumber());
		assertEquals(1, page.getNumberOfSamplesDisplayed());

		// Previous Button
		page.clickPreviousPageButton();
		assertEquals(2, page.getGetSelectedPageNumber());
		page.clickPreviousPageButton();
		assertEquals(1, page.getGetSelectedPageNumber());

		// Next Button
		page.clickNextPageButton();
		assertEquals(2, page.getGetSelectedPageNumber());
		page.clickNextPageButton();
		assertEquals(3, page.getGetSelectedPageNumber());

		// First and List page buttons
		page.clickFirstPageButton();
		assertEquals(1, page.getGetSelectedPageNumber());
		assertFalse(page.isFirstButtonEnabled());
		page.clickLastPageButton();
		assertEquals(3, page.getGetSelectedPageNumber());
		assertFalse(page.isLastButtonEnabled());
		assertTrue(page.isFirstButtonEnabled());
		assertEquals(5, page.getNumberOfSamplesDisplayed());
	}

	@Test
	public void testSelectSamples() {
		logger.info("Testing selecting samples for: Project Samples");
		page.goTo();

		assertEquals(0, page.getNumberOfSamplesSelected());
		page.selectSampleByRow(0);
		page.selectSampleByRow(1);
		page.selectSampleByRow(2);
		assertEquals(3, page.getNumberOfSamplesSelected());
		page.selectSampleByRow(1);
		assertEquals(2, page.getNumberOfSamplesSelected());

		// If I go back to the page I expect them to be there
		page.goTo();
		assertEquals(2, page.getNumberOfSamplesSelected());
	}

	@Test
	public void testPagingWithSelectingSamples() {
		logger.info("Testing paging with selecting samples for: Project Samples");
		List<Integer> page1 = ImmutableList.of(0, 1, 6);
		page.goTo();

		assertEquals(0, page.getNumberOfSamplesSelected());
		page1.forEach(page::selectSampleByRow);
		assertEquals(3, page.getNumberOfSamplesSelected());
		assertTrue(page.isRowSelected(6));

		// Let's go to the second page
		page.clickNextPageButton();
		for (int row : page1) {
			assertFalse(page.isRowSelected(row));
		}
		assertEquals(0, page.getNumberOfSamplesSelected());
		page.selectSampleByRow(2);

		// Let's jump around a little
		jumpAroundLists();

		// Make sure samples are still selected on the first page
		page.clickFirstPageButton();
		for (int row : page1) {
			assertTrue(page.isRowSelected(row));
		}
		assertEquals(3, page.getNumberOfSamplesSelected());

		// Deselect first page samples
		page1.forEach(page::selectSampleByRow);
		assertEquals(0, page.getNumberOfSamplesSelected());

		jumpAroundLists();

		page.clickFirstPageButton();
		assertEquals(0, page.getNumberOfSamplesSelected());
	}

	private void jumpAroundLists() {
		page.clickFirstPageButton();
		page.clickLastPageButton();
		page.clickPreviousPageButton();
		page.clickPreviousPageButton();
		page.clickNextPageButton();
	}
}
