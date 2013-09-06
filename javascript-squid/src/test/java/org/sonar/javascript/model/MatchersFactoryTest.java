/*
 * Sonar JavaScript Plugin
 * Copyright (C) 2011 SonarSource and Eriks Nukis
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.javascript.model;

import com.google.common.base.Charsets;
import com.sonar.sslr.api.AstNode;
import com.sonar.sslr.impl.Parser;
import org.junit.Test;
import org.sonar.javascript.EcmaScriptConfiguration;
import org.sonar.javascript.parser.EcmaScriptGrammar;
import org.sonar.javascript.parser.EcmaScriptParser;

import static org.fest.assertions.Assertions.assertThat;

public class MatchersFactoryTest {

  private final Parser p = EcmaScriptParser.create(new EcmaScriptConfiguration(Charsets.UTF_8));
  private final Matchers m = new MatchersFactory();

  @Test
  public void test() {
    Matcher ifStatementWithElseMatcher =
      m.ifStatement(m.hasElseClause(m.<StatementTree>anything()));
    assertThat(execute(ifStatementWithElseMatcher, EcmaScriptGrammar.IF_STATEMENT, "if (true) {}")).isFalse();
    assertThat(execute(ifStatementWithElseMatcher, EcmaScriptGrammar.IF_STATEMENT, "if (true) {} else {}")).isTrue();
    assertThat(execute(ifStatementWithElseMatcher, EcmaScriptGrammar.BLOCK, "{}")).isFalse();

    assertThat(execute(m.hasBody(m.<StatementTree>anything()), EcmaScriptGrammar.BLOCK, "{}")).isFalse();
  }

  @Test
  public void example_CollapsibleIfStatement() {
    Matcher<IfStatementTree> ifWithoutElse =
      m.ifStatement(m.unless(m.hasElseClause(m.<StatementTree>anything())));
    Matcher<IfStatementTree> collapsibleIf =
      m.ifStatement(ifWithoutElse, m.hasThenClause(m.<StatementTree>anyOf(
        m.compoundStatement(m.statementCountIs(1), m.<BlockTree>has(ifWithoutElse.capture())),
        ifWithoutElse.capture())));
    System.out.println(collapsibleIf);

    // versus

    IfStatementTree ifStatementTree = create(EcmaScriptGrammar.IF_STATEMENT, "if (true) {}");
    if (isIfWithoutElse(ifStatementTree)) {
      if (isIfWithoutElse(ifStatementTree.thenStatement())) {
        // capture
      } else if (isCompoundStatementWithOneIfWithoutElse(ifStatementTree.thenStatement())) {
        // capture
      }
    }
  }

  static boolean isCompoundStatementWithOneIfWithoutElse(StatementTree statementTree) {
    // TODO introduction of Tree.asOrNull can simplify this code
    if (statementTree != null && statementTree.is(BlockTree.class)) {
      BlockTree compoundStatementTree = statementTree.as(BlockTree.class);
      return compoundStatementTree != null
        && compoundStatementTree.statements().size() == 1
        && isIfWithoutElse(compoundStatementTree.statements().get(0));
    }
    return false;
  }

  static boolean isIfWithoutElse(StatementTree statementTree) {
    // TODO introduction of Tree.asOrNull can simplify this code
    if (statementTree != null && statementTree.is(IfStatementTree.class)) {
      IfStatementTree ifStatementTree = statementTree.as(IfStatementTree.class);
      return ifStatementTree != null
        && ifStatementTree.elseStatement() == null;
    }
    return false;
  }

  @Test
  public void example_IfTrue() {
    m.ifStatement(m.<IfStatementTree>hasCondition(m.boolLiteral(m.<LiteralTree>equalTo(true)))).capture();
  }

  @Test
  public void example_AssignmentWithinCondition() {
    m.hasCondition(m.binaryOperator(m.hasOperator(EcmaScriptGrammar.ASSIGNMENT_OPERATOR))).capture();
  }

  @Test
  public void example_AlwaysUseCurlyBraces() {
    m.hasBody(m.not(m.compoundStatement())).capture();
    m.ifStatement(m.anyOf(
      m.hasThenClause(m.not(m.compoundStatement()).capture()),
      m.hasElseClause(m.allOf(
        m.not(m.compoundStatement()),
        // if (...) { } else if (...) { }
        m.not(m.ifStatement())
      ))).capture()
    );
  }

  @Test
  public void example_ElseIfWithoutElse() {
    m.ifStatement(
      m.hasElseClause(
        m.ifStatement(
          m.not(m.hasElseClause(m.<StatementTree>anything()))
        ).capture()));
  }

  @Test
  public void example_EmptyBlock() {
    m.compoundStatement(m.statementCountIs(0)).capture();
  }

  @Test
  public void example_LabelPlacement() {
    m.labelledStatement(m.<LabelledStatementTree>hasBody(m.not(m.anyOf(
      m.doWhileStatement(),
      m.whileStatement(),
      m.forInStatement(),
      m.forStatement()
    )))).capture();
  }

  private boolean execute(Matcher matcher, EcmaScriptGrammar ruleKey, String input) {
    Tree tree = create(ruleKey, input);
    return ((MatchersFactory.AbstractMatcher) matcher).matches(tree);
  }

  private <T extends Tree> T create(EcmaScriptGrammar ruleKey, String input) {
    AstNode astNode = p.parse(input).getFirstDescendant(ruleKey);
    return (T) ASTMaker.create().makeFrom(astNode);
  }

}
