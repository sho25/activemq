begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|selector
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|JMSException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|ActiveMQTopic
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
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
name|apache
operator|.
name|activemq
operator|.
name|filter
operator|.
name|MessageEvaluationContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|UnknownHandlingSelectorTest
block|{
specifier|private
name|Message
name|message
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|message
operator|=
operator|new
name|ActiveMQMessage
argument_list|()
expr_stmt|;
name|message
operator|.
name|setJMSDestination
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
literal|"FOO.BAR"
argument_list|)
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSType
argument_list|(
literal|"selector-test"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setJMSMessageID
argument_list|(
literal|"connection:1:1:1:1"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setBooleanProperty
argument_list|(
literal|"trueProp"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|message
operator|.
name|setBooleanProperty
argument_list|(
literal|"falseProp"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|message
operator|.
name|setObjectProperty
argument_list|(
literal|"nullProp"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * | NOT      * +------+------      * |  T   |   F      * |  F   |   T      * |  U   |   U      * +------+-------      */
annotation|@
name|Test
specifier|public
name|void
name|notEvaluation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertSelector
argument_list|(
literal|"not(trueProp)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
literal|"not(falseProp)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
literal|"not(unknownProp)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * | AND  |   T   |   F   |   U      * +------+-------+-------+-------      * |  T   |   T   |   F   |   U      * |  F   |   F   |   F   |   F      * |  U   |   U   |   F   |   U      * +------+-------+-------+-------      */
annotation|@
name|Test
specifier|public
name|void
name|andEvaluation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertSelectorEvaluatesToTrue
argument_list|(
literal|"trueProp AND trueProp"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToFalse
argument_list|(
literal|"trueProp AND falseProp"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToFalse
argument_list|(
literal|"falseProp AND trueProp"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToFalse
argument_list|(
literal|"falseProp AND falseProp"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToFalse
argument_list|(
literal|"falseProp AND unknownProp"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToFalse
argument_list|(
literal|"unknownProp AND falseProp"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"trueProp AND unknownProp"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp AND trueProp"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp AND unknownProp"
argument_list|)
expr_stmt|;
block|}
comment|/**      * | OR   |   T   |   F   |   U      * +------+-------+-------+--------      * |  T   |   T   |   T   |   T      * |  F   |   T   |   F   |   U      * |  U   |   T   |   U   |   U      * +------+-------+-------+-------      */
annotation|@
name|Test
specifier|public
name|void
name|orEvaluation
parameter_list|()
throws|throws
name|Exception
block|{
name|assertSelectorEvaluatesToTrue
argument_list|(
literal|"trueProp OR trueProp"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToTrue
argument_list|(
literal|"trueProp OR falseProp"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToTrue
argument_list|(
literal|"falseProp OR trueProp"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToTrue
argument_list|(
literal|"trueProp OR unknownProp"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToTrue
argument_list|(
literal|"unknownProp OR trueProp"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToFalse
argument_list|(
literal|"falseProp OR falseProp"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"falseProp OR unknownProp"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp OR falseProp"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp OR unknownProp"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|comparisonWithUnknownShouldEvaluateToUnknown
parameter_list|()
throws|throws
name|Exception
block|{
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp = 0"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp> 0"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp>= 0"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp< 0"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp<= 0"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp<> 0"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp LIKE 'zero'"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp NOT LIKE 'zero'"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp IN ('zero')"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp NOT IN ('zero')"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp BETWEEN 1 AND 2"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp NOT BETWEEN 1 AND 2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|comparisonWithNullPropShouldEvaluateToUnknown
parameter_list|()
throws|throws
name|Exception
block|{
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"nullProp = 0"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"nullProp> 0"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"nullProp>= 0"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"nullProp< 0"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"nullProp<= 0"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"nullProp<> 0"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"nullProp LIKE 'zero'"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"nullProp NOT LIKE 'zero'"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"nullProp IN ('zero')"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"nullProp NOT IN ('zero')"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"nullProp BETWEEN 1 AND 2"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"nullProp NOT BETWEEN 1 AND 2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|isNullIsNotNull
parameter_list|()
throws|throws
name|Exception
block|{
name|assertSelectorEvaluatesToTrue
argument_list|(
literal|"unknownProp IS NULL"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToTrue
argument_list|(
literal|"nullProp IS NULL"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToFalse
argument_list|(
literal|"trueProp IS NULL"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToFalse
argument_list|(
literal|"unknownProp IS NOT NULL"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToFalse
argument_list|(
literal|"nullProp IS NOT NULL"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToTrue
argument_list|(
literal|"trueProp IS NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|arithmeticWithNull
parameter_list|()
throws|throws
name|Exception
block|{
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"-unknownProp = 0"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"+unknownProp = 0"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp * 2 = 0"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp / 2 = 0"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp + 2 = 0"
argument_list|)
expr_stmt|;
name|assertSelectorEvaluatesToUnknown
argument_list|(
literal|"unknownProp - 2 = 0"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertSelectorEvaluatesToUnknown
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|JMSException
block|{
name|assertSelector
argument_list|(
name|selector
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|not
argument_list|(
name|selector
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertSelectorEvaluatesToTrue
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|JMSException
block|{
name|assertSelector
argument_list|(
name|selector
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|not
argument_list|(
name|selector
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertSelectorEvaluatesToFalse
parameter_list|(
name|String
name|selector
parameter_list|)
throws|throws
name|JMSException
block|{
name|assertSelector
argument_list|(
name|selector
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|not
argument_list|(
name|selector
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertSelector
parameter_list|(
name|String
name|text
parameter_list|,
name|boolean
name|matches
parameter_list|)
throws|throws
name|JMSException
block|{
name|BooleanExpression
name|selector
init|=
name|SelectorParser
operator|.
name|parse
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Created a valid selector"
argument_list|,
name|selector
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|MessageEvaluationContext
name|context
init|=
operator|new
name|MessageEvaluationContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setMessageReference
argument_list|(
operator|(
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|Message
operator|)
name|message
argument_list|)
expr_stmt|;
name|boolean
name|value
init|=
name|selector
operator|.
name|matches
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Selector for: "
operator|+
name|text
argument_list|,
name|matches
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|not
parameter_list|(
name|String
name|selector
parameter_list|)
block|{
return|return
literal|"not("
operator|+
name|selector
operator|+
literal|")"
return|;
block|}
block|}
end_class

end_unit

