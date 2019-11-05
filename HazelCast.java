package com.kata;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class HazelCast {
    public static void main(String[] args){
        checkEntries();
    }

    public static void checkEntries(){
        WebDriver driver;
        String entries="";

        System.setProperty("webdriver.chrome.driver","C:/Windows/System32/chromedriver.exe");

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        driver.get("http://localhost:8081/mancenter/login.html");

        driver.findElement(By.cssSelector("[name='username'")).sendKeys("d0mu");
        driver.findElement(By.cssSelector("[name='password'")).sendKeys("mihai1986");
        driver.findElement(By.cssSelector("button.btn")).click();
        String cookie = driver.manage().getCookies().toString();
        cookie = cookie.substring(1,50);

        try {

            String sURL = "http://localhost:8081/mancenter/api/clusters/dev/maps";

            URL url = new URL(sURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Cookie", cookie);

            JsonParser jp = new JsonParser();
            JsonReader reader = new JsonReader(new InputStreamReader((InputStream) con.getContent()));
            reader.setLenient(true);

            JsonElement root = jp.parse(reader); //Convert the input stream to a json element

            entries = root.getAsJsonArray().get(0).getAsJsonObject().get("entries").toString();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }

        driver.findElement(By.cssSelector("[data-test='menu-maps']")).click();

        Assert.assertEquals(driver.findElement(By.cssSelector("div.rt-tr.-odd div.rt-td:nth-child(2)")).getText(), entries);

        driver.findElement(By.cssSelector("[title='Logout']")).click();
        driver.close();
    }
}
