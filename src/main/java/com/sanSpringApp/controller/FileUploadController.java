package com.sanSpringApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;


@RestController
public class FileUploadController {

	@Autowired
	FileUploadServiceImpl fileUploadServiceImpl;

	 
	 	@ApiOperation(value = "Import Data File")
		@RequestMapping(value = "/import", method = RequestMethod.GET)
		public ResponseEntity<String> importFile() {
	 		fileUploadServiceImpl.processImports();
	 		return new ResponseEntity<>("File has been imported and saved", HttpStatus.OK);
	 		  
		}
	 
	 
}