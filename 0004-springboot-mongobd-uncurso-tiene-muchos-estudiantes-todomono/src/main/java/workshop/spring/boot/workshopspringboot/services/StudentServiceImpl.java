package workshop.spring.boot.workshopspringboot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import workshop.spring.boot.workshopspringboot.models.Student;
import workshop.spring.boot.workshopspringboot.repositories.StudentsRepository;

@Service
public class StudentServiceImpl implements StudentService {

	private StudentsRepository studentRepository;

	@Autowired
	public StudentServiceImpl(StudentsRepository studentRepository) {
		super();
		this.studentRepository = studentRepository;
	}

	public Flux<Student> findAll() {
		Flux<Student> courses = studentRepository.findAll();
		return courses;
	}

	public Mono<Student> findBy(Integer idStudent) {
		Mono<Student> studentSaved = studentRepository.findById(idStudent);

		checkExistStudent(idStudent, studentSaved);

		return studentSaved;
	}

	
	
	public Mono<Student> create(Student student) {
		Assert.notNull(student,"Student cannot be null");
		Assert.isTrue(!exist(student),"StudentId already exist");
		
		Mono<Student> mono = studentRepository.save(student);
		return mono;
	}

	public Mono<Student> update(Integer id, Student student) {

		Mono<Student> studentMono = this.findBy(id);
		 
		studentMono = studentMono.doOnSuccess(studentFinded->{
			studentFinded.setRut(student.getRut());
			studentFinded.setName(student.getName());
			
	 		Mono<Student> studentSaved = studentRepository.save(studentFinded);
	 		studentSaved.subscribe();
		});
		
		return studentMono;
	 
	}

	public Mono<Void> delete(Integer id) {
		findBy(id);
		
		Mono<Void> monoVoid = studentRepository.deleteById(id);
		//monoVoid.subscribe(); 
		
		return monoVoid;
	}
	
	 

	private void checkExistStudent(Integer id, Mono<Student> studentSaved) {
		if (studentSaved == null || studentSaved.block() == null) {
			throw new CourseNotFoundException("Doesnt not exist the Student with id:" + id);
		}
	}

	@Override
	public boolean exist(Student student) {
		boolean existe = false;
		if(student != null) {
			Mono<Student> studentSaved = studentRepository.findById(student.getId());
			existe = studentSaved != null && studentSaved.block() != null;
		}
		
		return existe;
	}

}
