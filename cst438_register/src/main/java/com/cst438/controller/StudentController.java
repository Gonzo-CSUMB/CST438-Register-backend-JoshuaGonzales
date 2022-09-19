package com.cst438.controller;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://registerf-cst438.herokuapp.com/"})
public class StudentController {
	@Autowired
	CourseRepository courseRepository;
	
	@Autowired
	StudentRepository studentRepository;
	
	@Autowired
	EnrollmentRepository enrollmentRepository;
	
	
	@GetMapping("/student/addStudent")
	@Transactional
	public void addStudent(@RequestParam("email") String email,  @RequestParam("name") String name ) {
		//check db for student
		Student stu = studentRepository.findByEmail(email);
	
		//if student does not exist we need to add them to the "system"
		if(stu == null){
			//create new student
			stu = new Student();
			stu.setName(name);
			stu.setEmail(email);
			
			//save new entity to db, so easy :D
			studentRepository.save(stu);
			
			System.out.println("New Student added "+ stu.toString()); 
			
		}else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student already exists in system" + stu.toString());
		}
	}
	
	@GetMapping("/student/registrationHold")
	@Transactional
	public void registrationHold(@RequestParam("email")String email) {
		//check db for student
		Student stu = studentRepository.findByEmail(email);
	
		//if student does not exist we need to add them to the "system"
		if(stu != null && stu.getStatusCode()!=1){
			stu.setStatusCode(1); //1 == registration hold
			studentRepository.save(stu);
		}else if(stu == null){
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student not found.");
		}else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student registration already on hold");
		}
	}
	
	@GetMapping("/student/releaseHold")
	@Transactional
	public void releaseHold(@RequestParam("email")String email) {
		//check db for student
		Student stu = studentRepository.findByEmail(email);
	
		//if student does not exist we need to add them to the "system"
		if(stu != null && stu.getStatusCode()!=0){
			stu.setStatusCode(0);//0 == no hold
			studentRepository.save(stu);
		}else if(stu == null){
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student not found.");
		}else {
			throw  new ResponseStatusException( HttpStatus.BAD_REQUEST, "Student has no holds on their registration");
		}
	}
}
