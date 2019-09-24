package workshop.spring.boot.workshopspringboot.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import workshop.spring.boot.workshopspringboot.models.Course;
import workshop.spring.boot.workshopspringboot.models.Student;
import workshop.spring.boot.workshopspringboot.repositories.CourseRepository;

@Service
public class CourseServiceImpl implements CourseService {

	private CourseRepository courseRepository;
	private StudentService studentService;
	
	@Autowired
	public CourseServiceImpl(CourseRepository courseRepository, StudentService studentService) {
		super();
		this.courseRepository = courseRepository;
		this.studentService = studentService;
	}

	@Override
	public Flux<Course> findAll() {
		Flux<Course> courses = courseRepository.findAll();
		return courses;
	}

	@Override
	public Mono<Course> findBy(Integer idCourse) {
		Mono<Course> courseSaved = courseRepository.findById(idCourse);

		checkExistCourse(idCourse, courseSaved);

		return courseSaved;
	}
	
	@Override
	public Mono<Course> monoFindBy(Integer idCourse) {
		Mono<Course> courseSaved = courseRepository.findById(idCourse);

		checkExistCourse(idCourse, courseSaved);

		return courseSaved;
	}

	@Override
	public Mono<Course> create(Course course) {
		Mono<Course> mono = courseRepository.save(course);
		
		return mono;
	}

	@Override
	public Mono<Course> update(Integer id, Course course) {

		Mono<Course> courseSaved = this.findBy(id);
		courseSaved.doOnSuccess(courseFinded->{
			courseFinded.setName(course.getName());
			courseFinded.setStudent(course.getStudent());

			Mono<Course> mono = courseRepository.save(courseFinded);
			mono.subscribe();
		});
		
		
		return courseSaved;
	}

	private void checkExistCourse(Integer id, Mono<Course> courseSaved) {
		if (courseSaved == null || courseSaved.block() == null) {
			throw new CourseNotFoundException("Doesnt not exist the course with id:" + id);
		}
	}

	@Override
	public Mono<Void> delete(Integer id) {
		return courseRepository.deleteById(id);
	}

	@Override
	public Mono<Course> addStudent(Integer idCourse, Integer idStudent) {
		Mono<Course> courseFinded = findBy(idCourse);
		Student studentToAdd = studentService.findBy(idStudent).block();
		
		courseFinded.doOnSuccess(course->{
			System.out.println("course");
			
			List<Student> students = course.getStudent();
			if(students==null) {
				students = new ArrayList<>();
				course.setStudent(students);
			}
			
			Boolean existe = false;
			
			for(Student student : course.getStudent()) {
				if(student.getId() == idStudent) {
					student.setName(studentToAdd.getName());
					student.setRut(studentToAdd.getRut());
					existe = true;
				}
			}
			
			if(!existe) {
				course.getStudent().add(studentToAdd);	
			}
			
			update(idCourse, course);
		});
		
		
		return courseFinded;
	}

}
