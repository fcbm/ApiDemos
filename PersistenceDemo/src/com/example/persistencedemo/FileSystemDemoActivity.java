package com.example.persistencedemo;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FileSystemDemoActivity extends Activity {

	private static final String INTERNAL_DIRNAME = "internal_dir";
	private static final String INTERNAL_FNAME = "internal_fname.txt";
	private static final String INTERNAL_FNAME_IN_SUBDIR = "internal_fname_in_subdir.txt";
	private static final String EXTERNAL_FNAME = "external_fname.txt";
	
	@TargetApi(19)
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filesystem_demo );
		
		String[] fileList = fileList();
		StringBuilder sb = new StringBuilder();
		for (String s : fileList)
		{
			sb.append("- " + s + "\n");
		}
		if (fileList.length == 0)
		{
			sb.append("No Files!");
		}
		TextView tvFileList = (TextView) findViewById( R.id.tvFileList );
		tvFileList.setText( sb.toString() );
		
		TextView tvInternalPath = (TextView) findViewById( R.id.tvInternalPath );
		File internalDir = getDir(INTERNAL_DIRNAME, Activity.MODE_PRIVATE);
		sb = new StringBuilder();
		getFileInfo(internalDir, sb);
		tvInternalPath.setText( sb.toString() );
		
		TextView tvExternalPath = (TextView) findViewById( R.id.tvExternalPath );
		sb = new StringBuilder();
		buildExternalPath(Environment.DIRECTORY_ALARMS, sb);
		buildExternalPath(Environment.DIRECTORY_DCIM, sb);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
		{
			buildExternalPath(Environment.DIRECTORY_DOCUMENTS, sb);
		}
		buildExternalPath(Environment.DIRECTORY_DOWNLOADS, sb);
		buildExternalPath(Environment.DIRECTORY_MOVIES, sb);
		buildExternalPath(Environment.DIRECTORY_MUSIC, sb);
		buildExternalPath(Environment.DIRECTORY_NOTIFICATIONS, sb);
		buildExternalPath(Environment.DIRECTORY_PICTURES, sb);
		buildExternalPath(Environment.DIRECTORY_PODCASTS, sb);
		buildExternalPath(Environment.DIRECTORY_RINGTONES, sb);
		tvExternalPath.setText( sb.toString() );
		
		TextView tvOldExternalPath = (TextView) findViewById( R.id.tvOldExternalPath );
		File fOldExternal = Environment.getExternalStorageDirectory();
		sb = new StringBuilder();
		getFileInfo(fOldExternal, sb);
		tvOldExternalPath.setText( sb.toString() );
		
		TextView tvPrivateFileReadWrite = (TextView) findViewById( R.id.tvPrivateFileReadWrite);
		try {
			FileOutputStream fOut = openFileOutput(INTERNAL_FNAME, Context.MODE_PRIVATE|Context.MODE_APPEND);
			fOut.write( new String("Internal File opened at " + (new Date()).toString() + "\n").getBytes() );
			fOut.close();
			
			FileInputStream fIn = openFileInput(INTERNAL_FNAME);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			MainActivity.readFromInputStream(fIn, baos);
			
			tvPrivateFileReadWrite.setText( baos.toString() );
		} catch (FileNotFoundException e) {
			tvPrivateFileReadWrite.setText( e.toString() );
		} catch (IOException e) {
			tvPrivateFileReadWrite.setText( e.toString() );
		}
		
		Button b = (Button) findViewById( R.id.btnDeletePrivateFile);
		b.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				deleteFile(INTERNAL_FNAME);
			}
		});
		

		TextView tvPrivateNestedFileReadWrite = (TextView) findViewById( R.id.tvPrivateNestedFileReadWrite);
		final File internalNestedFile = new File(internalDir, INTERNAL_FNAME_IN_SUBDIR);
		try {
			
			FileWriter fOut = new FileWriter(internalNestedFile.getCanonicalPath(), true);
			BufferedWriter bw = new BufferedWriter( fOut );
			bw.write( new String("Internal Nested File opened at " + (new Date()).toString() + "\n") );
			bw.close();
			
			FileInputStream fIn = new FileInputStream(internalNestedFile);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			MainActivity.readFromInputStream(fIn, baos);
			
			tvPrivateNestedFileReadWrite.setText( baos.toString() );
		} catch (FileNotFoundException e) {
			tvPrivateNestedFileReadWrite.setText( e.toString() );
		} catch (IOException e) {
			tvPrivateFileReadWrite.setText( e.toString() );
		} finally {}
		
		Button b1 = (Button) findViewById( R.id.btnDeletePrivateNestedFile);
		b1.setOnClickListener( new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				internalNestedFile.delete();
				// File name passed to deleteFile can't contain separator
				//deleteFile(internalNestedFile.getName());
			}
		});		
		
		TextView tvGetFilesDir = (TextView) findViewById( R.id.tvGetFilesDir );
		tvGetFilesDir.setText( getFilesDir().toString() );
		
		TextView tvGetFileStreamPath = (TextView) findViewById( R.id.tvGetFileStreamPath);
		sb = new StringBuilder();
		sb.append( "- " + INTERNAL_FNAME + " : " + getFileStreamPath( INTERNAL_FNAME ) + "\n");
		sb.append( "- " + INTERNAL_FNAME_IN_SUBDIR + " : " + getFileStreamPath( INTERNAL_FNAME_IN_SUBDIR)+ "\n");
		sb.append( "- " + INTERNAL_DIRNAME + " : " + getFileStreamPath( INTERNAL_DIRNAME)+ "\n");
		sb.append( "- " + "not existing file" + " : " + getFileStreamPath( "dummyname")+ "\n");
		tvGetFileStreamPath.setText( sb.toString() );
		
		//getDir(String name, int mode)
		//Retrieve, creating if needed, a new directory in which the application can place its own custom data files.
		
		//getFilesDir()
		//Returns the absolute path to the directory on the filesystem where files created with openFileOutput(String, int) are stored.
		
		//getFileStreamPath(String name)
		//Returns the absolute path on the filesystem where a file created with openFileOutput(String, int) is stored.
		TextView tvCachePaths = (TextView) findViewById( R.id.tvCachePaths);
		sb = new StringBuilder();
		sb.append( "Internal Cache path : " + getCacheDir() + "\n");
		sb.append( "External Cache path : " + getExternalCacheDir() + "\n");
		tvCachePaths.setText( sb.toString() );
		
		
	}
	
	private void buildExternalPath(String dir, StringBuilder sb)
	{
		File externalFile = getExternalFilesDir(dir);
		sb.append("Looking for : " + dir + " (private)\n");
		getFileInfo(externalFile, sb);
		sb.append("Looking for : " + dir + " (public)\n");
		File externalPublicDir = Environment.getExternalStoragePublicDirectory(dir);
		getFileInfo(externalPublicDir, sb);
		sb.append("---\n");
	}
	
	private void getFileInfo(File f, StringBuilder sb)
	{
		if (f == null)
		{
			sb.append(" not found\n");
			return;
		}
			
		sb.append("AbsolutePath: " + f.getAbsolutePath() + "\n");
		try {
			sb.append("CanonicalPath: " + f.getCanonicalPath() + "\n");
		} catch (IOException e) {
			sb.append("CanonicalPath: Failed to retrieve canonical path!\n");
		}
		sb.append("Parent: " + f.getParent() + "\n");
		sb.append("Path: " + f.getPath() + "\n");
		sb.append("Name: " + f.getName() + "\n");
	}
}

