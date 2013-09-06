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

public class MatchersFactory implements Matchers {

  public static abstract class AbstractMatcher<T extends Tree> implements Matcher<T> {
    @Override
    public Matcher<T> capture() {
      // TODO implement me, nop for the time being
      return this;
    }

    public abstract boolean matches(Tree tree);
  }

  private static class UnsupportedMatcher<T extends Tree> extends AbstractMatcher<T> {
    @Override
    public boolean matches(Tree tree) {
      return false;
    }
  }

  public static class NodeMatcher<T extends Tree> extends AbstractMatcher<T> {
    private final Class<T> cls;
    private final Matcher<T>[] m;

    public NodeMatcher(Class<T> cls, Matcher<T>[] m) {
      this.cls = cls;
      this.m = m;
    }

    public boolean matches(Tree tree) {
      if (!tree.is(cls)) {
        return false;
      }
      for (Matcher i : m) {
        if (!((AbstractMatcher) i).matches(tree)) {
          return false;
        }
      }
      return true;
    }
  }

  @Override
  public Matcher<BlockTree> compoundStatement(final Matcher<BlockTree>... m) {
    return new NodeMatcher<BlockTree>(BlockTree.class, m);
  }

  @Override
  public Matcher<IfStatementTree> ifStatement(final Matcher<IfStatementTree>... m) {
    return new NodeMatcher<IfStatementTree>(IfStatementTree.class, m);
  }

  @Override
  public Matcher<WhileStatementTree> whileStatement(final Matcher<WhileStatementTree>... m) {
    return new NodeMatcher<WhileStatementTree>(WhileStatementTree.class, m);
  }

  @Override
  public Matcher<DoWhileStatementTree> doWhileStatement(final Matcher<DoWhileStatementTree>... m) {
    return new NodeMatcher<DoWhileStatementTree>(DoWhileStatementTree.class, m);
  }

  @Override
  public Matcher<ForStatementTree> forStatement(final Matcher<ForStatementTree>... m) {
    return new NodeMatcher<ForStatementTree>(ForStatementTree.class, m);
  }

  @Override
  public Matcher<ForInStatementTree> forInStatement(final Matcher<ForInStatementTree>... m) {
    return new NodeMatcher<ForInStatementTree>(ForInStatementTree.class, m);
  }

  @Override
  public Matcher<LabelledStatementTree> labelledStatement(final Matcher<LabelledStatementTree>... m) {
    return new NodeMatcher<LabelledStatementTree>(LabelledStatementTree.class, m);
  }

  @Override
  public Matcher<BinaryOperatorTree> binaryOperator(final Matcher<BinaryOperatorTree>... m) {
    return new NodeMatcher<BinaryOperatorTree>(BinaryOperatorTree.class, m);
  }

  @Override
  public Matcher<LiteralTree> boolLiteral(final Matcher<LiteralTree>... m) {
    return new NodeMatcher<LiteralTree>(LiteralTree.class, m);
  }

  @Override
  public <T extends Tree> Matcher<T> allOf(final Matcher<? extends T>... m) {
    return new AbstractMatcher<T>() {
      @Override
      public boolean matches(Tree tree) {
        for (Matcher i : m) {
          if (!((AbstractMatcher) i).matches(tree)) {
            return false;
          }
        }
        return true;
      }
    };
  }

  @Override
  public <T extends Tree> Matcher<T> anyOf(final Matcher<? extends T>... m) {
    return new AbstractMatcher<T>() {
      @Override
      public boolean matches(Tree tree) {
        for (Matcher i : m) {
          if (!((AbstractMatcher) i).matches(tree)) {
            return true;
          }
        }
        return false;
      }
    };

  }

  @Override
  public <T extends Tree> Matcher<T> anything() {
    return new AbstractMatcher<T>() {
      @Override
      public boolean matches(Tree tree) {
        return true;
      }
    };
  }

  @Override
  public <T extends Tree> Matcher<T> unless(final Matcher<T> m) {
    return not(m);
  }

  @Override
  public <T extends Tree> Matcher<T> not(final Matcher<T> m) {
    return new AbstractMatcher<T>() {
      @Override
      public boolean matches(Tree tree) {
        return ((AbstractMatcher) m).matches(tree);
      }
    };
  }

