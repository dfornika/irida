package ca.corefacility.bioinformatics.irida.ria.integration.utilities;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Helper methods for finding items within the UI.
 *
 * @author Josh Adam <josh.adam@phac-aspc.gc.ca>
 */
public class PageUtilities {
	private WebDriver driver;

	public PageUtilities(WebDriver driver) {
		this.driver = driver;
	}

	/**
	 * Wait for an {@link org.openqa.selenium.WebElement} to be present on the screen. 10 seconds.
	 *
	 * @param locator {@link org.openqa.selenium.By}
	 */
	public void waitForElementPresent(By locator) {
		(new WebDriverWait(this.driver, 10)).until(ExpectedConditions.presenceOfElementLocated(locator));
	}

	/**
	 * Wait for an {@link org.openqa.selenium.WebElement} to be visible on the screen. 10 seconds.
	 *
	 * @param locator {@link org.openqa.selenium.By}
	 */
	public void waitForElementVisible(By locator) {
		(new WebDriverWait(this.driver, 10)).until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	/**
	 * Wait for an {@link org.openqa.selenium.WebElement} to be invisible on the screen. 10 seconds.
	 *
	 * @param locator {@link org.openqa.selenium.By}
	 */
	public void waitForElementToBeAbsent(By locator) {
		(new WebDriverWait(this.driver, 10)).until(ExpectedConditions.invisibilityOfElementLocated(locator));
	}
}
