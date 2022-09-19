package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cst438.controller.StudentController;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;


@ContextConfiguration(classes = { StudentController.class })
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
public class JunitTestStudent {
	//test variables- fake stuff
	static final String URL = "http://localhost:8080";
	public static final String TEST_STUDENT_EMAIL = "test2@csumb.edu";
	public static final String TEST_STUDENT_NAME  = "test2";
	
	//mockbeans have to micmic the repositories in the class being tested
	@MockBean
	StudentRepository studentRepository;
	
	@MockBean
	CourseRepository courseRepository;

	@MockBean
	EnrollmentRepository enrollmentRepository;
	
	@Autowired
	private MockMvc mvc;
	
	@Test
	public void addStudent() throws Exception{
		//fake response from mockmvc?
		MockHttpServletResponse response;
		
		//make fake student
		Student student = new Student();
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setName(TEST_STUDENT_NAME);
		
		
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .get("/student/addStudent?email=" + TEST_STUDENT_EMAIL + "&name=" + TEST_STUDENT_NAME)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
		
		//make fake call to db return fake student 
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
		
		//run same request with added student to see if the 'already exists error' appears
		response = mvc.perform(
				MockMvcRequestBuilders
			      .get("/student/addStudent?email=" + TEST_STUDENT_EMAIL + "&name=" + TEST_STUDENT_NAME)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		
		
		// verify that function will not add existing students
		assertEquals(400, response.getStatus());
	}
	
	@Test
	public void registrationHold() throws Exception{
		MockHttpServletResponse response;
		
		//make fake student
		Student student = new Student();
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setName(TEST_STUDENT_NAME);
		student.setStatusCode(0);
		
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
		
		//basis request to hold existing student with no holds
		response = mvc.perform(
				MockMvcRequestBuilders
			      .get("/student/registrationHold?email=" + TEST_STUDENT_EMAIL )
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		
		student.setStatusCode(1);
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .get("/student/registrationHold?email=" + TEST_STUDENT_EMAIL)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(400, response.getStatus());
		
		//return null
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(null);
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .get("/student/registrationHold?email=" + TEST_STUDENT_EMAIL)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(400, response.getStatus());
	}
	
	@Test
	public void releaseHold() throws Exception{
		MockHttpServletResponse response;
		
		//make fake student
		Student student = new Student();
		student.setEmail(TEST_STUDENT_EMAIL);
		student.setName(TEST_STUDENT_NAME);
		student.setStatusCode(1);
		
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
		
		//basis request to release hold on existing student with a hold
		response = mvc.perform(
				MockMvcRequestBuilders
			      .get("/student/releaseHold?email=" + TEST_STUDENT_EMAIL )
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(200, response.getStatus());
		
		student.setStatusCode(0);
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(student);
		
		//request to release hold with student with no holds, should fail
		response = mvc.perform(
				MockMvcRequestBuilders
			      .get("/student/releaseHold?email=" + TEST_STUDENT_EMAIL )
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		assertEquals(400, response.getStatus());
		
		
		given(studentRepository.findByEmail(TEST_STUDENT_EMAIL)).willReturn(null);
		
		//request to release hold with student with no holds, should fail
		response = mvc.perform(
				MockMvcRequestBuilders
			      .get("/student/releaseHold?email=" + TEST_STUDENT_EMAIL )
				      .accept(MediaType.APPLICATION_JSON))
					.andReturn().getResponse();
			
		assertEquals(400, response.getStatus());
}
}
