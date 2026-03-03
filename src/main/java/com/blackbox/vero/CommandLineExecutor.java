package com.blackbox.vero;

import java.util.Arrays;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.blackbox.vero.rule_engine.engine.context.EvaluationContext;
import com.blackbox.vero.rule_engine.engine.evaluator.RuleEvaluator;
import com.blackbox.vero.rule_engine.entity.Rule;
import com.blackbox.vero.rule_engine.entity.RuleCondition;
import com.blackbox.vero.rule_engine.entity.RuleNode;
import com.blackbox.vero.rule_engine.enums.LogicalOperationType;
import com.blackbox.vero.rule_engine.enums.OperationType;
import com.blackbox.vero.rule_engine.enums.ValueType;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommandLineExecutor implements CommandLineRunner {

    private final RuleEvaluator ruleEvaluator;

    private final Logger logger = LoggerFactory.getLogger(CommandLineExecutor.class);

    @Override
    public void run(String... args) {
        try {
            logger.info("=".repeat(60));
            logger.info("VERO RULE ENGINE - DEMONSTRATION");
            logger.info("=".repeat(60));

            demonstrateSimpleAndRule();
            demonstrateOrRule();
            demonstrateNotRule();
            demonstrateNestedRuleWithNot();
            demonstrateMultiFieldRule();
            demonstrateShortCircuitEvaluation();

            logger.info("\n" + "=".repeat(60));
            logger.info("ADVANCED DEMONSTRATIONS");
            logger.info("=".repeat(60));

            demonstrateDeepNestedTree();
            demonstrateDeMorgansLaw();
            demonstrateLoanApprovalRule();
            demonstrateFraudDetectionRule();
            demonstrateAccessControlRule();
            demonstratePromotionEligibilityRule();

            logger.info("\n" + "=".repeat(60));
            logger.info("ALL DEMONSTRATIONS COMPLETED SUCCESSFULLY");
            logger.info("=".repeat(60));

        } catch (Exception ex) {
            logger.error("Demo execution failed", ex);
        }
    }

    /**
     * EXAMPLE 1: Simple AND Rule
     * Rule: age > 18 AND age < 60
     * 
     * Tree Structure:
     *        AND
     *       /   \
     *   age>18  age<60
     */
    private void demonstrateSimpleAndRule() {
        logger.info("\n" + "-".repeat(50));
        logger.info("EXAMPLE 1: Simple AND Rule (age > 18 AND age < 60)");
        logger.info("-".repeat(50));

        Rule rule = Rule.builder()
                .name("Age Range Rule")
                .ruleSlug("age-range")
                .description("Check if age is between 18 and 60")
                .build();

        RuleNode andNode = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .rule(rule)
                .build();

        RuleCondition ageGreaterThan18 = RuleCondition.builder()
                .fieldName("age")
                .valueType(ValueType.INTEGER)
                .operationType(OperationType.GREATER_THAN)
                .expectedValue("18")
                .ruleNode(andNode)
                .build();

        RuleCondition ageLessThan60 = RuleCondition.builder()
                .fieldName("age")
                .valueType(ValueType.INTEGER)
                .operationType(OperationType.LESS_THAN)
                .expectedValue("60")
                .ruleNode(andNode)
                .build();

        andNode.setRuleConditions(Arrays.asList(ageGreaterThan18, ageLessThan60));
        rule.setRuleNodes(Arrays.asList(andNode));

        EvaluationContext ctx1 = EvaluationContext.of("age", "25");
        EvaluationContext ctx2 = EvaluationContext.of("age", "15");
        EvaluationContext ctx3 = EvaluationContext.of("age", "65");

        logger.info("Testing age=25: {} (expected: true)", ruleEvaluator.evaluate(rule, ctx1));
        logger.info("Testing age=15: {} (expected: false - fails age>18)", ruleEvaluator.evaluate(rule, ctx2));
        logger.info("Testing age=65: {} (expected: false - fails age<60)", ruleEvaluator.evaluate(rule, ctx3));
    }

    /**
     * EXAMPLE 2: OR Rule
     * Rule: salary > 50000 OR department = "Engineering"
     * 
     * Tree Structure:
     *           OR
     *          /  \
     *  salary>50k  dept=Eng
     */
    private void demonstrateOrRule() {
        logger.info("\n" + "-".repeat(50));
        logger.info("EXAMPLE 2: OR Rule (salary > 50000 OR dept = Engineering)");
        logger.info("-".repeat(50));

        Rule rule = Rule.builder()
                .name("Eligibility Rule")
                .ruleSlug("eligibility")
                .build();

        RuleNode orNode = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.OR)
                .rule(rule)
                .build();

        RuleCondition highSalary = RuleCondition.builder()
                .fieldName("salary")
                .valueType(ValueType.INTEGER)
                .operationType(OperationType.GREATER_THAN)
                .expectedValue("50000")
                .ruleNode(orNode)
                .build();

        RuleCondition engineeringDept = RuleCondition.builder()
                .fieldName("department")
                .valueType(ValueType.STRING)
                .operationType(OperationType.EQUALS)
                .expectedValue("Engineering")
                .ruleNode(orNode)
                .build();

        orNode.setRuleConditions(Arrays.asList(highSalary, engineeringDept));
        rule.setRuleNodes(Arrays.asList(orNode));

        EvaluationContext ctx1 = EvaluationContext.of(Map.of("salary", "60000", "department", "Sales"));
        EvaluationContext ctx2 = EvaluationContext.of(Map.of("salary", "30000", "department", "Engineering"));
        EvaluationContext ctx3 = EvaluationContext.of(Map.of("salary", "30000", "department", "Sales"));

        logger.info("Testing salary=60000, dept=Sales: {} (expected: true - high salary)", ruleEvaluator.evaluate(rule, ctx1));
        logger.info("Testing salary=30000, dept=Engineering: {} (expected: true - engineering)", ruleEvaluator.evaluate(rule, ctx2));
        logger.info("Testing salary=30000, dept=Sales: {} (expected: false)", ruleEvaluator.evaluate(rule, ctx3));
    }

    /**
     * EXAMPLE 3: NOT Rule
     * Rule: NOT(age < 21) → equivalent to age >= 21
     * 
     * Tree Structure:
     *       NOT
     *        |
     *     age<21
     */
    private void demonstrateNotRule() {
        logger.info("\n" + "-".repeat(50));
        logger.info("EXAMPLE 3: NOT Rule - NOT(age < 21)");
        logger.info("-".repeat(50));

        Rule rule = Rule.builder()
                .name("Adult Only Rule")
                .ruleSlug("adult-only")
                .build();

        RuleNode notNode = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.NOT)
                .rule(rule)
                .build();

        RuleCondition underAge = RuleCondition.builder()
                .fieldName("age")
                .valueType(ValueType.INTEGER)
                .operationType(OperationType.LESS_THAN)
                .expectedValue("21")
                .ruleNode(notNode)
                .build();

        notNode.setRuleConditions(Arrays.asList(underAge));
        rule.setRuleNodes(Arrays.asList(notNode));

        EvaluationContext ctx1 = EvaluationContext.of("age", "25");
        EvaluationContext ctx2 = EvaluationContext.of("age", "18");

        logger.info("Testing age=25: {} (expected: true - NOT(25<21) = NOT(false) = true)", ruleEvaluator.evaluate(rule, ctx1));
        logger.info("Testing age=18: {} (expected: false - NOT(18<21) = NOT(true) = false)", ruleEvaluator.evaluate(rule, ctx2));
    }

    /**
     * EXAMPLE 4: Nested Rule with NOT
     * Rule: NOT(age < 18 OR status = "inactive")
     * Equivalent to: age >= 18 AND status != "inactive"
     * 
     * Tree Structure:
     *        NOT
     *         |
     *        OR
     *       /  \
     *   age<18  status=inactive
     */
    private void demonstrateNestedRuleWithNot() {
        logger.info("\n" + "-".repeat(50));
        logger.info("EXAMPLE 4: Nested NOT(OR) - NOT(age<18 OR status=inactive)");
        logger.info("-".repeat(50));

        Rule rule = Rule.builder()
                .name("Active Adult Rule")
                .ruleSlug("active-adult")
                .build();

        RuleNode notNode = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.NOT)
                .rule(rule)
                .build();

        RuleNode orNode = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.OR)
                .parentRuleNode(notNode)
                .build();

        RuleCondition underAge = RuleCondition.builder()
                .fieldName("age")
                .valueType(ValueType.INTEGER)
                .operationType(OperationType.LESS_THAN)
                .expectedValue("18")
                .ruleNode(orNode)
                .build();

        RuleCondition inactiveStatus = RuleCondition.builder()
                .fieldName("status")
                .valueType(ValueType.STRING)
                .operationType(OperationType.EQUALS)
                .expectedValue("inactive")
                .ruleNode(orNode)
                .build();

        orNode.setRuleConditions(Arrays.asList(underAge, inactiveStatus));
        notNode.setChildRuleNodes(Arrays.asList(orNode));
        rule.setRuleNodes(Arrays.asList(notNode));

        EvaluationContext ctx1 = EvaluationContext.of(Map.of("age", "25", "status", "active"));
        EvaluationContext ctx2 = EvaluationContext.of(Map.of("age", "15", "status", "active"));
        EvaluationContext ctx3 = EvaluationContext.of(Map.of("age", "25", "status", "inactive"));

        logger.info("Testing age=25, status=active: {} (expected: true)", ruleEvaluator.evaluate(rule, ctx1));
        logger.info("Testing age=15, status=active: {} (expected: false - underage)", ruleEvaluator.evaluate(rule, ctx2));
        logger.info("Testing age=25, status=inactive: {} (expected: false - inactive)", ruleEvaluator.evaluate(rule, ctx3));
    }

    /**
     * EXAMPLE 5: Complex Multi-Field Rule
     * Rule: (age > 18 AND country = "India") AND (salary > 30000 OR experience > 3)
     * 
     * Tree Structure:
     *                 AND (root)
     *                /         \
     *             AND           OR
     *            /   \         /   \
     *       age>18  country  salary  exp>3
     */
    private void demonstrateMultiFieldRule() {
        logger.info("\n" + "-".repeat(50));
        logger.info("EXAMPLE 5: Complex Multi-Field Rule");
        logger.info("-".repeat(50));

        Rule rule = Rule.builder()
                .name("Job Eligibility Rule")
                .ruleSlug("job-eligibility")
                .build();

        RuleNode rootAnd = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .rule(rule)
                .build();

        RuleNode leftAnd = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .parentRuleNode(rootAnd)
                .build();

        RuleCondition ageCondition = RuleCondition.builder()
                .fieldName("age")
                .valueType(ValueType.INTEGER)
                .operationType(OperationType.GREATER_THAN)
                .expectedValue("18")
                .ruleNode(leftAnd)
                .build();

        RuleCondition countryCondition = RuleCondition.builder()
                .fieldName("country")
                .valueType(ValueType.STRING)
                .operationType(OperationType.EQUALS)
                .expectedValue("India")
                .ruleNode(leftAnd)
                .build();

        leftAnd.setRuleConditions(Arrays.asList(ageCondition, countryCondition));

        RuleNode rightOr = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.OR)
                .parentRuleNode(rootAnd)
                .build();

        RuleCondition salaryCondition = RuleCondition.builder()
                .fieldName("salary")
                .valueType(ValueType.INTEGER)
                .operationType(OperationType.GREATER_THAN)
                .expectedValue("30000")
                .ruleNode(rightOr)
                .build();

        RuleCondition experienceCondition = RuleCondition.builder()
                .fieldName("experience")
                .valueType(ValueType.INTEGER)
                .operationType(OperationType.GREATER_THAN)
                .expectedValue("3")
                .ruleNode(rightOr)
                .build();

        rightOr.setRuleConditions(Arrays.asList(salaryCondition, experienceCondition));

        rootAnd.setChildRuleNodes(Arrays.asList(leftAnd, rightOr));
        rule.setRuleNodes(Arrays.asList(rootAnd));

        EvaluationContext eligible = EvaluationContext.of(Map.of(
                "age", "25",
                "country", "India",
                "salary", "35000",
                "experience", "2"
        ));

        EvaluationContext notEligibleAge = EvaluationContext.of(Map.of(
                "age", "16",
                "country", "India",
                "salary", "50000",
                "experience", "5"
        ));

        EvaluationContext notEligibleSalaryExp = EvaluationContext.of(Map.of(
                "age", "25",
                "country", "India",
                "salary", "20000",
                "experience", "1"
        ));

        logger.info("Testing eligible candidate: {} (expected: true)", ruleEvaluator.evaluate(rule, eligible));
        logger.info("Testing underage candidate: {} (expected: false)", ruleEvaluator.evaluate(rule, notEligibleAge));
        logger.info("Testing low salary & exp: {} (expected: false)", ruleEvaluator.evaluate(rule, notEligibleSalaryExp));
    }

    /**
     * EXAMPLE 6: Demonstrating Short-Circuit Evaluation
     * With AND: stops at first false
     * With OR: stops at first true
     */
    private void demonstrateShortCircuitEvaluation() {
        logger.info("\n" + "-".repeat(50));
        logger.info("EXAMPLE 6: Short-Circuit Evaluation Demo");
        logger.info("-".repeat(50));

        Rule andRule = Rule.builder()
                .name("AND Short-Circuit")
                .ruleSlug("and-short-circuit")
                .build();

        RuleNode andNode = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .rule(andRule)
                .build();

        RuleCondition cond1 = RuleCondition.builder()
                .fieldName("field1")
                .valueType(ValueType.STRING)
                .operationType(OperationType.EQUALS)
                .expectedValue("fail")
                .ruleNode(andNode)
                .build();

        RuleCondition cond2 = RuleCondition.builder()
                .fieldName("field2")
                .valueType(ValueType.STRING)
                .operationType(OperationType.EQUALS)
                .expectedValue("value2")
                .ruleNode(andNode)
                .build();

        andNode.setRuleConditions(Arrays.asList(cond1, cond2));
        andRule.setRuleNodes(Arrays.asList(andNode));

        EvaluationContext ctx = EvaluationContext.of(Map.of(
                "field1", "pass",
                "field2", "value2"
        ));

        logger.info("AND rule with first condition false:");
        logger.info("  - field1='pass' != 'fail' → false");
        logger.info("  - field2 is NOT evaluated (short-circuit!)");
        logger.info("  Result: {} (expected: false)", ruleEvaluator.evaluate(andRule, ctx));

        Rule orRule = Rule.builder()
                .name("OR Short-Circuit")
                .ruleSlug("or-short-circuit")
                .build();

        RuleNode orNode = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.OR)
                .rule(orRule)
                .build();

        RuleCondition orCond1 = RuleCondition.builder()
                .fieldName("field1")
                .valueType(ValueType.STRING)
                .operationType(OperationType.EQUALS)
                .expectedValue("pass")
                .ruleNode(orNode)
                .build();

        RuleCondition orCond2 = RuleCondition.builder()
                .fieldName("field2")
                .valueType(ValueType.STRING)
                .operationType(OperationType.EQUALS)
                .expectedValue("never_checked")
                .ruleNode(orNode)
                .build();

        orNode.setRuleConditions(Arrays.asList(orCond1, orCond2));
        orRule.setRuleNodes(Arrays.asList(orNode));

        logger.info("\nOR rule with first condition true:");
        logger.info("  - field1='pass' == 'pass' → true");
        logger.info("  - field2 is NOT evaluated (short-circuit!)");
        logger.info("  Result: {} (expected: true)", ruleEvaluator.evaluate(orRule, ctx));
    }

    /**
     * EXAMPLE 7: Deep Nested Tree (4 levels)
     * Rule: ((A AND B) OR (C AND D)) AND NOT(E OR F)
     * 
     * Tree Structure:
     *                        AND (root)
     *                       /          \
     *                     OR           NOT
     *                    /  \            |
     *                 AND    AND        OR
     *                / \    /  \       /  \
     *               A   B  C    D     E    F
     * 
     * Where:
     *   A = age > 21
     *   B = income > 50000
     *   C = creditScore > 700
     *   D = yearsEmployed > 2
     *   E = hasDefaulted = true
     *   F = isBankrupt = true
     */
    private void demonstrateDeepNestedTree() {
        logger.info("\n" + "-".repeat(50));
        logger.info("EXAMPLE 7: Deep Nested Tree (4 levels)");
        logger.info("((age>21 AND income>50k) OR (credit>700 AND employed>2)) AND NOT(defaulted OR bankrupt)");
        logger.info("-".repeat(50));

        Rule rule = Rule.builder()
                .name("Complex Loan Eligibility")
                .ruleSlug("complex-loan")
                .build();

        // Root AND
        RuleNode rootAnd = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .rule(rule)
                .build();

        // Left OR branch
        RuleNode leftOr = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.OR)
                .parentRuleNode(rootAnd)
                .build();

        // Left-Left AND (A AND B)
        RuleNode leftLeftAnd = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .parentRuleNode(leftOr)
                .build();

        RuleCondition condA = RuleCondition.builder()
                .fieldName("age")
                .valueType(ValueType.INTEGER)
                .operationType(OperationType.GREATER_THAN)
                .expectedValue("21")
                .ruleNode(leftLeftAnd)
                .build();

        RuleCondition condB = RuleCondition.builder()
                .fieldName("income")
                .valueType(ValueType.INTEGER)
                .operationType(OperationType.GREATER_THAN)
                .expectedValue("50000")
                .ruleNode(leftLeftAnd)
                .build();

        leftLeftAnd.setRuleConditions(Arrays.asList(condA, condB));

        // Left-Right AND (C AND D)
        RuleNode leftRightAnd = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .parentRuleNode(leftOr)
                .build();

        RuleCondition condC = RuleCondition.builder()
                .fieldName("creditScore")
                .valueType(ValueType.INTEGER)
                .operationType(OperationType.GREATER_THAN)
                .expectedValue("700")
                .ruleNode(leftRightAnd)
                .build();

        RuleCondition condD = RuleCondition.builder()
                .fieldName("yearsEmployed")
                .valueType(ValueType.INTEGER)
                .operationType(OperationType.GREATER_THAN)
                .expectedValue("2")
                .ruleNode(leftRightAnd)
                .build();

        leftRightAnd.setRuleConditions(Arrays.asList(condC, condD));

        leftOr.setChildRuleNodes(Arrays.asList(leftLeftAnd, leftRightAnd));

        // Right NOT branch
        RuleNode rightNot = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.NOT)
                .parentRuleNode(rootAnd)
                .build();

        // NOT's child OR (E OR F)
        RuleNode notChildOr = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.OR)
                .parentRuleNode(rightNot)
                .build();

        RuleCondition condE = RuleCondition.builder()
                .fieldName("hasDefaulted")
                .valueType(ValueType.BOOLEAN)
                .operationType(OperationType.EQUALS)
                .expectedValue("true")
                .ruleNode(notChildOr)
                .build();

        RuleCondition condF = RuleCondition.builder()
                .fieldName("isBankrupt")
                .valueType(ValueType.BOOLEAN)
                .operationType(OperationType.EQUALS)
                .expectedValue("true")
                .ruleNode(notChildOr)
                .build();

        notChildOr.setRuleConditions(Arrays.asList(condE, condF));
        rightNot.setChildRuleNodes(Arrays.asList(notChildOr));

        rootAnd.setChildRuleNodes(Arrays.asList(leftOr, rightNot));
        rule.setRuleNodes(Arrays.asList(rootAnd));

        // Test cases
        EvaluationContext goodCandidate1 = EvaluationContext.of(Map.of(
                "age", "30",
                "income", "75000",
                "creditScore", "650",
                "yearsEmployed", "1",
                "hasDefaulted", "false",
                "isBankrupt", "false"
        ));

        EvaluationContext goodCandidate2 = EvaluationContext.of(Map.of(
                "age", "19",
                "income", "30000",
                "creditScore", "750",
                "yearsEmployed", "5",
                "hasDefaulted", "false",
                "isBankrupt", "false"
        ));

        EvaluationContext badCandidateDefaulted = EvaluationContext.of(Map.of(
                "age", "30",
                "income", "100000",
                "creditScore", "800",
                "yearsEmployed", "10",
                "hasDefaulted", "true",
                "isBankrupt", "false"
        ));

        EvaluationContext badCandidateNoQualify = EvaluationContext.of(Map.of(
                "age", "19",
                "income", "30000",
                "creditScore", "600",
                "yearsEmployed", "1",
                "hasDefaulted", "false",
                "isBankrupt", "false"
        ));

        logger.info("Good candidate (age+income path): {} (expected: true)", 
                ruleEvaluator.evaluate(rule, goodCandidate1));
        logger.info("Good candidate (credit+employed path): {} (expected: true)", 
                ruleEvaluator.evaluate(rule, goodCandidate2));
        logger.info("Bad candidate (has defaulted): {} (expected: false)", 
                ruleEvaluator.evaluate(rule, badCandidateDefaulted));
        logger.info("Bad candidate (no qualifying path): {} (expected: false)", 
                ruleEvaluator.evaluate(rule, badCandidateNoQualify));
    }

    /**
     * EXAMPLE 8: De Morgan's Law Demonstration
     * Shows equivalence: NOT(A AND B) ≡ NOT(A) OR NOT(B)
     * 
     * Rule 1: NOT(premium = true AND age < 25)
     * Rule 2: premium != true OR age >= 25
     */
    private void demonstrateDeMorgansLaw() {
        logger.info("\n" + "-".repeat(50));
        logger.info("EXAMPLE 8: De Morgan's Law - NOT(A AND B) ≡ NOT(A) OR NOT(B)");
        logger.info("-".repeat(50));

        // Rule 1: NOT(premium AND age<25)
        Rule rule1 = Rule.builder()
                .name("De Morgan Rule 1")
                .ruleSlug("demorgan-1")
                .build();

        RuleNode notNode = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.NOT)
                .rule(rule1)
                .build();

        RuleNode innerAnd = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .parentRuleNode(notNode)
                .build();

        RuleCondition premiumTrue = RuleCondition.builder()
                .fieldName("premium")
                .valueType(ValueType.BOOLEAN)
                .operationType(OperationType.EQUALS)
                .expectedValue("true")
                .ruleNode(innerAnd)
                .build();

        RuleCondition ageUnder25 = RuleCondition.builder()
                .fieldName("age")
                .valueType(ValueType.INTEGER)
                .operationType(OperationType.LESS_THAN)
                .expectedValue("25")
                .ruleNode(innerAnd)
                .build();

        innerAnd.setRuleConditions(Arrays.asList(premiumTrue, ageUnder25));
        notNode.setChildRuleNodes(Arrays.asList(innerAnd));
        rule1.setRuleNodes(Arrays.asList(notNode));

        // Rule 2: NOT(premium) OR NOT(age<25) - equivalent using OR
        Rule rule2 = Rule.builder()
                .name("De Morgan Rule 2")
                .ruleSlug("demorgan-2")
                .build();

        RuleNode orNode = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.OR)
                .rule(rule2)
                .build();

        RuleNode notPremium = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.NOT)
                .parentRuleNode(orNode)
                .build();

        RuleCondition premiumCheck = RuleCondition.builder()
                .fieldName("premium")
                .valueType(ValueType.BOOLEAN)
                .operationType(OperationType.EQUALS)
                .expectedValue("true")
                .ruleNode(notPremium)
                .build();

        notPremium.setRuleConditions(Arrays.asList(premiumCheck));

        RuleNode notAge = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.NOT)
                .parentRuleNode(orNode)
                .build();

        RuleCondition ageCheck = RuleCondition.builder()
                .fieldName("age")
                .valueType(ValueType.INTEGER)
                .operationType(OperationType.LESS_THAN)
                .expectedValue("25")
                .ruleNode(notAge)
                .build();

        notAge.setRuleConditions(Arrays.asList(ageCheck));

        orNode.setChildRuleNodes(Arrays.asList(notPremium, notAge));
        rule2.setRuleNodes(Arrays.asList(orNode));

        // Test with various inputs - both rules should give same results
        EvaluationContext ctx1 = EvaluationContext.of(Map.of("premium", "true", "age", "20"));
        EvaluationContext ctx2 = EvaluationContext.of(Map.of("premium", "true", "age", "30"));
        EvaluationContext ctx3 = EvaluationContext.of(Map.of("premium", "false", "age", "20"));
        EvaluationContext ctx4 = EvaluationContext.of(Map.of("premium", "false", "age", "30"));

        logger.info("Testing De Morgan's equivalence:");
        logger.info("premium=true, age=20:  Rule1={}, Rule2={} (both false)", 
                ruleEvaluator.evaluate(rule1, ctx1), ruleEvaluator.evaluate(rule2, ctx1));
        logger.info("premium=true, age=30:  Rule1={}, Rule2={} (both true)", 
                ruleEvaluator.evaluate(rule1, ctx2), ruleEvaluator.evaluate(rule2, ctx2));
        logger.info("premium=false, age=20: Rule1={}, Rule2={} (both true)", 
                ruleEvaluator.evaluate(rule1, ctx3), ruleEvaluator.evaluate(rule2, ctx3));
        logger.info("premium=false, age=30: Rule1={}, Rule2={} (both true)", 
                ruleEvaluator.evaluate(rule1, ctx4), ruleEvaluator.evaluate(rule2, ctx4));
    }

    /**
     * EXAMPLE 9: Real-World Loan Approval Rule
     * 
     * Rule: Approve loan if:
     *   (creditScore >= 700 AND debtToIncome < 40) 
     *   OR 
     *   (creditScore >= 650 AND debtToIncome < 30 AND hasCollateral = true)
     *   AND
     *   NOT(hasActiveBankruptcy = true OR monthsEmployed < 6)
     * 
     * Tree:
     *                           AND
     *                          /   \
     *                        OR    NOT
     *                       /  \     |
     *                    AND   AND   OR
     *                   / \   /|\   / \
     *                 c1 c2 c3c4c5 c6 c7
     */
    private void demonstrateLoanApprovalRule() {
        logger.info("\n" + "-".repeat(50));
        logger.info("EXAMPLE 9: Real-World Loan Approval Rule");
        logger.info("-".repeat(50));

        Rule rule = Rule.builder()
                .name("Loan Approval")
                .ruleSlug("loan-approval")
                .build();

        // Root AND
        RuleNode rootAnd = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .rule(rule)
                .build();

        // Left OR (qualification paths)
        RuleNode qualificationOr = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.OR)
                .parentRuleNode(rootAnd)
                .build();

        // Path 1: High credit score path
        RuleNode highCreditPath = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .parentRuleNode(qualificationOr)
                .build();

        highCreditPath.setRuleConditions(Arrays.asList(
                RuleCondition.builder()
                        .fieldName("creditScore")
                        .valueType(ValueType.INTEGER)
                        .operationType(OperationType.GREATER_THAN_OR_EQUALS)
                        .expectedValue("700")
                        .ruleNode(highCreditPath)
                        .build(),
                RuleCondition.builder()
                        .fieldName("debtToIncome")
                        .valueType(ValueType.INTEGER)
                        .operationType(OperationType.LESS_THAN)
                        .expectedValue("40")
                        .ruleNode(highCreditPath)
                        .build()
        ));

        // Path 2: Medium credit with collateral
        RuleNode collateralPath = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .parentRuleNode(qualificationOr)
                .build();

        collateralPath.setRuleConditions(Arrays.asList(
                RuleCondition.builder()
                        .fieldName("creditScore")
                        .valueType(ValueType.INTEGER)
                        .operationType(OperationType.GREATER_THAN_OR_EQUALS)
                        .expectedValue("650")
                        .ruleNode(collateralPath)
                        .build(),
                RuleCondition.builder()
                        .fieldName("debtToIncome")
                        .valueType(ValueType.INTEGER)
                        .operationType(OperationType.LESS_THAN)
                        .expectedValue("30")
                        .ruleNode(collateralPath)
                        .build(),
                RuleCondition.builder()
                        .fieldName("hasCollateral")
                        .valueType(ValueType.BOOLEAN)
                        .operationType(OperationType.EQUALS)
                        .expectedValue("true")
                        .ruleNode(collateralPath)
                        .build()
        ));

        qualificationOr.setChildRuleNodes(Arrays.asList(highCreditPath, collateralPath));

        // Right NOT (disqualifiers)
        RuleNode disqualifierNot = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.NOT)
                .parentRuleNode(rootAnd)
                .build();

        RuleNode disqualifierOr = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.OR)
                .parentRuleNode(disqualifierNot)
                .build();

        disqualifierOr.setRuleConditions(Arrays.asList(
                RuleCondition.builder()
                        .fieldName("hasActiveBankruptcy")
                        .valueType(ValueType.BOOLEAN)
                        .operationType(OperationType.EQUALS)
                        .expectedValue("true")
                        .ruleNode(disqualifierOr)
                        .build(),
                RuleCondition.builder()
                        .fieldName("monthsEmployed")
                        .valueType(ValueType.INTEGER)
                        .operationType(OperationType.LESS_THAN)
                        .expectedValue("6")
                        .ruleNode(disqualifierOr)
                        .build()
        ));

        disqualifierNot.setChildRuleNodes(Arrays.asList(disqualifierOr));
        rootAnd.setChildRuleNodes(Arrays.asList(qualificationOr, disqualifierNot));
        rule.setRuleNodes(Arrays.asList(rootAnd));

        // Test cases
        EvaluationContext approved1 = EvaluationContext.of(Map.of(
                "creditScore", "750", "debtToIncome", "35", "hasCollateral", "false",
                "hasActiveBankruptcy", "false", "monthsEmployed", "24"
        ));

        EvaluationContext approved2 = EvaluationContext.of(Map.of(
                "creditScore", "680", "debtToIncome", "25", "hasCollateral", "true",
                "hasActiveBankruptcy", "false", "monthsEmployed", "12"
        ));

        EvaluationContext deniedBankruptcy = EvaluationContext.of(Map.of(
                "creditScore", "800", "debtToIncome", "20", "hasCollateral", "true",
                "hasActiveBankruptcy", "true", "monthsEmployed", "60"
        ));

        EvaluationContext deniedNewJob = EvaluationContext.of(Map.of(
                "creditScore", "750", "debtToIncome", "30", "hasCollateral", "true",
                "hasActiveBankruptcy", "false", "monthsEmployed", "3"
        ));

        EvaluationContext deniedNoPath = EvaluationContext.of(Map.of(
                "creditScore", "620", "debtToIncome", "45", "hasCollateral", "false",
                "hasActiveBankruptcy", "false", "monthsEmployed", "24"
        ));

        logger.info("High credit, low DTI: {} (expected: APPROVED)", ruleEvaluator.evaluate(rule, approved1));
        logger.info("Medium credit with collateral: {} (expected: APPROVED)", ruleEvaluator.evaluate(rule, approved2));
        logger.info("Excellent but bankruptcy: {} (expected: DENIED)", ruleEvaluator.evaluate(rule, deniedBankruptcy));
        logger.info("Good but new job: {} (expected: DENIED)", ruleEvaluator.evaluate(rule, deniedNewJob));
        logger.info("No qualifying path: {} (expected: DENIED)", ruleEvaluator.evaluate(rule, deniedNoPath));
    }

    /**
     * EXAMPLE 10: Fraud Detection Rule
     * 
     * Flag as suspicious if:
     *   (amount > 10000 AND NOT(isVerifiedMerchant))
     *   OR
     *   (transactionsLast24h > 10 AND amount > 1000)
     *   OR
     *   (countryMismatch = true AND amount > 500)
     */
    private void demonstrateFraudDetectionRule() {
        logger.info("\n" + "-".repeat(50));
        logger.info("EXAMPLE 10: Fraud Detection Rule");
        logger.info("-".repeat(50));

        Rule rule = Rule.builder()
                .name("Fraud Detection")
                .ruleSlug("fraud-detection")
                .build();

        // Root OR (any suspicious pattern triggers)
        RuleNode rootOr = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.OR)
                .rule(rule)
                .build();

        // Pattern 1: Large amount to unverified merchant
        RuleNode pattern1 = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .parentRuleNode(rootOr)
                .build();

        RuleCondition largeAmount = RuleCondition.builder()
                .fieldName("amount")
                .valueType(ValueType.INTEGER)
                .operationType(OperationType.GREATER_THAN)
                .expectedValue("10000")
                .ruleNode(pattern1)
                .build();

        RuleNode notVerified = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.NOT)
                .parentRuleNode(pattern1)
                .build();

        RuleCondition verifiedCheck = RuleCondition.builder()
                .fieldName("isVerifiedMerchant")
                .valueType(ValueType.BOOLEAN)
                .operationType(OperationType.EQUALS)
                .expectedValue("true")
                .ruleNode(notVerified)
                .build();

        notVerified.setRuleConditions(Arrays.asList(verifiedCheck));
        pattern1.setRuleConditions(Arrays.asList(largeAmount));
        pattern1.setChildRuleNodes(Arrays.asList(notVerified));

        // Pattern 2: High velocity
        RuleNode pattern2 = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .parentRuleNode(rootOr)
                .build();

        pattern2.setRuleConditions(Arrays.asList(
                RuleCondition.builder()
                        .fieldName("transactionsLast24h")
                        .valueType(ValueType.INTEGER)
                        .operationType(OperationType.GREATER_THAN)
                        .expectedValue("10")
                        .ruleNode(pattern2)
                        .build(),
                RuleCondition.builder()
                        .fieldName("amount")
                        .valueType(ValueType.INTEGER)
                        .operationType(OperationType.GREATER_THAN)
                        .expectedValue("1000")
                        .ruleNode(pattern2)
                        .build()
        ));

        // Pattern 3: Country mismatch
        RuleNode pattern3 = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .parentRuleNode(rootOr)
                .build();

        pattern3.setRuleConditions(Arrays.asList(
                RuleCondition.builder()
                        .fieldName("countryMismatch")
                        .valueType(ValueType.BOOLEAN)
                        .operationType(OperationType.EQUALS)
                        .expectedValue("true")
                        .ruleNode(pattern3)
                        .build(),
                RuleCondition.builder()
                        .fieldName("amount")
                        .valueType(ValueType.INTEGER)
                        .operationType(OperationType.GREATER_THAN)
                        .expectedValue("500")
                        .ruleNode(pattern3)
                        .build()
        ));

        rootOr.setChildRuleNodes(Arrays.asList(pattern1, pattern2, pattern3));
        rule.setRuleNodes(Arrays.asList(rootOr));

        // Test cases
        EvaluationContext legitimate = EvaluationContext.of(Map.of(
                "amount", "5000", "isVerifiedMerchant", "true",
                "transactionsLast24h", "3", "countryMismatch", "false"
        ));

        EvaluationContext suspiciousLargeUnverified = EvaluationContext.of(Map.of(
                "amount", "15000", "isVerifiedMerchant", "false",
                "transactionsLast24h", "1", "countryMismatch", "false"
        ));

        EvaluationContext suspiciousVelocity = EvaluationContext.of(Map.of(
                "amount", "2000", "isVerifiedMerchant", "true",
                "transactionsLast24h", "15", "countryMismatch", "false"
        ));

        EvaluationContext suspiciousCountry = EvaluationContext.of(Map.of(
                "amount", "800", "isVerifiedMerchant", "true",
                "transactionsLast24h", "2", "countryMismatch", "true"
        ));

        logger.info("Legitimate transaction: {} (expected: false/safe)", ruleEvaluator.evaluate(rule, legitimate));
        logger.info("Large to unverified: {} (expected: true/SUSPICIOUS)", ruleEvaluator.evaluate(rule, suspiciousLargeUnverified));
        logger.info("High velocity: {} (expected: true/SUSPICIOUS)", ruleEvaluator.evaluate(rule, suspiciousVelocity));
        logger.info("Country mismatch: {} (expected: true/SUSPICIOUS)", ruleEvaluator.evaluate(rule, suspiciousCountry));
    }

    /**
     * EXAMPLE 11: Role-Based Access Control Rule
     * 
     * Grant access if:
     *   (role = "ADMIN")
     *   OR
     *   (role = "MANAGER" AND department = resourceDepartment)
     *   OR
     *   (role = "USER" AND isOwner = true AND NOT(isArchived = true))
     */
    private void demonstrateAccessControlRule() {
        logger.info("\n" + "-".repeat(50));
        logger.info("EXAMPLE 11: Role-Based Access Control");
        logger.info("-".repeat(50));

        Rule rule = Rule.builder()
                .name("Access Control")
                .ruleSlug("access-control")
                .build();

        RuleNode rootOr = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.OR)
                .rule(rule)
                .build();

        // Admin path (always allowed)
        RuleNode adminPath = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .parentRuleNode(rootOr)
                .build();

        adminPath.setRuleConditions(Arrays.asList(
                RuleCondition.builder()
                        .fieldName("role")
                        .valueType(ValueType.STRING)
                        .operationType(OperationType.EQUALS)
                        .expectedValue("ADMIN")
                        .ruleNode(adminPath)
                        .build()
        ));

        // Manager path (same department)
        RuleNode managerPath = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .parentRuleNode(rootOr)
                .build();

        managerPath.setRuleConditions(Arrays.asList(
                RuleCondition.builder()
                        .fieldName("role")
                        .valueType(ValueType.STRING)
                        .operationType(OperationType.EQUALS)
                        .expectedValue("MANAGER")
                        .ruleNode(managerPath)
                        .build(),
                RuleCondition.builder()
                        .fieldName("sameDepartment")
                        .valueType(ValueType.BOOLEAN)
                        .operationType(OperationType.EQUALS)
                        .expectedValue("true")
                        .ruleNode(managerPath)
                        .build()
        ));

        // User path (owner of non-archived resource)
        RuleNode userPath = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .parentRuleNode(rootOr)
                .build();

        RuleCondition roleUser = RuleCondition.builder()
                .fieldName("role")
                .valueType(ValueType.STRING)
                .operationType(OperationType.EQUALS)
                .expectedValue("USER")
                .ruleNode(userPath)
                .build();

        RuleCondition isOwner = RuleCondition.builder()
                .fieldName("isOwner")
                .valueType(ValueType.BOOLEAN)
                .operationType(OperationType.EQUALS)
                .expectedValue("true")
                .ruleNode(userPath)
                .build();

        RuleNode notArchived = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.NOT)
                .parentRuleNode(userPath)
                .build();

        RuleCondition archivedCheck = RuleCondition.builder()
                .fieldName("isArchived")
                .valueType(ValueType.BOOLEAN)
                .operationType(OperationType.EQUALS)
                .expectedValue("true")
                .ruleNode(notArchived)
                .build();

        notArchived.setRuleConditions(Arrays.asList(archivedCheck));
        userPath.setRuleConditions(Arrays.asList(roleUser, isOwner));
        userPath.setChildRuleNodes(Arrays.asList(notArchived));

        rootOr.setChildRuleNodes(Arrays.asList(adminPath, managerPath, userPath));
        rule.setRuleNodes(Arrays.asList(rootOr));

        // Test cases
        EvaluationContext admin = EvaluationContext.of(Map.of(
                "role", "ADMIN", "sameDepartment", "false", "isOwner", "false", "isArchived", "true"
        ));

        EvaluationContext managerSameDept = EvaluationContext.of(Map.of(
                "role", "MANAGER", "sameDepartment", "true", "isOwner", "false", "isArchived", "false"
        ));

        EvaluationContext managerDiffDept = EvaluationContext.of(Map.of(
                "role", "MANAGER", "sameDepartment", "false", "isOwner", "false", "isArchived", "false"
        ));

        EvaluationContext userOwner = EvaluationContext.of(Map.of(
                "role", "USER", "sameDepartment", "false", "isOwner", "true", "isArchived", "false"
        ));

        EvaluationContext userOwnerArchived = EvaluationContext.of(Map.of(
                "role", "USER", "sameDepartment", "false", "isOwner", "true", "isArchived", "true"
        ));

        EvaluationContext userNotOwner = EvaluationContext.of(Map.of(
                "role", "USER", "sameDepartment", "false", "isOwner", "false", "isArchived", "false"
        ));

        logger.info("Admin (any resource): {} (expected: GRANTED)", ruleEvaluator.evaluate(rule, admin));
        logger.info("Manager same dept: {} (expected: GRANTED)", ruleEvaluator.evaluate(rule, managerSameDept));
        logger.info("Manager diff dept: {} (expected: DENIED)", ruleEvaluator.evaluate(rule, managerDiffDept));
        logger.info("User owner (active): {} (expected: GRANTED)", ruleEvaluator.evaluate(rule, userOwner));
        logger.info("User owner (archived): {} (expected: DENIED)", ruleEvaluator.evaluate(rule, userOwnerArchived));
        logger.info("User not owner: {} (expected: DENIED)", ruleEvaluator.evaluate(rule, userNotOwner));
    }

    /**
     * EXAMPLE 12: Employee Promotion Eligibility
     * 
     * Eligible for promotion if:
     *   yearsInRole >= 2
     *   AND
     *   (
     *     (performanceRating >= 4 AND NOT(hasWarnings = true))
     *     OR
     *     (performanceRating >= 3 AND completedLeadershipTraining = true AND projectsLed > 2)
     *   )
     *   AND
     *   NOT(
     *     currentlyOnPIP = true 
     *     OR 
     *     (attendanceScore < 80 AND NOT(hasApprovedLeave = true))
     *   )
     */
    private void demonstratePromotionEligibilityRule() {
        logger.info("\n" + "-".repeat(50));
        logger.info("EXAMPLE 12: Employee Promotion Eligibility (Complex)");
        logger.info("-".repeat(50));

        Rule rule = Rule.builder()
                .name("Promotion Eligibility")
                .ruleSlug("promotion-eligibility")
                .build();

        // Root AND
        RuleNode rootAnd = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .rule(rule)
                .build();

        // Condition: yearsInRole >= 2
        RuleCondition tenureCondition = RuleCondition.builder()
                .fieldName("yearsInRole")
                .valueType(ValueType.INTEGER)
                .operationType(OperationType.GREATER_THAN_OR_EQUALS)
                .expectedValue("2")
                .ruleNode(rootAnd)
                .build();

        rootAnd.setRuleConditions(Arrays.asList(tenureCondition));

        // Performance paths OR
        RuleNode performanceOr = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.OR)
                .parentRuleNode(rootAnd)
                .build();

        // High performer path
        RuleNode highPerformerPath = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .parentRuleNode(performanceOr)
                .build();

        RuleCondition highRating = RuleCondition.builder()
                .fieldName("performanceRating")
                .valueType(ValueType.INTEGER)
                .operationType(OperationType.GREATER_THAN_OR_EQUALS)
                .expectedValue("4")
                .ruleNode(highPerformerPath)
                .build();

        RuleNode noWarnings = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.NOT)
                .parentRuleNode(highPerformerPath)
                .build();

        RuleCondition warningsCheck = RuleCondition.builder()
                .fieldName("hasWarnings")
                .valueType(ValueType.BOOLEAN)
                .operationType(OperationType.EQUALS)
                .expectedValue("true")
                .ruleNode(noWarnings)
                .build();

        noWarnings.setRuleConditions(Arrays.asList(warningsCheck));
        highPerformerPath.setRuleConditions(Arrays.asList(highRating));
        highPerformerPath.setChildRuleNodes(Arrays.asList(noWarnings));

        // Leadership track path
        RuleNode leadershipPath = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .parentRuleNode(performanceOr)
                .build();

        leadershipPath.setRuleConditions(Arrays.asList(
                RuleCondition.builder()
                        .fieldName("performanceRating")
                        .valueType(ValueType.INTEGER)
                        .operationType(OperationType.GREATER_THAN_OR_EQUALS)
                        .expectedValue("3")
                        .ruleNode(leadershipPath)
                        .build(),
                RuleCondition.builder()
                        .fieldName("completedLeadershipTraining")
                        .valueType(ValueType.BOOLEAN)
                        .operationType(OperationType.EQUALS)
                        .expectedValue("true")
                        .ruleNode(leadershipPath)
                        .build(),
                RuleCondition.builder()
                        .fieldName("projectsLed")
                        .valueType(ValueType.INTEGER)
                        .operationType(OperationType.GREATER_THAN)
                        .expectedValue("2")
                        .ruleNode(leadershipPath)
                        .build()
        ));

        performanceOr.setChildRuleNodes(Arrays.asList(highPerformerPath, leadershipPath));

        // Disqualifiers NOT
        RuleNode disqualifiersNot = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.NOT)
                .parentRuleNode(rootAnd)
                .build();

        RuleNode disqualifiersOr = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.OR)
                .parentRuleNode(disqualifiersNot)
                .build();

        // PIP check
        RuleCondition pipCheck = RuleCondition.builder()
                .fieldName("currentlyOnPIP")
                .valueType(ValueType.BOOLEAN)
                .operationType(OperationType.EQUALS)
                .expectedValue("true")
                .ruleNode(disqualifiersOr)
                .build();

        // Attendance issue (low attendance without approved leave)
        RuleNode attendanceIssue = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.AND)
                .parentRuleNode(disqualifiersOr)
                .build();

        RuleCondition lowAttendance = RuleCondition.builder()
                .fieldName("attendanceScore")
                .valueType(ValueType.INTEGER)
                .operationType(OperationType.LESS_THAN)
                .expectedValue("80")
                .ruleNode(attendanceIssue)
                .build();

        RuleNode noApprovedLeave = RuleNode.builder()
                .logicalOperationType(LogicalOperationType.NOT)
                .parentRuleNode(attendanceIssue)
                .build();

        RuleCondition approvedLeaveCheck = RuleCondition.builder()
                .fieldName("hasApprovedLeave")
                .valueType(ValueType.BOOLEAN)
                .operationType(OperationType.EQUALS)
                .expectedValue("true")
                .ruleNode(noApprovedLeave)
                .build();

        noApprovedLeave.setRuleConditions(Arrays.asList(approvedLeaveCheck));
        attendanceIssue.setRuleConditions(Arrays.asList(lowAttendance));
        attendanceIssue.setChildRuleNodes(Arrays.asList(noApprovedLeave));

        disqualifiersOr.setRuleConditions(Arrays.asList(pipCheck));
        disqualifiersOr.setChildRuleNodes(Arrays.asList(attendanceIssue));
        disqualifiersNot.setChildRuleNodes(Arrays.asList(disqualifiersOr));

        rootAnd.setChildRuleNodes(Arrays.asList(performanceOr, disqualifiersNot));
        rule.setRuleNodes(Arrays.asList(rootAnd));

        // Test cases
        EvaluationContext starPerformer = EvaluationContext.of(Map.of(
                "yearsInRole", "3", "performanceRating", "5", "hasWarnings", "false",
                "completedLeadershipTraining", "false", "projectsLed", "0",
                "currentlyOnPIP", "false", "attendanceScore", "95", "hasApprovedLeave", "false"
        ));

        EvaluationContext leadershipTrack = EvaluationContext.of(Map.of(
                "yearsInRole", "2", "performanceRating", "3", "hasWarnings", "false",
                "completedLeadershipTraining", "true", "projectsLed", "4",
                "currentlyOnPIP", "false", "attendanceScore", "88", "hasApprovedLeave", "false"
        ));

        EvaluationContext highPerformerWithWarning = EvaluationContext.of(Map.of(
                "yearsInRole", "4", "performanceRating", "5", "hasWarnings", "true",
                "completedLeadershipTraining", "false", "projectsLed", "0",
                "currentlyOnPIP", "false", "attendanceScore", "90", "hasApprovedLeave", "false"
        ));

        EvaluationContext onPIP = EvaluationContext.of(Map.of(
                "yearsInRole", "5", "performanceRating", "5", "hasWarnings", "false",
                "completedLeadershipTraining", "true", "projectsLed", "10",
                "currentlyOnPIP", "true", "attendanceScore", "100", "hasApprovedLeave", "false"
        ));

        EvaluationContext lowAttendanceNoLeave = EvaluationContext.of(Map.of(
                "yearsInRole", "3", "performanceRating", "4", "hasWarnings", "false",
                "completedLeadershipTraining", "false", "projectsLed", "0",
                "currentlyOnPIP", "false", "attendanceScore", "70", "hasApprovedLeave", "false"
        ));

        EvaluationContext lowAttendanceWithLeave = EvaluationContext.of(Map.of(
                "yearsInRole", "3", "performanceRating", "4", "hasWarnings", "false",
                "completedLeadershipTraining", "false", "projectsLed", "0",
                "currentlyOnPIP", "false", "attendanceScore", "70", "hasApprovedLeave", "true"
        ));

        EvaluationContext tooNewInRole = EvaluationContext.of(Map.of(
                "yearsInRole", "1", "performanceRating", "5", "hasWarnings", "false",
                "completedLeadershipTraining", "true", "projectsLed", "5",
                "currentlyOnPIP", "false", "attendanceScore", "100", "hasApprovedLeave", "false"
        ));

        logger.info("Star performer (rating 5, no issues): {} (expected: ELIGIBLE)", 
                ruleEvaluator.evaluate(rule, starPerformer));
        logger.info("Leadership track (rating 3, training+projects): {} (expected: ELIGIBLE)", 
                ruleEvaluator.evaluate(rule, leadershipTrack));
        logger.info("High performer WITH warning: {} (expected: NOT ELIGIBLE)", 
                ruleEvaluator.evaluate(rule, highPerformerWithWarning));
        logger.info("Currently on PIP: {} (expected: NOT ELIGIBLE)", 
                ruleEvaluator.evaluate(rule, onPIP));
        logger.info("Low attendance, no approved leave: {} (expected: NOT ELIGIBLE)", 
                ruleEvaluator.evaluate(rule, lowAttendanceNoLeave));
        logger.info("Low attendance WITH approved leave: {} (expected: ELIGIBLE)", 
                ruleEvaluator.evaluate(rule, lowAttendanceWithLeave));
        logger.info("Too new in role (1 year): {} (expected: NOT ELIGIBLE)", 
                ruleEvaluator.evaluate(rule, tooNewInRole));
    }
}
