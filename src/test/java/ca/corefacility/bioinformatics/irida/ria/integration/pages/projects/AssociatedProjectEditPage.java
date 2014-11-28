package ca.corefacility.bioinformatics.irida.ria.integration.pages.projects;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.corefacility.bioinformatics.irida.ria.integration.pages.AbstractPage;
import ca.corefacility.bioinformatics.irida.ria.integration.utilities.Ajax;

public class AssociatedProjectEditPage extends AbstractPage {
	private static final Logger logger = LoggerFactory.getLogger(AssociatedProjectEditPage.class);

	private static final String RELATIVE_URL = "/projects/1/associated/edit";

	public AssociatedProjectEditPage(WebDriver driver) {
		super(driver);
		get(driver, RELATIVE_URL);
	}

	public List<String> getProjects() {
		logger.debug("Getting associated projects");
		List<WebElement> rows = driver.findElements(By.className("associated-project-row"));
		logger.debug("Got " + rows.size() + " projects");
		List<String> names = new ArrayList<>();

		for (WebElement ele : rows) {
			WebElement findElement = ele.findElement(By.className("project-id"));
			names.add(findElement.getText());
		}
		return names;
	}

	public List<String> getAssociatedProjects() {
		logger.debug("Getting associated projects");
		List<WebElement> rows = driver.findElements(By.className("associated-project-row"));

		List<String> names = new ArrayList<>();
		// get only the rows that have a btn-success
		for (WebElement ele : rows) {
			if (ele.findElements(By.className("btn-success")).size() > 0) {
				WebElement findElement = ele.findElement(By.className("project-id"));
				names.add(findElement.getText());
			}

		}

		return names;
	}

	public void clickAssociatedButton(Long projectId) {
		List<WebElement> rows = driver.findElements(By.className("associated-project-row"));
		WebElement foundRow = null;
		for (WebElement ele : rows) {
			if (ele.findElement(By.className("project-id")).getText().equals(projectId.toString())) {
				foundRow = ele;
			}
		}

		if (foundRow == null) {
			throw new IllegalArgumentException("No row with given project ID");
		}

		foundRow.findElement(By.cssSelector("button")).click();
		waitForAjax();
	}

	public boolean checkNotyStatus(String status) {
		WebElement noty = (new WebDriverWait(driver, 10)).until(ExpectedConditions.elementToBeClickable(By
				.className("noty_type_" + status)));
		return noty.isDisplayed();
	}

	// ************************************************************************************************
	// UTILITY METHODS
	// ************************************************************************************************

	private void waitForAjax() {
		Wait<WebDriver> wait = new WebDriverWait(driver, 60);
		wait.until(Ajax.waitForAjax(60000));
	}

}