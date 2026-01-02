package com.example.netapp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.netapp.repository.AppointmentRepository;
import com.example.netapp.services.AppointmentService;
import com.example.netapp.entity.AppointmentEntity;
import com.example.netapp.entity.AppointmentStatus;
import com.example.netapp.entity.UserEntity;
import com.example.netapp.exceptions.HttpException;


@RestController
@RequestMapping("/api/v1/appointments")
public class AppointmentController {

	@Autowired
	private AppointmentRepository appointmentRepo;

	@Autowired
	private AppointmentService appointmentService;
	/*
	 * ===========================================
	 * Listing all appointments
	 * ===========================================
	 */
	
	@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
	@GetMapping
	public ResponseEntity<List<AppointmentEntity>> getAllAppointments(){
		return ResponseEntity.ok( appointmentRepo.findAll());
	}
/*
	 * ===========================================
	 * let a user reutnr all of his appointments ( completed and rejected and all ) . 
	 * ===========================================
	 */
    @PreAuthorize("hasRole('CUSTOMER')")
	@GetMapping("/customer")
    public ResponseEntity<List<AppointmentEntity>> getMyAppointments() {
		UserEntity customer =
		        (UserEntity) SecurityContextHolder.getContext()
		            .getAuthentication()
		            .getPrincipal();
		Long id = customer.getUserId();
		if(id == null) {
			throw new HttpException(HttpStatus.BAD_GATEWAY,"the id from the token is null");
		}
			return ResponseEntity.ok(
        		appointmentRepo.findByCustomer_UserId(id)
        );
    }
 /*
	 * ===========================================
	 * Get all the pending requests ( for the admin to change their status later ) 
	 * ===========================================
	 */   
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<AppointmentEntity>> getPendingAppointments() {
        return ResponseEntity.ok(
        		appointmentRepo.findByStatus(
                        AppointmentStatus.PENDING
                )
        );
    }
    /*
	 * ===========================================
	 * get all the appointments made by a user ( this end point is for the admin to see all of them for any user ) . 
	 * ===========================================
	 */   
	@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
	@GetMapping("/customer/{userId}")
    public ResponseEntity<List<AppointmentEntity>> getAppointmentsByCustomer(
            @PathVariable Long userId
    ) {
		if(userId == null) throw new HttpException(HttpStatus.BAD_REQUEST , "add a userId to the path variables");
	
        return ResponseEntity.ok(
        		appointmentRepo.findByCustomer_UserId(userId)
        );
    }
    /*
	 * ===========================================
	 * get all the apointments of a service . 
	 * ===========================================
	 */
	@PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
	@GetMapping("/service/{serviceId}")
    public ResponseEntity<List<AppointmentEntity>> getAppointmentsByService(
            @PathVariable Long serviceId
    ) {
		if(serviceId == null) throw new HttpException(HttpStatus.BAD_REQUEST , "add a serviceId to the path variables");

        return ResponseEntity.ok(
        		appointmentRepo.findByServiceIdAndStatusIn(
                        serviceId,
                        List.of(
                                AppointmentStatus.PENDING,
                                AppointmentStatus.APPROVED,
                                AppointmentStatus.COMPLETED
                        )
                )
        );
    }
	 /*
	 * ===========================================
	 * update an appointment status 
	 * ===========================================
	 */
    @PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{appointmetnId}/accept")
	public ResponseEntity<?> acceptAnAppointment(@PathVariable Long appointmetnId){
		if(appointmetnId == null) throw new HttpException(HttpStatus.BAD_REQUEST , "add a appointmetnId to the path variables");
		return ResponseEntity.ok(  appointmentService.acceptAppointment(appointmetnId));
		
	}
    
    @PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{appointmetnId}/reject")
	public ResponseEntity<?> rejectAnAppointment(@PathVariable Long appointmetnId){
		if(appointmetnId == null) throw new HttpException(HttpStatus.BAD_REQUEST , "add a appointmetnId to the path variables");
		return ResponseEntity.ok(  appointmentService.rejectAppointment(appointmetnId));
		
	}
    
    @GetMapping("/{appointmetnId}/cancel")
	public ResponseEntity<?> cancelAnAppointment(@PathVariable Long appointmetnId){
		if(appointmetnId == null) throw new HttpException(HttpStatus.BAD_REQUEST , "add a appointmetnId to the path variables");
		UserEntity customer =
		        (UserEntity) SecurityContextHolder.getContext()
		            .getAuthentication()
		            .getPrincipal();
		Long id = customer.getUserId();
		if(id == null) {
			throw new HttpException(HttpStatus.BAD_GATEWAY,"the id from the token is null");
		}
		return ResponseEntity.ok(  appointmentService.cancelAppointment(appointmetnId , id));
		
	}
    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{appointmetnId}/cancel/admin")
	public ResponseEntity<?> cancelAnAppointmentByAdmin(@PathVariable Long appointmetnId){
		if(appointmetnId == null) throw new HttpException(HttpStatus.BAD_REQUEST , "add a appointmetnId to the path variables");
		
		return ResponseEntity.ok(  appointmentService.cancelAppointment(appointmetnId , (long) 0));
		
	}
}
