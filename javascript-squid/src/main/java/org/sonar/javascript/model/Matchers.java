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

import org.sonar.javascript.parser.EcmaScriptGrammar;

@SuppressWarnings("UnusedDeclaration")
public interface Matchers {

  // Node Matchers - specify the type of node that is expected.

  Matcher<BlockTree> compoundStatement(Matcher<BlockTree>... m);

  Matcher<IfStatementTree> ifStatement(Matcher<IfStatementTree>... m);

  Matcher<WhileStatementTree> whileStatement(Matcher<WhileStatementTree>... m);

  Matcher<DoWhileStatementTree> doWhileStatement(Matcher<DoWhileStatementTree>... m);

  Matcher<ForStatementTree> forStatement(Matcher<ForStatementTree>... m);

  Matcher<ForInStatementTree> forInStatement(Matcher<ForInStatementTree>... m);

  Matcher<LabelledStatementTree> labelledStatement(Matcher<LabelledStatementTree>... m);

  Matcher<BinaryOperatorTree> binaryOperator(Matcher<BinaryOperatorTree>... m);

  Matcher<LiteralTree> boolLiteral(Matcher<LiteralTree>... m);

  // Narrowing Matchers - match certain attributes on the current node.

  /**
   * Matches if all given matchers match.
   */
  <T extends Tree> Matcher<T> allOf(Matcher<? extends T>... m);

  /**
   * Matches if any of the given matchers matches.
   */
  <T extends Tree> Matcher<T> anyOf(Matcher<? extends T>... m);

  /**
   * Matches any node.
   * Useful when another matcher requires a child matcher, but there's no additional constraint.
   */
  <T extends Tree> Matcher<T> anything();

  /**
   * Matches if the provided matcher does not match.
   */
  <T extends Tree> Matcher<T> unless(Matcher<T> m);

  /**
   * Equivalent of {@link #unless(Matcher)}.
   * TODO Godin: don't know which of the names is better
   */
  <T extends Tree> Matcher<T> not(Matcher<T> m);

  Matcher<BinaryOperatorTree> hasOperator(/* FIXME little hack: */ EcmaScriptGrammar ruleKey);

  /**
   * Matches literals that are equal to the given value.
   */
  <T extends LiteralTree> Matcher<T> equalTo(Object value);

  // Traversal Matchers - specify the relationship to other nodes that are reachable from the current node.

  /**
   * Matches AST nodes that have child AST nodes that match the provided matcher.
   */
  <T extends Tree> Matcher<T> has(Matcher<? extends Tree> m);

  /**
   * Matches AST nodes that have an ancestor that matches the provided matcher.
   */
  <T extends Tree> Matcher<T> hasAncestor(Matcher<? extends Tree> m);

  /**
   * Matches AST nodes that have descendant AST nodes that match the provided matcher.
   */
  <T extends Tree> Matcher<T> hasDescendant(Matcher<? extends Tree> m);

  /**
   * Matches AST nodes that have a parent that matches the provided matcher.
   */
  <T extends Tree> Matcher<T> hasParent(Matcher<? extends Tree> m);

  /**
   * Applicable for {@link org.sonar.javascript.model.IfStatementTree}, {@link org.sonar.javascript.model.WhileStatementTree}, {@link org.sonar.javascript.model.DoWhileStatementTree},
   * {@link org.sonar.javascript.model.ForStatementTree}, {@link org.sonar.javascript.model.ConditionalOperatorTree}.
   */
  <T extends HasCondition> Matcher<T> hasCondition(Matcher<? extends ExpressionTree> m);

  /**
   * Applicable for {@link org.sonar.javascript.model.ReturnStatementTree}, {@link org.sonar.javascript.model.ThrowStatementTree}, {@link org.sonar.javascript.model.WithStatementTree}.
   */
  <T extends HasExpression> Matcher<T> hasExpression(Matcher<? extends ExpressionTree> m);

  /**
   * Applicable for {@link org.sonar.javascript.model.WhileStatementTree}, {@link org.sonar.javascript.model.DoWhileStatementTree}, {@link org.sonar.javascript.model.ForStatementTree},
   * {@link org.sonar.javascript.model.LabelledStatementTree}.
   */
  <T extends HasBody> Matcher<T> hasBody(Matcher<? extends StatementTree> m);

  Matcher<IfStatementTree> hasThenClause(Matcher<? extends StatementTree> m);

  Matcher<IfStatementTree> hasElseClause(Matcher<? extends StatementTree> m);

  /**
   * Checks that a compound statement contains a specific number of child statements.
   */
  Matcher<BlockTree> statementCountIs(int n);

  // Marker interfaces

  public interface HasBody extends Tree {
    StatementTree statement();
  }

  public interface HasCondition extends Tree {
    ExpressionTree condition();
  }

  public interface HasExpression extends Tree {
    ExpressionTree expression();
  }

}
