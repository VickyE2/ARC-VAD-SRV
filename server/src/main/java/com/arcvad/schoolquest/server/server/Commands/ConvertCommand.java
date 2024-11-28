package com.arcvad.schoolquest.server.server.Commands;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

import static com.arcvad.schoolquest.server.server.DataFormat.Converter.DataConverter.convertToSql;
import static com.arcvad.schoolquest.server.server.GlobalUtils.Config.getConfigValue;

@Command(name="convert",
        version="beta-1.0",
        description = "User sensibly. used to change from SQLITE, XML, or JSON data format",
        mixinStandardHelpOptions = true
    )
    public class ConvertCommand implements Callable<Integer> {

        @Option(names = {"-D", "--delay"})
        private int delay;

        @Parameters(
            description = "Write a non-case sensitive valid data format. <XML, JSON, SQLITE>"
        )
        public String dataFormat;

        @Override
        public Integer call() throws Exception {
            if (!dataFormat.equalsIgnoreCase("XML") &&
                !dataFormat.equalsIgnoreCase("JSON") &&
                !dataFormat.equalsIgnoreCase("SQLITE")){
                System.out.println(StringTemplate.STR."Data format: \{dataFormat} is not a valid data format <XML, JSON, SQLITE>");
            }else if(dataFormat.equalsIgnoreCase("XML")){
                if (getConfigValue("former_data_format").toString().equalsIgnoreCase("XML")){
                    System.out.println("Server data format is already in XML format...");
                }else if(getConfigValue("former_data_format").toString().equalsIgnoreCase("JSON")){

                }else if(getConfigValue("former_data_format").toString().equalsIgnoreCase("SQLITE")){
                    boolean hasConvertedToSql = convertToSql();
                }
            }
            return 0;
        }
    }

