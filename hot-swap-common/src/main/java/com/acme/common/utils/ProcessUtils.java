package com.acme.common.utils;

import com.acme.common.AnsiLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description TODO
 * @Author wangkai
 * @Date 2023/6/15 21:39
 */
public class ProcessUtils {

    public static List<String> process(String [] cmdToRunWithArgs){
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(cmdToRunWithArgs);
        } catch (SecurityException e) {
            AnsiLog.trace("Couldn't run command {}:", Arrays.toString(cmdToRunWithArgs));
            AnsiLog.trace(e);
            return new ArrayList<String>(0);
        } catch (IOException e) {
            AnsiLog.trace("Couldn't run command {}:", Arrays.toString(cmdToRunWithArgs));
            AnsiLog.trace(e);
            return new ArrayList<String>(0);
        }

        ArrayList<String> sa = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                sa.add(line);
            }
            p.waitFor();
        } catch (IOException e) {
            AnsiLog.trace("Problem reading output from {}:", Arrays.toString(cmdToRunWithArgs));
            AnsiLog.trace(e);
            return new ArrayList<String>(0);
        } catch (InterruptedException ie) {
            AnsiLog.trace("Problem reading output from {}:", Arrays.toString(cmdToRunWithArgs));
            AnsiLog.trace(ie);
            Thread.currentThread().interrupt();
        } finally {
             IOUtils.close(reader);
        }
        return sa;
    }

    public static List<String> jpsCmd(){

        String[] args = {"jps","-l"};
        List<String> result = ProcessUtils.process(args);
        return result;
    }



}
