package com.jeju.hanrabong.redis;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class PythonScriptRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        runPythonScript();
    }

    private void runPythonScript() {
        // Python 인터프리터와 스크립트의 경로를 설정합니다.
        String pythonInterpreter = "src/script/venv/bin/python";  // Unix 또는 MacOS
        String pythonScriptPath = "src/script/main.py";

        // 스크립트의 절대 경로를 설정합니다.
        File scriptFile = new File(pythonScriptPath);
        if (!scriptFile.exists()) {
            System.err.println("Python script not found: " + pythonScriptPath);
            return;
        }

        // ProcessBuilder를 사용하여 Python 스크립트를 실행합니다.
        ProcessBuilder processBuilder = new ProcessBuilder(pythonInterpreter, pythonScriptPath);
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Python script execution failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}