package com.hlj;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Reservation {
	private Long id;
	
	private String name;//花名
	private String comment; //备注
	private LocalDate createdDate;
}
