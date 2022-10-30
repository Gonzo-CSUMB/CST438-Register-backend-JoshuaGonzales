package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;

@SpringBootTest
public class EndToEndAddStuTest {

	public static final String CHROME_DRIVER_FILE_LOCATION = "C:\\Users\\joshu\\OneDrive\\Documents\\cst438\\chromedriver.exe";

	public static final String URL = "http://localhost:3000";
	
	public static final String TEST_STUDENT_EMAIL = "testTest@csumb.edu";
	public static final String TEST_STUDENT_NAME  = "testTest";

	public static final int TEST_COURSE_ID = 40443; 

	public static final String TEST_SEMESTER = "2021 Fall";

	public static final int SLEEP_DURATION = 1000; // 1 second.

	/*
	 * When running in @SpringBootTest environment, database repositories can be used
	 * with the actual database.
	 */
	
	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	StudentRepository studentRepository;
	
	
	@Test
	public void addStudentTest() throws Exception {

		/*
		 * if student is already added, then delete the student.
		 */
		
		Student stu = null;
		do {
			 stu = studentRepository.findByEmail(TEST_STUDENT_EMAIL);
			if (stu != null)
				studentRepository.delete(stu);
		} while (stu != null);

		// set the driver location and start driver

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		try {

			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);

			// Locate and click "Add Student" button, last button
			
			driver.findElement(By.xpath("//button[text()='Add Student']")).click();
			Thread.sleep(SLEEP_DURATION);

			// enter student name/email and click Add button
			
			driver.findElement(By.xpath("//input[@name='name']")).sendKeys(TEST_STUDENT_NAME);
			driver.findElement(By.xpath("//input[@name='email']")).sendKeys(TEST_STUDENT_EMAIL);
			driver.findElement(By.xpath("//button[@id='Add']")).click();
			Thread.sleep(SLEEP_DURATION);

			
			// verify that the student has been inserted to database.
			
			Student stuAdded = studentRepository.findByEmail(TEST_STUDENT_EMAIL);
			assertNotNull(stuAdded, "Student not found in database.");

		} catch (Exception ex) {
			throw ex;
		} finally {

			// clean up database.
			
			Student stuAdded = studentRepository.findByEmail(TEST_STUDENT_EMAIL);
			if (stuAdded != null)
				studentRepository.delete(stuAdded);

			driver.quit();
		}

	}
}
