const { Builder, By, Key, until } = require('selenium-webdriver');
const chrome = require('selenium-webdriver/chrome');

async function testMusicSearch(driver, track) {
    try {
        // Open a new tab
        await driver.get('http://localhost:3000');
        const searchInput = await driver.findElement(By.css('input[placeholder="Buscá una canción..."]'));
        await searchInput.sendKeys(track, Key.RETURN);

        await driver.wait(until.elementLocated(By.className('react-autosuggest__suggestions-list')), 5000);
        const firstSuggestion = await driver.findElement(By.className('react-autosuggest__suggestion'));
        await firstSuggestion.click();

        const pagarButton = await driver.wait(until.elementLocated(By.id('pay')), 10000);
        await pagarButton.click();
        await driver.sleep(10000);

        console.log(`Test finished successfully for ${track}`);
    } catch (error) {
        console.error(`Error occurred for ${track}:`, error);
    } finally {
        // Close the current tab
        await driver.close();
        // Switch back to the original tab
        const tabs = await driver.getAllWindowHandles();
        await driver.switchTo().window(tabs[0]);
    }
}

async function logUserIntoDriver(driver) {
    // Log user in to Mercado Pago with test account
    await driver.get('https://www.mercadolibre.com/jms/mla/lgz/login?platform_id=mp&go=https://www.mercadopago.com.ar/developers/es?matt_tool=34593899&matt_word=MLA_MP_Sellers_AO_Point_G_PMAX_X_PROS_X&gad_source=1&gclid=EAIaIQobChMI2o_w07vPhgMVSFlIAB2EKAt9EAAYASAAEgIiYvD_BwE');
    await driver.findElement(By.id('user_id')).sendKeys('TESTUSER287488765', Key.RETURN);
    await driver.wait(until.elementLocated(By.id('password')), 5000);
    await driver.findElement(By.id('password')).sendKeys('qexwfb5nzg', Key.RETURN);
    console.log("Logged in successfully!");
}

async function initDriver() {
    const chromeOptions = new chrome.Options();
    // chromeOptions.addArguments("--headless"); // Optional: run without displaying the browser

    // Initialize the WebDriver
    const driver = await new Builder()
        .forBrowser('chrome')
        .setChromeOptions(chromeOptions)
        .build();

    await logUserIntoDriver(driver);
    return driver;
}

async function dispatchTest(songs) {
    // Initialize WebDriver for each song and execute testMusicSearch concurrently
    const testPromises = songs.map(async song => {
        const driver = await initDriver();
        await testMusicSearch(driver, song);
        await driver.quit();
    });

    await Promise.all(testPromises);
}

async function main() {
    const songs = ["Stairway to Heaven", "Imagine", "Hey Jude"]; // Real song names
        await dispatchTest(songs);

    
}

main()
    .then(() => console.log('All batches dispatched successfully'))
    .catch(error => console.error('Error in main:', error));

