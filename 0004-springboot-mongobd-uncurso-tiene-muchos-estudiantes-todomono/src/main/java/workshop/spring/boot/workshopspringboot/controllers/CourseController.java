package workshop.spring.boot.workshopspringboot.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import workshop.spring.boot.workshopspringboot.models.Course;
import workshop.spring.boot.workshopspringboot.services.CourseService;

@RestController
@RequestMapping("/courses")
public class CourseController {

	private CourseService courseService;
	
	@Autowired
	public CourseController(CourseService courseService) {
		super();
		this.courseService = courseService;
	}

	@RequestMapping
	@ResponseBody
	public Flux<Course> findAll() {
		return courseService.findAll();
	}
	
	@PostMapping
	@ResponseBody
	public ResponseEntity<Mono<Course>> create(@Valid @RequestBody Course course ) {
		Mono<Course> courseCreate = courseService.create(course);
		
		return new ResponseEntity<Mono<Course>>(courseCreate, HttpStatus.OK);
	}
	
	@PutMapping("/{id}")
	@ResponseBody
	public ResponseEntity<Mono<Course>> update(@PathVariable(value="id") Integer id , @RequestBody Course course) {
		Mono<Course> courseSaved = courseService.update(id, course);
		
		return new ResponseEntity<Mono<Course>>(courseSaved, HttpStatus.ACCEPTED);
		
	}
	
	@DeleteMapping("/{id}")
	@ResponseBody
	public ResponseEntity<Mono<Void>> delete(@PathVariable(value="id") Integer id) {
		Mono<Void> deleted = courseService.delete(id);
		
		return new ResponseEntity<Mono<Void>>(deleted, HttpStatus.OK);
	}
	
	@PutMapping("/{courseId}/{studentId}")
	@ResponseBody
	public ResponseEntity<Mono<Course>> addStudent(@PathVariable(value = "studentId") Integer idStudent,
			@PathVariable(value = "courseId") Integer idCourse) {
		
		Mono<Course> courseSaved = courseService.addStudent(idCourse, idStudent);

		return new ResponseEntity<Mono<Course>>(courseSaved, HttpStatus.OK);
	}
	
}
