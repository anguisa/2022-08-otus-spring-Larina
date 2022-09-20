package ru.otus.service;

import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.PrintStream;
import java.util.Scanner;

@Service
public class IOServiceSystem implements IOService {
    
    private final PrintStream output;
    private final Scanner input;

    public IOServiceSystem() {
        output = System.out;
        input = new Scanner(System.in);
    }

    @PreDestroy
    public void close() {
        input.close();
    }

    @Override
    public void outputString(String s){
        output.println(s);
    }

    @Override
    public int readIntWithPrompt(String prompt){
        outputString(prompt);
        return Integer.parseInt(input.nextLine());
    }

    @Override
    public String readStringWithPrompt(String prompt){
        outputString(prompt);
        return input.nextLine();
    }
}
