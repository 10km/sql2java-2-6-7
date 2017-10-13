package gu.rpc.thrift;

import static org.junit.Assert.*;

import org.junit.Test;

import com.facebook.swift.service.metadata.ThriftServiceMetadata;

public class TestGetServiceMetadata {

	@Test
	public void test() throws ClassNotFoundException {
		ThriftServiceMetadata metadata = SwiftServiceUtils.getServiceMetadata("net.gdface.facelog.FaceLogDefinition", 
				"D:/j/facelog/facelog-main/target/classes",
				"D:/j/facelog/db/target/classes");
		SwiftServiceUtils.output(metadata, System.out);
	
	}

}
