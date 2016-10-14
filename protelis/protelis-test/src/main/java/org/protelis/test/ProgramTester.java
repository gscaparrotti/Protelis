package org.protelis.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.danilopianini.io.FileUtilities;
import org.danilopianini.lang.LangUtils;
import org.protelis.lang.ProtelisLoader;
import org.protelis.vm.ProtelisProgram;
import org.protelis.vm.ProtelisVM;
import org.protelis.vm.impl.SimpleContext;

import java8.util.stream.IntStream;
import java8.util.stream.IntStreams;

public class ProgramTester {


    private static final String SL_NAME = "singleLineComment";
    private static final String ML_NAME = "multilineComment";
    private static final String EXPECTED = "EXPECTED_RESULT:";
    private static final Pattern EXTRACT_RESULT = Pattern.compile(
            ".*?" + EXPECTED + "\\s*(?<" + ML_NAME
            + ">.*?)\\s*\\*\\/|\\/\\/\\s*" + EXPECTED + "\\s*(?<"
            + SL_NAME + ">.*?)\\s*\\n", Pattern.DOTALL);
    private static final Pattern CYCLE = Pattern.compile("\\$CYCLE");
    private static final int MIN_CYCLE_NUM = 1;
    private static final int MAX_CYCLE_NUM = 100;
    
    public static void testFileWithExplicitResult(final String file, final Object expectedResult) {
        testFile(file, 1, expectedResult);
    }

    public static void testFile(final String file) {
        testFile(file, 1);
    }

    public static void testFileWithMultipleRuns(final String file) {
        testFileWithMultipleRuns(file, MIN_CYCLE_NUM, MAX_CYCLE_NUM);
    }

    public static void testFileWithMultipleRuns(final String file, final int min, final int max) {
        testFileWithMultipleRuns(file, IntStreams.rangeClosed(min, max));
    }

    public static void testFileWithMultipleRuns(final String file, final IntStream stream) {
        stream.forEach(i -> {
            testFile(file, i);
        });
    }

    public static void testFile(final String file, final int runs) {
        final Object execResult = runProgram(file, runs);
        final InputStream is = ProgramTester.class.getResourceAsStream(file);
        try {
            final String test = IOUtils.toString(is, StandardCharsets.UTF_8);
            final Matcher extractor = EXTRACT_RESULT.matcher(test);
            if (extractor.find()) {
                String result = extractor.group(ML_NAME);
                if (result == null) {
                    result = extractor.group(SL_NAME);
                }
                final String toCheck = CYCLE.matcher(result).replaceAll(Integer.toString(runs));
                final ProtelisVM vm = new ProtelisVM(ProtelisLoader.parse(toCheck), new SimpleContext());
                vm.runCycle();
                assertEquals(vm.getCurrentValue(), execResult instanceof Number
                        ? ((Number) execResult).doubleValue()
                        : execResult);
            } else {
                fail("Your test does not include the expected result");
            }
        } catch (IOException e) {
            fail(LangUtils.stackTraceToString(e));
        }
    }

    public static void testFile(final String file, final int runs, final Object expectedResult) {
        assertEquals(expectedResult, runProgram(file, runs));
    }

    public static Object runProgram(final String s, final int runs) {
        final ProtelisProgram program = ProtelisLoader.parse(s);
        try {
            FileUtilities.serializeObject(program);
        } catch (Exception e) {
            fail();
        }
        final ProtelisVM vm = new ProtelisVM(program, new SimpleContext());
        for (int i = 0; i < runs; i++) {
            vm.runCycle();
        }
        return vm.getCurrentValue();
    }
}
