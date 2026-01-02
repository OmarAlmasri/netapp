package com.example.netapp.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.netapp.entity.AppointmentEntity;
import com.example.netapp.entity.AppointmentStatus;
import com.example.netapp.entity.ServiceEntity;
import com.example.netapp.entity.UserEntity;
import com.example.netapp.exceptions.HttpException;
import com.example.netapp.exceptions.NotFoundException;
import com.example.netapp.repository.AppointmentRepository;

import jakarta.transaction.Transactional;

@Service

public class AppointmentService {

	private final AppointmentRepository appointmentRepository; 
	public AppointmentService(AppointmentRepository appointmentRepository) {
		this.appointmentRepository = appointmentRepository;
	}
	
	@Autowired
	private MailServices mail;
	
	public AppointmentEntity acceptAppointment(Long appointmentId) {
		AppointmentEntity appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new NotFoundException("appointment not found"));
		appointment.setApprovedAt(LocalDateTime.now());
		appointment.setStatus(AppointmentStatus.APPROVED);
		appointmentRepository.save(appointment);
		/*
		 * ADD NOTIFICATION SENDING ( IF WE CAN ADD IT AS A BACKGROUND JOB THAT WOULD HELP US EVEN MORE ) 
		 */
		String message = "we would love to tell you that your appointment " + appointment + " was accepted and confirmed " ;
		mail.sendEMail(appointment.getCustomer().getEmail(), "Appointment Accepted", message);
		return appointment;
	}
	
	public AppointmentEntity rejectAppointment(Long appointmentId) {
		AppointmentEntity appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new NotFoundException("appointment not found"));
		appointment.setStatus(AppointmentStatus.REJECTED);
		appointmentRepository.save(appointment);
		/*
		 * ADD NOTIFICATION SENDING ( IF WE CAN ADD IT AS A BACKGROUND JOB THAT WOULD HELP US EVEN MORE ) 
		 */
		String message = "we are sorry to tell you that your appointment " + appointment + " was rejected " ;
		mail.sendEMail(appointment.getCustomer().getEmail(), "Appointment Rejected", message);
		return appointment;
	}
	public AppointmentEntity cancelAppointment(Long appointmentId , Long id) {
		AppointmentEntity appointment = appointmentRepository.findById(appointmentId).orElseThrow(() -> new NotFoundException("appointment not found"));
		
		if(id != 0)
			if(appointment.getCustomer().getUserId() != id)
				throw new HttpException(HttpStatus.UNAUTHORIZED , "you are not autherized to cancel this appointment");
		
		appointment.setStatus(AppointmentStatus.CANCELLED);
		appointmentRepository.save(appointment);
		/*
		 * ADD NOTIFICATION SENDING ( IF WE CAN ADD IT AS A BACKGROUND JOB THAT WOULD HELP US EVEN MORE ) 
		 */
		String message = "we are sorry to tell you that your appointment " + appointment + " was canceled " ;
		mail.sendEMail(appointment.getCustomer().getEmail(), "Appointment canceled", message);
		return appointment;
	}
	
	@Transactional
	public AppointmentEntity createAppointment(
			UserEntity custormer , 
			ServiceEntity service , 
			LocalDateTime start , 
			int sessionsBooked
			){
		if(start.getMinute() % 30 != 0 ) {
			throw new HttpException(HttpStatus.BAD_REQUEST , "the start time must alilgn to a 30-minute block");
		}
		if(service.getDurationMinutes() % 30 != 0 ) {
			throw new HttpException(HttpStatus.BAD_REQUEST , "the service duration must alilgn to a 30-minute block");
		}
		LocalDateTime end = start.plusMinutes(sessionsBooked * service.getDurationMinutes());
		
		boolean overlaps = 
				!appointmentRepository.findOverlappingAppointmentsForService(service.getServiceId(), start, end)
				.isEmpty();
		if(overlaps) {
			throw new HttpException(HttpStatus.BAD_REQUEST , "the time chosen for this service overlaps with the another one ");
		}
		double totalPrice = 
				sessionsBooked * service.getPrice();
		AppointmentEntity appointment = new AppointmentEntity();
		appointment.setCustomer(custormer);
		appointment.setService(service);
		appointment.setStartDateTime(start);
		appointment.setEndDateTime(end);
		appointment.setTotalPrice(totalPrice);
		
		return appointmentRepository.save(appointment);
		
	}
	
	
	
	
	
}
