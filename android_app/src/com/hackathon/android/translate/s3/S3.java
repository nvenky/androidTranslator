package com.hackathon.android.translate.s3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.StorageClass;

public class S3 {

	private static AmazonS3Client s3 = null;

	public static AmazonS3 getInstance() {
		if (s3 != null) {
			return s3;
		}
		AWSCredentials credentials = new BasicAWSCredentials("AKIAJXINCOD3QJRYK6EA", "6XFsjowbC01VRN2X9wJUaN6NwJ/gH7PFuChvLwj7");
		s3 = new AmazonS3Client(credentials);
		return s3;
	}

	public static List<String> getBucketNames() {
		List<Bucket> buckets = getInstance().listBuckets();
		List<String> bucketNames = new ArrayList<String>(buckets.size());
		Iterator<Bucket> bIter = buckets.iterator();
		while (bIter.hasNext()) {
			bucketNames.add((bIter.next().getName()));
		}
		return bucketNames;
	}

	public static void createBucket(String bucketName) {
		getInstance().createBucket(bucketName);
	}

	public static void deleteBucket(String bucketName) {
		getInstance().deleteBucket(bucketName);
	}

	public static void createObjectForBucket(String bucketName, String objectName, String data) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(data.getBytes().length);
			//SAVE COST WITH RRS..
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, bais, metadata).withStorageClass(StorageClass.ReducedRedundancy);
			getInstance().putObject(putObjectRequest);
		} catch (Exception exception) {
			Log.e("TODO", "createObjectForBucket");
		}
	}

	public static void deleteObject(String bucketName, String objectName) {
		getInstance().deleteObject(bucketName, objectName);
	}

	public static String getDataForObject(String bucketName, String objectName) {
		return read(getInstance().getObject(bucketName, objectName).getObjectContent());
	}

	protected static String read(InputStream stream) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(8196);
			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = stream.read(buffer)) > 0) {
				baos.write(buffer, 0, length);
			}
			return baos.toString();
		} catch (Exception exception) {
			return exception.getMessage();
		}
	}
}
