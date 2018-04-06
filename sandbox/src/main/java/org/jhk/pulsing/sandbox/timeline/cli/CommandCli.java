/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jhk.pulsing.sandbox.timeline.cli;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.jhk.pulsing.sandbox.timeline.TimeLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jhk.pulsing.sandbox.timeline.cache.UserCache;
import org.jhk.pulsing.sandbox.timeline.pojo.Message;
import org.jhk.pulsing.sandbox.timeline.pojo.User;
import org.jhk.pulsing.sandbox.timeline.queue.TweetConsumerMimic;
import org.jhk.pulsing.sandbox.timeline.util.Util;
import org.jhk.pulsing.sandbox.timeline.queue.IConsumerMimic;
import org.jhk.pulsing.sandbox.timeline.queue.KafkaMimic;

/**
 * @author Ji Kim
 */
public final class CommandCli {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandCli.class);
    
    private static final String FOLLOWS_CSV = "follows.csv";
    private static final String TWEETS_CSV = "tweets.csv";
    private static final String USERS_CSV = "users.csv";
    
    public static void main(String[] args) {
        CommandCli starter = new CommandCli();
        starter.run();
    }
    
    private UserCache userCache;
    private TimeLine timeLine;
    private IConsumerMimic consumer;
    private KafkaMimic queue;
    
    private CommandLineParser cliParser;
    private Options cliOptions;
    private HelpFormatter cliFormatter;
    
    private CommandCli() {
        userCache = new UserCache();
        timeLine = new TimeLine(userCache);
        consumer = new TweetConsumerMimic(timeLine);
        queue = new KafkaMimic(10);
        
        cliParser = new DefaultParser();
        cliOptions = new Options();
        createCommandOptions();
        cliFormatter = new HelpFormatter();
    }
    
    private void run() {
        userCache.loadUsers(CommandCli.class.getResourceAsStream(USERS_CSV));
        userCache.loadFollowers(CommandCli.class.getResourceAsStream(FOLLOWS_CSV));
        
        queue.addConsumer(consumer);
        queue.run();
        
        // base load of tweets
        Util.readStream(CommandCli.class.getResourceAsStream(TWEETS_CSV), line -> {
            consumer.processMessage(new Message(line));
        });
        
        LOGGER.info("Ready for input...");
        Scanner input = new Scanner(System.in);
        while(input.hasNextLine()) {
            String command = input.nextLine();
            processCommandLine(command);
        }
        
        input.close();
        queue.cancel();
    }
    
    private void processCommandLine(String command) {
        try {
            // only support 3 args max
            String[] split = command.split(" ");
            if (split.length > 2) {
                // hacky for now...
                String[] temp = new String[3];
                temp[0] = split[0];
                temp[1] = split[1];
                
                StringJoiner joiner = new StringJoiner(" ");
                for(int loop=2; loop < split.length; loop++) {
                    joiner.add(split[loop]); // seriously why doesn't StringJoiner accept a List or Array...
                }
                temp[2] = joiner.toString();
                split = temp;
            }
            CommandLine commandLine = cliParser.parse(cliOptions, split);
            if (commandLine.hasOption("help")) {
                cliFormatter.printHelp( "timeline", cliOptions );
            } else if (commandLine.hasOption("getTimeline")) {
                long timeLineId = Long.valueOf(commandLine.getOptionValue("getTimeline"));
                Optional<List<String>> tweets = timeLine.getTweets(timeLineId);
                
                if (tweets.isPresent()) {
                    LOGGER.info("Tweets => {}", tweets.get());
                } else {
                    LOGGER.info("Unable to retrieve tweets with id => {}", timeLineId);
                }
                
            } else if (commandLine.hasOption("tweet")) {
                String tweet = commandLine.getOptionValue("tweet");
                queue.publish(tweet);
            } else if (commandLine.hasOption("getUser")) {
                long userId = Long.valueOf(commandLine.getOptionValue("getUser"));
                Optional<User> user = userCache.getUser(userId);
                
                if (user.isPresent()) {
                    LOGGER.info("Retrieved user => {}", user.get());
                } else {
                    LOGGER.info("Unable to retrieve user with id => {}", userId);
                }
            } else if (commandLine.hasOption("getFollowers")) {
                long userId = Long.valueOf(commandLine.getOptionValue("getFollowers"));
                String userScreeNames = userCache.getFollowers(userId).stream()
                    .map(user -> user.getScreenName())
                    .collect(Collectors.joining(","));
                
                LOGGER.info("Retrieved followers => {}", userScreeNames);
            }
            
        } catch (Exception exp) {
            LOGGER.error(exp.getMessage(), exp);
        }
    }
    
    private void createCommandOptions() {
        Option help = new Option("help", "prints commands");
        cliOptions.addOption(help);
        
        Option getTimeline = Option.builder("getTimeline")
                .hasArg()
                .desc("gets the last max 10 tweets")
                .build();
        cliOptions.addOption(getTimeline);
        
        Option tweet = Option.builder("tweet")
                .hasArg()
                .desc("adds tweet in same format as csv")
                .build();
        cliOptions.addOption(tweet);
        
        Option getUser = Option.builder("getUser")
                .hasArg()
                .desc("gets user info")
                .build();
        cliOptions.addOption(getUser);
        
        Option getFollowers = Option.builder("getFollowers")
                .hasArg()
                .desc("gets follower name for this user")
                .build();
        cliOptions.addOption(getFollowers);
    }
    
}
