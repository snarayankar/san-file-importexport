package com.sanSpringApp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sanSpringApp.dto.FileDataDto;
import com.sanSpringApp.dto.FileDataExportRow;
import com.sanSpringApp.service.DelimitedItemWriter;

@Service("fileUploadServiceImpl")
public class FileUploadServiceImpl {

	// @Value("#{env[@environment+'.file.directory.contextRoot']}") - Can add a
	// resource file and define the values
	private String importFileLocation = "C:\\san\\fileToImport";

	// @Value("#{env[@environment+'.file.directory.contextRoot']}") - Can add a
	// resource file and define the values
	private String outputFileLocation = "C:\\san\\resultFiles";

	@Transactional
	public void processImports() {
		// System.out.println("Process File Data Start : The File Location is " +
		// importFileLocation);

		File f = new File(importFileLocation);
		File[] listOfFiles = f.listFiles();

		// If we want to import multiple files we can do that using the below stream
		if (null != listOfFiles && listOfFiles.length > 0) {
			try (Stream<Path> filePathStream = Files.walk(Paths.get(importFileLocation))) {
				filePathStream.forEach(filePath -> {
					if (Files.isRegularFile(filePath)) {
						String extension = FilenameUtils.getExtension(filePath.toString());
						String fileName = FilenameUtils.getName(filePath.toString());
						System.out.println("Process File Data: Checking the following file for extension " + filePath
								+ " extension: " + extension + " fileName: " + fileName);
						if (extension.equals("xlsx")) {
							System.out.println("Process File Data: File being processed is: " + fileName);
							processFilesFromDirectory(filePath.toString());
						}
					}
				});
				System.out.println("Process File Data: Processing complete");
			} catch (IOException e1) {
				System.out.println("Process File Data: There was an exception reading the files located at :"
						+ importFileLocation + " IOexception is: " + e1);

			}
		} else {
			System.out.println("Process File Data: No Files exists at " + importFileLocation);
		}

	}

	private void processFilesFromDirectory(String importFileLocation) {
		try (FileInputStream file = new FileInputStream(new File(importFileLocation))) {
			@SuppressWarnings("resource")
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			XSSFSheet sheet = workbook.getSheetAt(0);

			final List<String> FILE_COLUMNS = new ArrayList<>(
					Arrays.asList("ID", "YEAR", "PRESIDENT", "FIRST_LADY", "VICE_PRESIDENT"));
			final HashMap<Long, String> FILE_COLUMNS_INDEX = new HashMap<>();

			Iterator<Row> rows = sheet.rowIterator();
			LocalDateTime date = LocalDateTime.now();

			String fileName = "Exporting_Data_" + date.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".csv";
			try (FileOutputStream fos = new FileOutputStream(this.outputFileLocation + "/" + fileName)) {

				DelimitedItemWriter<FileDataDto> delimitedItemWriter = new DelimitedItemWriter<>(
						FileDataExportRow.HEADER, FileDataExportRow.FIELDS, fos);
				while (rows.hasNext()) {
					FileDataDto fileData = new FileDataDto();
					XSSFRow row = (XSSFRow) rows.next();
					Iterator<Cell> cells = row.cellIterator();

					if (row.getRowNum() == 0) {
						while (cells.hasNext()) {
							XSSFCell cell = (XSSFCell) cells.next();
							if (FILE_COLUMNS.contains(cell.getStringCellValue())) {
								FILE_COLUMNS_INDEX.put((long) cell.getColumnIndex(), cell.getStringCellValue());
							}
						}

					} else {
						while (cells.hasNext()) {
							XSSFCell cell = (XSSFCell) cells.next();
							if (FILE_COLUMNS_INDEX.containsKey((long) cell.getColumnIndex())) {
								switch (FILE_COLUMNS_INDEX.get((long) cell.getColumnIndex())) {
								case "ID":
									fileData.setId((long) cell.getNumericCellValue());
									break;

								case "YEAR":
									fileData.setYear((String) cell.getStringCellValue());
									break;

								case "PRESIDENT":
									fileData.setPresident(cell.getStringCellValue());
									break;

								case "FIRST_LADY":
									fileData.setFirstLady((String) cell.getStringCellValue());
									break;

								case "VICE_PRESIDENT":
									fileData.setVicePresident((String) cell.getStringCellValue());
									break;
								}
							}
						}
						delimitedItemWriter.write(fileData);
					}
				}

			}

			System.out.println("Process File Data: Completed processing File: " + importFileLocation);
		} catch (FileNotFoundException e1) {
			System.out.println(
					"Process File Data: There was a FileNotFoundException reading the file, the exception is: " + e1);
		} catch (IOException e2) {
			System.out
					.println("Process File Data: There was a IOException reading the file, the IOException is: " + e2);
		}

	}

}
