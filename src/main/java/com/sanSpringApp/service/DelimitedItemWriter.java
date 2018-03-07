package com.sanSpringApp.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.AbstractItemStreamItemWriter;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
public class DelimitedItemWriter<T> extends AbstractItemStreamItemWriter<T> {
	private static final String DEFAULT_LINE_SEPARATOR = System.getProperty("line.separator");
	
	private String lineSeparator = DEFAULT_LINE_SEPARATOR;
	private OutputStream outputStream;
	private DelimitedLineAggregator<T> lineAggregator;
	private String[] header;
	private String[] fields;
	private String delimiter = ",";
	private boolean headerWritten;
	
	public DelimitedItemWriter(String[] header, String[] fields, String delimiter, OutputStream outputStream) {
		this(header, fields, outputStream);
		this.delimiter = delimiter;
	}
	
	public DelimitedItemWriter(String[] header, String[] fields, OutputStream outputStream) {
		this(fields, outputStream);
		this.header = header;
	}
	
	public DelimitedItemWriter(String[] fields, OutputStream outputStream) {
		this.fields = fields;
		this.outputStream = outputStream;
		
		this.init();
	}
	
	public DelimitedItemWriter() {/*default constructor*/}
	
	private void init() {
		BeanWrapperFieldExtractor<T> fieldExtractor = new BeanWrapperFieldExtractor<>();
		fieldExtractor.setNames(this.fields);

		this.lineAggregator = new DelimitedLineAggregator<>();
		this.lineAggregator.setDelimiter(this.delimiter);
		this.lineAggregator.setFieldExtractor(fieldExtractor);
	}
	
	@Override
	public void write(List<? extends T> items) throws IOException {
		for(T item : items) {
			this.write(item);
		}
	}
	
	public void write(T item) throws IOException {
		if(this.lineAggregator == null) {
			this.init();
		}
		
		if(!headerWritten && this.header != null && this.header.length != 0) {
			this.outputStream.write((String.join(this.delimiter, this.header)+this.lineSeparator).getBytes());
			headerWritten = true;
		}
		
		String line = this.lineAggregator.aggregate(item) + this.lineSeparator;
		this.outputStream.write(line.getBytes());
	}
	
	public String getDelimiter() {
		return this.delimiter;
	}
	
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
		this.init();
	}
	
	public String[] getHeader() {
		return this.header;
	}
	
	public void setHeader(String[] header) {
		this.header = header;
	}
	
	public String[] getFields() {
		return this.fields;
	}
	
	public void setFields(String[] fields) {
		this.fields = fields;
		this.init();
	}
	
	public OutputStream getOutputStream() {
		return this.outputStream;
	}
	
	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	public String getLineSeparator() {
		return this.lineSeparator;
	}
	
	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}
}
