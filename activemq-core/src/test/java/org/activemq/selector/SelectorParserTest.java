begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|selector
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|filter
operator|.
name|BooleanExpression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|filter
operator|.
name|ComparisonExpression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|filter
operator|.
name|Expression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|filter
operator|.
name|LogicExpression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|filter
operator|.
name|PropertyExpression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|filter
operator|.
name|XPathExpression
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|SelectorParserTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|static
specifier|final
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
name|log
init|=
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
operator|.
name|getLog
argument_list|(
name|SelectorParserTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|void
name|testParseXPath
parameter_list|()
throws|throws
name|Exception
block|{
name|BooleanExpression
name|filter
init|=
name|parse
argument_list|(
literal|"XPATH '//title[@lang=''eng'']'"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Created XPath expression"
argument_list|,
name|filter
operator|instanceof
name|XPathExpression
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Expression: "
operator|+
name|filter
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testParseWithParensAround
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|values
init|=
block|{
literal|"x = 1 and y = 2"
block|,
literal|"(x = 1) and (y = 2)"
block|,
literal|"((x = 1) and (y = 2))"
block|}
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|value
init|=
name|values
index|[
name|i
index|]
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Parsing: "
operator|+
name|value
argument_list|)
expr_stmt|;
name|BooleanExpression
name|andExpression
init|=
name|parse
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Created LogicExpression expression"
argument_list|,
name|andExpression
operator|instanceof
name|LogicExpression
argument_list|)
expr_stmt|;
name|LogicExpression
name|logicExpression
init|=
operator|(
name|LogicExpression
operator|)
name|andExpression
decl_stmt|;
name|Expression
name|left
init|=
name|logicExpression
operator|.
name|getLeft
argument_list|()
decl_stmt|;
name|Expression
name|right
init|=
name|logicExpression
operator|.
name|getRight
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Left is a binary filter"
argument_list|,
name|left
operator|instanceof
name|ComparisonExpression
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Right is a binary filter"
argument_list|,
name|right
operator|instanceof
name|ComparisonExpression
argument_list|)
expr_stmt|;
name|ComparisonExpression
name|leftCompare
init|=
operator|(
name|ComparisonExpression
operator|)
name|left
decl_stmt|;
name|ComparisonExpression
name|rightCompare
init|=
operator|(
name|ComparisonExpression
operator|)
name|right
decl_stmt|;
name|assertPropertyExpression
argument_list|(
literal|"left"
argument_list|,
name|leftCompare
operator|.
name|getLeft
argument_list|()
argument_list|,
literal|"x"
argument_list|)
expr_stmt|;
name|assertPropertyExpression
argument_list|(
literal|"right"
argument_list|,
name|rightCompare
operator|.
name|getLeft
argument_list|()
argument_list|,
literal|"y"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|assertPropertyExpression
parameter_list|(
name|String
name|message
parameter_list|,
name|Expression
name|expression
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|message
operator|+
literal|". Must be PropertyExpression"
argument_list|,
name|expression
operator|instanceof
name|PropertyExpression
argument_list|)
expr_stmt|;
name|PropertyExpression
name|propExp
init|=
operator|(
name|PropertyExpression
operator|)
name|expression
decl_stmt|;
name|assertEquals
argument_list|(
name|message
operator|+
literal|". Property name"
argument_list|,
name|expected
argument_list|,
name|propExp
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|BooleanExpression
name|parse
parameter_list|(
name|String
name|text
parameter_list|)
throws|throws
name|Exception
block|{
return|return
operator|new
name|SelectorParser
argument_list|()
operator|.
name|parse
argument_list|(
name|text
argument_list|)
return|;
block|}
block|}
end_class

end_unit

