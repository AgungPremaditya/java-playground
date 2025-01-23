package com.schias.playground;

import java.util.Arrays;

public class Main {
    private final String grade;
    private final String workLocation;

    private final Double present;
    private final Double salary;


    public Main(){
        this.grade = "G1";
        this.present = 18.0;
        this.salary = 6000000.0;
        this.workLocation = "Office";
    }

    public static void main(String[] args) {
//        String input = "PRESENT * IF (GRADE == 'G1' OR GRADE == 'G2', 20000, 30000)";
//        String input = "PRESENT * IF (GRADE == 'G1' OR GRADE == 'G2', 15000, 20000)";
//        String input = "SALARY * IF (WORKLOCATION == 'Office', 0.24 , 1.24 ) / 100";
//        String input = "PRESENT * SALARY";
        String input = "SALARY * 0.3 / 100";
        Double result = processFormula(input);

        System.out.println(Math.ceil(result));
    }

    public static Double processFormula(String formula){

        Main instance = new Main();

        // Check is there arithmetic operation
        Boolean isThereArithmeticOperation = checkIsThereArithmeticOperation(formula);

        Boolean isThereConditionalOperation = formula.contains("IF");

        Double result = 0.0;

        if (isThereArithmeticOperation) {
            // Split into condition and value
            result = doArithmeticOperation(formula);
        }else if (isThereConditionalOperation) {
            // Split into condition and value
            result = conditionalOperation(formula);
        }else{
            result = replaceEntities(formula);
        }

        return result;
    }


    /**
     * ARITHMETIC OPERATION
     * */
    public static Double replaceEntities(String fieldName){
        Main instance = new Main();

        return switch (fieldName.toUpperCase()) {
            case "PRESENT" -> instance.present;
            case "SALARY" -> instance.salary;
            default -> 0.0;
        };
    }

    public static Boolean checkIsThereArithmeticOperation(String formula) {
        // Define common arithmetic operators
        String[] operators = {"+", "-", "*", "/"};

        // Check if any conditionalOperator exists in the formula
        return Arrays.stream(operators)
                .anyMatch(formula::contains);
    }

    public static String[] getArithmeticOperation(String formula) {
        // Define arithmetic operators
        String[] operators = {" \\+ ", " - ", " \\* ", " / "};

        // Find which conditionalOperator exists in the formula
        String foundOperator = Arrays.stream(operators)
                .filter(op -> formula.contains(op.replace("\\", "")))
                .findFirst()
                .orElse(null);

        if (foundOperator != null) {
            // Split by the found conditionalOperator
            return formula.split(foundOperator);
        }
        return new String[]{ formula };
    }

    public static String getOperator(String formula) {
        String[] operators = {"+", "-", "*", "/"};

        return Arrays.stream(operators)
                .filter(formula::contains)
                .findFirst()
                .orElse("");
    }

    public static Double doArithmeticOperation(String formula){
        String[] parts = getArithmeticOperation(formula);

        String operator = getOperator(formula);

        Double rightPart = checkIsStringNumber(parts[0]) ? Double.parseDouble(parts[0]) : processFormula(parts[0]);
        Double leftPart = checkIsStringNumber(parts[1]) ? Double.parseDouble(parts[1]) : processFormula(parts[1]);

        Double result = 0.0;
        switch (operator) {
            case "+" -> result = rightPart + leftPart;
            case "*" -> result = rightPart * leftPart;
            case "-" -> result = rightPart - leftPart;
            case "/" -> result = rightPart / leftPart;
            default -> result = 0.0;
        }

        return result;
    }

    public static Boolean checkIsStringNumber(String value){
        return value.matches("\\d+") ? true : value.matches("-?\\d+(\\.\\d+)?");
    }



    /**
     * CONDITIONAL OPERATION
     * */
    public static Double conditionalOperation(String input) {
        Main instance = new Main();
        String[] parts = getPart(input);
        String[] conditions = getCondition(parts[0]);

        // Evaluate conditions
        boolean result = evaluateConditions(conditions, instance);

        // Return appropriate value based on condition evaluation
        return Double.parseDouble(result ? parts[1] : parts[2]);
    }

    private static boolean evaluateConditions(String[] conditions, Main instance) {
        boolean firstConditionResult = evaluateSingleCondition(conditions[0], instance);

        // If first condition is true or there's only one condition, return the result
        if (firstConditionResult || conditions.length == 1) {
            return firstConditionResult;
        }

        // Evaluate second condition if first one is false
        return evaluateSingleCondition(conditions[1], instance);
    }

    private static boolean evaluateSingleCondition(String condition, Main instance) {
        if (condition.contains(instance.workLocation)) {
            return conditionalOperator(condition, instance.workLocation);
        }
        if (condition.contains(instance.grade)) {
            return conditionalOperator(condition, instance.grade);
        }
        return false;
    }

    public static boolean conditionalOperator(String condition, String value) {
        if (condition.contains("==")) {
            String[] components = condition.split("==");
            return cleanString(components[1]).equals(value);
        }else if (condition.contains("!=")) {
            String[] components = condition.split("!=");
            return !cleanString(components[1]).equals(value);
        }

        return false;
    }

    public static String[] getPart(String condition) {
        // Remove the 'IF' and parentheses
        String cleaned = condition.substring(4, condition.length() - 1).trim();

        return cleaned.split(",");
    }

    public static String[] getCondition(String part) {
        if (part.contains("OR")){
            return part.split("OR");
        } else if (part.contains("AND")) {
            return part.split("AND");
        }else {
            return new String[]{part};
        }
    }

    public static String cleanString(String input) {
        return input.substring(1, input.length() - 1).replace("'", "");
    }
}