package com.company;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.*;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        List<String> lines = new ArrayList<>();
        int value=0;
        List<String> results = new ArrayList<>();
        int threadcount=0;
        int linecount;
        int synccount=0;
        List<Path> filep = new ArrayList<>();
        List<String> threads = new ArrayList<>();
        List<Integer> threadlines = new ArrayList<>();
        List<Integer> synclines = new ArrayList<>();
        try(Stream<Path> paths = Files.walk(Paths.get("input"))) {
            paths.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    System.out.println(filePath);
                    filep.add(filePath);
                }
            });
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }

        for(int i = 0; i< filep.size();i++) {
            try {
                FileReader file = new FileReader(filep.get(i).toString());
                BufferedReader br = new BufferedReader(file);
                linecount = 0;
                Pattern p = Pattern.compile(// "\\sThread\\s|" +
                        "\\s[\\w]*\\s?=(\\s)?new( |.*)?Thread(\\(|^\\w)(\\s)?");
                String line;

                while ((line = br.readLine()) != null) {
                    linecount++;
                    if(!line.startsWith("//")) {
                        System.out.println(line);
                        Matcher m = p.matcher(line);

                        while (m.find()) {
                            String ln = line.trim();
                            String[] l = ln.split(" ");
                            for(int q=0;q<l.length;q++){
                                System.out.println(l[q]);
                            }
                            if(l[0].startsWith("RunnableThread")){
                                threads.add(l[1]);
                            }else {
                                threads.add(l[0]);
                            }

                            threadcount++;
                            String tx = (filep.get(i).toString() + ": there was a possible thread found was found at position " +
                                    m.start() + " on line " + linecount + " possible threads found: " + threadcount + '\n');
                            System.out.println(tx);
                            lines.add(tx);
                        }
                    }
                }
                for(int w=0; w < threads.size(); w++) {
                    Pattern threadP = Pattern.compile("\\s"+threads.get(w).toString()+"\\.");
                    file = new FileReader(filep.get(i).toString());
                    br = new BufferedReader(file);
                    try {
                        linecount = 0;
                        while ((line = br.readLine()) != null) {
                            linecount++;
                            if (!line.startsWith("//")) {
                                System.out.println(line);
                                Matcher m = threadP.matcher(line);
                                while (m.find()) {
                                    String tx = "Found the thread at" + linecount;
                                    System.out.println(tx);
                                    threadlines.add(linecount);
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }

                if (threadcount >= 2) {
                    try {
                        Pattern sync = Pattern.compile("\\bsynchronized");
                        file = new FileReader(filep.get(i).toString());
                        br = new BufferedReader(file);
                        linecount = 0;

                        while ((line = br.readLine()) != null) {
                            linecount++;
                            if(!line.startsWith("//")) {
                                System.out.println(line);
                                Matcher m = sync.matcher(line);

                                while (m.find()) {
                                    synccount++;
                                    String tx = (filep.get(i).toString() + ": Synchronized found at position " +
                                            m.start() + " on line " + linecount + " possible synchronizes found: " + synccount + '\n');
                                    System.out.println(tx);
                                    lines.add(tx);
                                    synclines.add(linecount);
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }

            for (int r = 0; r<threadlines.size();r++){
                if (synclines.size()>0) {
                    for (int t = 0; t < synclines.size(); t++) {
                        int dif = threadlines.get(r) - synclines.get(t);
                        if (dif <= 50 && dif >= -50) {
                            value += 2;
                            if (dif <= 20 && dif >= -20) {
                                value += 3;
                                results.add(filep.get(i).toString() + " Low chance at line " + threadlines.get(r));
                            }
                            results.add(filep.get(i).toString() + " Moderate chance at line " + threadlines.get(r));
                        } else {
                            value++;
                            results.add(filep.get(i).toString() + " High chance at line " + threadlines.get(r));
                        }
                    }
                }else{
                    results.add(filep.get(i).toString() + " Extremely high chance at line " + threadlines.get(r));
                }
            }

        }


        System.out.println("Total threads: "+threadcount+"\ntotal synchronizes: "+synccount+"\nin "+filep.size()+" files");
        System.out.println("Total value = "+value +" (higher = less chances overall, lower = higher chances overall)");
        System.out.println("Low/Moderate/High chances of race conditions at:");
        for (int i = 0; i<results.size();i++){
            System.out.println(results.get(i));
        }
    }
}