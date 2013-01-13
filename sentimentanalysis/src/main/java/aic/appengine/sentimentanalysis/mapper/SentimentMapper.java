// Copyright 2012 Google Inc. All Rights Reserved.

package aic.appengine.sentimentanalysis.mapper;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.Mapper;

import java.util.logging.Logger;

/*
 * GAE Map/Reduce is experimental you have to compile the jar files first
 * https://code.google.com/p/appengine-mapreduce/wiki/GettingStartedInJava
 * 
 * Check out the MapReduce code:
 * svn checkout http://appengine-mapreduce.googlecode.com/svn/trunk/java
 * Then, build the code using ant in the directory you just checked out:
 *
 * cd java
 * ant
 * 
 * Then call the following in our project folder:
 * 
 * mvn install:install-file -Dfile=../../java/dist/lib/appengine-mapper.jar
 *    -Dpackaging=jar -DgroupId=com.google.appengine
 *    -DartifactId=appengine-mapper -Dversion=1.7.3
 */
public class SentimentMapper extends Mapper<Entity, String, Double> {
	private static final long serialVersionUID = -7502805571528438406L;
	private static final Logger log = Logger.getLogger(SentimentMapper.class
			.getName());
	private String searchTerm;


	public SentimentMapper(String searchTerm) {
		this.searchTerm=searchTerm;
	}

	@Override
	public void beginShard() {
		log.info("beginShard()");
	}

	@Override
	public void beginSlice() {
		log.info("beginSlice()");
	}

	@Override
	public void map(Entity entity) {
		String text=(String)entity.getProperty("text");
		Double sentiment=(Double)entity.getProperty("sentiment");
		if(text.indexOf(searchTerm)!=-1){
			getContext().emit("mean", sentiment);
		}
	}

	@Override
	public void endShard() {
		log.info("endShard()");
	}

	@Override
	public void endSlice() {
		log.info("endSlice()");
	}

}
