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

public class MatchersDSL2 {

  static Matchers factory() {
    return null;
  }

  static Matchers m = factory();

  public static void main(String[] args) {
    IfStatementMatcher ifWithoutElse = m.ifStatement().elseClause(m.<StatementMatcher>isNull());
    ifWithoutElse
      .thenClause(m.oneOf(
        m.compoundStatement().statements(ifWithoutElse),
        ifWithoutElse
      ));
  }

  interface Matchers {

    IfStatementMatcher ifStatement();
    CompoundStatementMatcher compoundStatement();
    <T> T oneOf(T... m);
    <T> T isNull();

  }

  public interface IfStatementMatcher extends StatementMatcher {
    IfStatementMatcher thenClause(StatementMatcher m);
    IfStatementMatcher elseClause(StatementMatcher m);
  }

  public interface Matcher {
  }

  public interface CompoundStatementMatcher extends StatementMatcher {
    CompoundStatementMatcher statements(StatementMatcher... m);
  }

  public interface StatementMatcher extends Matcher {
  }

}
