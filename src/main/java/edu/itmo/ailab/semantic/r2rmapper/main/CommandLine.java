package edu.itmo.ailab.semantic.r2rmapper.main;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;


public class CommandLine {
	 
	  @Parameter
	  public List<String> parameters = new ArrayList<String>();
	 
	  @Parameter(names = { "-config" }, description = "Path to config file")
	  public String config;
	 
	}