// Copyright 2012 Google Inc. All Rights Reserved.

package aic.appengine.sentimentanalysis.mapper;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.mapreduce.Mapper;

import java.util.logging.Logger;

class SentimentMapper extends Mapper<Entity, String, Double> {
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
