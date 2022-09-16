package ru.otus.service;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.PrintStream;
import java.util.Scanner;

@Service
public class IOServiceSystem implements IOService {
    private PrintStream output;
    private Scanner input;

    public IOServiceSystem() {
    }

    @PostConstruct
    @Override
    public void init() {
        output = System.out;
        input = new Scanner(System.in);
    }

    @PreDestroy
    @Override
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