  @Override
  public Matcher<BinaryOperatorTree> hasOperator(final EcmaScriptGrammar ruleKey) {
    return new AbstractMatcher<BinaryOperatorTree>() {
      @Override
      public boolean matches(Tree tree) {
        if (tree instanceof BinaryOperatorTree) {
          BinaryOperatorTree binaryOperatorTree = (BinaryOperatorTree) tree;
          return binaryOperatorTree.operator() == ruleKey;
        }
        return false;
      }
    };
  }

  @Override
  public <T extends LiteralTree> Matcher<T> equalTo(final Object value) {
    // TODO
    return new UnsupportedMatcher<T>();
  }

  @Override
  public <T extends Tree> Matcher<T> has(final Matcher<? extends Tree> m) {
    // TODO
    return new UnsupportedMatcher<T>();
  }

  @Override
  public <T extends Tree> Matcher<T> hasAncestor(final Matcher<? extends Tree> m) {
    // TODO
    return new UnsupportedMatcher<T>();
  }

  @Override
  public <T extends Tree> Matcher<T> hasDescendant(final Matcher<? extends Tree> m) {
    // TODO
    return new UnsupportedMatcher<T>();
  }

  @Override
  public <T extends Tree> Matcher<T> hasParent(final Matcher<? extends Tree> m) {
    // TODO
    return new UnsupportedMatcher<T>();
  }

  @Override
  public <T extends HasCondition> Matcher<T> hasCondition(final Matcher<? extends ExpressionTree> m) {
    return new AbstractMatcher<T>() {
      @Override
      public boolean matches(Tree tree) {
        if (tree.is(HasCondition.class)) {
          HasCondition hasCondition = (HasCondition) tree;
          return hasCondition.condition() != null
            && ((AbstractMatcher) m).matches(hasCondition.condition());
        }
        return false;
      }
    };
  }

  @Override
  public <T extends HasExpression> Matcher<T> hasExpression(final Matcher<? extends ExpressionTree> m) {
    return new AbstractMatcher<T>() {
      @Override
      public boolean matches(Tree tree) {
        if (tree.is(HasExpression.class)) {
          HasExpression hasExpression = (HasExpression) tree;
          return hasExpression.expression() != null
            && ((AbstractMatcher) m).matches(hasExpression.expression());
        }
        return false;
      }
    };
  }

  @Override
  public <T extends HasBody> Matcher<T> hasBody(final Matcher<? extends StatementTree> m) {
    return new AbstractMatcher<T>() {
      @Override
      public boolean matches(Tree tree) {
        if (tree.is(HasBody.class)) {
          HasBody hasBody = (HasBody) tree;
          return hasBody.statement() != null
            && ((AbstractMatcher) m).matches(hasBody.statement());
        }
        return false;
      }
    };
  }

  @Override
  public Matcher<IfStatementTree> hasThenClause(final Matcher<? extends StatementTree> m) {
    return new AbstractMatcher<IfStatementTree>() {
      @Override
      public boolean matches(Tree tree) {
        if (tree.is(IfStatementTree.class)) {
          IfStatementTree ifStatementTree = (IfStatementTree) tree;
          return ifStatementTree.thenStatement() != null
            && ((AbstractMatcher) m).matches(ifStatementTree.thenStatement());
        }
        return false;
      }
    };
  }

  @Override
  public Matcher<IfStatementTree> hasElseClause(final Matcher<? extends StatementTree> m) {
    return new AbstractMatcher<IfStatementTree>() {
      @Override
      public boolean matches(Tree tree) {
        if (tree.is(IfStatementTree.class)) {
          IfStatementTree ifStatementTree = (IfStatementTree) tree;
          return ifStatementTree.elseStatement() != null
            && ((AbstractMatcher) m).matches(ifStatementTree.elseStatement());
        }
        return false;
      }
    };
  }

  @Override
  public Matcher<BlockTree> statementCountIs(final int n) {
    return new AbstractMatcher<BlockTree>() {
      @Override
      public boolean matches(Tree tree) {
        if (tree.is(BlockTree.class)) {
          BlockTree blockTree = (BlockTree) tree;
          return blockTree.statements().size() == n;
        }
        return false;
      }
    };
  }

}
