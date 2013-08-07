package edu.itmo.ailab.semantic.r2rmapper.main;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;

/**
 * R2R Mapper. It is a free software.
 *
 * Parsing of command line args.
 * Author: Ilya Semerhanov
 * Date: 06.08.13
 */
public class CommandLine {

    @Parameter
    public List<String> parameters = new ArrayList<>();

    @Parameter(names = {"--config"}, description = "Path to config file")
    public String config;

    @Parameter(names = {"--step"}, description = "Define run step")
    public String step;

    @Parameter(names = {"--settings"}, description = "Define path to settings")
    public String settings;

}