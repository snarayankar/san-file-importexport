package com.sanSpringApp.dto;

import lombok.Data;

@Data
public class FileDataExportRow {
	public static final String[] HEADER = new String[] {
		"ID",
		"YEAR",
		"PRESIDENT",
		"FIRSTLADY",
		"VICEPRESIDENT"
	};
	
	public static final String[] FIELDS = new String[] {
		"id",
		"year",
		"president",
		"firstLady",
		"vicePresident"
	};
	
	private Long id;
	private String year;
	private String president;
	private String firstLady;
	private String vicePresident;
}
