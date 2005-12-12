begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   * Copyright 2005 LogicBlaze, Inc. http://www.logicblaze.com  *   * Licensed under the Apache License, Version 2.0 (the "License");   * you may not use this file except in compliance with the License.   * You may obtain a copy of the License at   *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,   * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   * See the License for the specific language governing permissions and   * limitations under the License.   *   **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|systest
package|;
end_package

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|ApplicationContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Destination
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

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
name|junit
operator|.
name|framework
operator|.
name|TestSuite
import|;
end_import

begin_comment
comment|/**  * A helper class for creating a test suite  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|ScenarioTestSuite
extends|extends
name|TestCase
block|{
specifier|private
name|List
name|brokers
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|cacheBrokers
decl_stmt|;
specifier|public
specifier|static
name|TestSuite
name|createSuite
parameter_list|(
name|ApplicationContext
name|clientContext
parameter_list|,
name|ApplicationContext
name|brokerContext
parameter_list|,
name|Class
index|[]
name|scenarios
parameter_list|,
name|int
name|destinationType
parameter_list|)
throws|throws
name|Exception
block|{
name|TestSuite
name|suite
init|=
operator|new
name|TestSuite
argument_list|()
decl_stmt|;
name|ScenarioTestSuite
name|test
init|=
operator|new
name|ScenarioTestSuite
argument_list|()
decl_stmt|;
name|test
operator|.
name|appendTestCases
argument_list|(
name|suite
argument_list|,
name|clientContext
argument_list|,
name|brokerContext
argument_list|,
name|scenarios
argument_list|,
name|destinationType
argument_list|)
expr_stmt|;
return|return
name|suite
return|;
block|}
specifier|public
name|void
name|appendTestCases
parameter_list|(
name|TestSuite
name|suite
parameter_list|,
name|ApplicationContext
name|clientContext
parameter_list|,
name|ApplicationContext
name|brokerContext
parameter_list|,
name|Class
index|[]
name|scenarios
parameter_list|,
name|int
name|destinationType
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|scenarios
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Class
name|scenario
init|=
name|scenarios
index|[
name|i
index|]
decl_stmt|;
name|appendTestCase
argument_list|(
name|suite
argument_list|,
name|clientContext
argument_list|,
name|brokerContext
argument_list|,
name|scenario
argument_list|,
name|destinationType
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|appendTestCase
parameter_list|(
name|TestSuite
name|suite
parameter_list|,
name|ApplicationContext
name|clientContext
parameter_list|,
name|ApplicationContext
name|brokerContext
parameter_list|,
name|Class
name|scenario
parameter_list|,
name|int
name|destinationType
parameter_list|)
throws|throws
name|Exception
block|{
comment|// lets figure out how to create the scenario from all the options
comment|// available
name|Constructor
index|[]
name|constructors
init|=
name|scenario
operator|.
name|getConstructors
argument_list|()
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
name|constructors
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Constructor
name|constructor
init|=
name|constructors
index|[
name|i
index|]
decl_stmt|;
name|appendTestCase
argument_list|(
name|suite
argument_list|,
name|clientContext
argument_list|,
name|brokerContext
argument_list|,
name|scenario
argument_list|,
name|constructor
argument_list|,
name|destinationType
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|appendTestCase
parameter_list|(
name|TestSuite
name|suite
parameter_list|,
name|ApplicationContext
name|clientContext
parameter_list|,
name|ApplicationContext
name|brokerContext
parameter_list|,
name|Class
name|scenario
parameter_list|,
name|Constructor
name|constructor
parameter_list|,
name|int
name|destinationType
parameter_list|)
throws|throws
name|Exception
block|{
comment|// lets configure the test case
name|Class
index|[]
name|parameterTypes
init|=
name|constructor
operator|.
name|getParameterTypes
argument_list|()
decl_stmt|;
name|int
name|size
init|=
name|parameterTypes
operator|.
name|length
decl_stmt|;
name|String
index|[]
index|[]
name|namesForParameters
init|=
operator|new
name|String
index|[
name|size
index|]
index|[]
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|Class
name|parameterType
init|=
name|parameterTypes
index|[
name|i
index|]
decl_stmt|;
name|String
index|[]
name|names
init|=
name|clientContext
operator|.
name|getBeanNamesForType
argument_list|(
name|parameterType
argument_list|)
decl_stmt|;
if|if
condition|(
name|names
operator|==
literal|null
operator|||
name|names
operator|.
name|length
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|parameterType
operator|.
name|equals
argument_list|(
name|BrokerAgent
operator|.
name|class
argument_list|)
condition|)
block|{
name|names
operator|=
operator|new
name|String
index|[
literal|1
index|]
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"No bean instances available in the ApplicationContext for type: "
operator|+
name|parameterType
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|namesForParameters
index|[
name|i
index|]
operator|=
name|names
expr_stmt|;
block|}
comment|// lets try out each permutation of configuration
name|int
index|[]
name|counters
init|=
operator|new
name|int
index|[
name|size
index|]
decl_stmt|;
name|boolean
name|completed
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|completed
condition|)
block|{
name|Object
index|[]
name|parameters
init|=
operator|new
name|Object
index|[
name|size
index|]
decl_stmt|;
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|(
name|scenario
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|brokerCounter
init|=
literal|1
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|String
name|beanName
init|=
name|namesForParameters
index|[
name|i
index|]
index|[
name|counters
index|[
name|i
index|]
index|]
decl_stmt|;
if|if
condition|(
name|beanName
operator|!=
literal|null
condition|)
block|{
name|parameters
index|[
name|i
index|]
operator|=
name|clientContext
operator|.
name|getBean
argument_list|(
name|beanName
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|beanName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parameters
index|[
name|i
index|]
operator|=
name|getBrokerAgent
argument_list|(
name|brokerContext
argument_list|,
name|brokerCounter
operator|++
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|destinationName
init|=
name|buffer
operator|.
name|toString
argument_list|()
decl_stmt|;
name|addTestsFor
argument_list|(
name|suite
argument_list|,
name|clientContext
argument_list|,
name|brokerContext
argument_list|,
name|constructor
argument_list|,
name|parameters
argument_list|,
name|destinationName
argument_list|,
name|destinationType
argument_list|)
expr_stmt|;
comment|// now lets count though the options
name|int
name|pivot
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|++
name|counters
index|[
name|pivot
index|]
operator|>=
name|namesForParameters
index|[
name|pivot
index|]
operator|.
name|length
condition|)
block|{
name|counters
index|[
name|pivot
index|]
operator|=
literal|0
expr_stmt|;
name|pivot
operator|++
expr_stmt|;
if|if
condition|(
name|pivot
operator|>=
name|size
condition|)
block|{
name|completed
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
specifier|protected
name|BrokerAgent
name|getBrokerAgent
parameter_list|(
name|ApplicationContext
name|brokerContext
parameter_list|,
name|int
name|brokerCounter
parameter_list|)
block|{
if|if
condition|(
name|cacheBrokers
condition|)
block|{
name|BrokerAgent
name|broker
init|=
literal|null
decl_stmt|;
name|int
name|index
init|=
name|brokerCounter
operator|-
literal|1
decl_stmt|;
comment|// lets reuse broker instances across test cases
if|if
condition|(
name|brokers
operator|.
name|size
argument_list|()
operator|>=
name|brokerCounter
condition|)
block|{
name|broker
operator|=
operator|(
name|BrokerAgent
operator|)
name|brokers
operator|.
name|get
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|broker
operator|==
literal|null
condition|)
block|{
name|broker
operator|=
operator|(
name|BrokerAgent
operator|)
name|brokerContext
operator|.
name|getBean
argument_list|(
literal|"broker"
operator|+
name|brokerCounter
argument_list|)
expr_stmt|;
name|brokers
operator|.
name|add
argument_list|(
name|index
argument_list|,
name|broker
argument_list|)
expr_stmt|;
block|}
return|return
name|broker
return|;
block|}
else|else
block|{
return|return
operator|(
name|BrokerAgent
operator|)
name|brokerContext
operator|.
name|getBean
argument_list|(
literal|"broker"
operator|+
name|brokerCounter
argument_list|)
return|;
block|}
block|}
specifier|protected
name|void
name|addTestsFor
parameter_list|(
name|TestSuite
name|suite
parameter_list|,
name|ApplicationContext
name|clientContext
parameter_list|,
name|ApplicationContext
name|brokerContext
parameter_list|,
name|Constructor
name|constructor
parameter_list|,
name|Object
index|[]
name|parameters
parameter_list|,
name|String
name|destinationName
parameter_list|,
name|int
name|destinationType
parameter_list|)
throws|throws
name|Exception
block|{
name|Collection
name|values
init|=
name|clientContext
operator|.
name|getBeansOfType
argument_list|(
name|DestinationFactory
operator|.
name|class
argument_list|)
operator|.
name|values
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"we should at least one DestinationFactory in the ApplicationContext"
argument_list|,
name|values
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|values
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DestinationFactory
name|destinationFactory
init|=
operator|(
name|DestinationFactory
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Object
name|instance
init|=
name|constructor
operator|.
name|newInstance
argument_list|(
name|parameters
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Instance is not a Scenario: "
operator|+
name|instance
argument_list|,
name|instance
operator|instanceof
name|Scenario
argument_list|)
expr_stmt|;
name|Scenario
name|scenarioInstance
init|=
operator|(
name|Scenario
operator|)
name|instance
decl_stmt|;
name|String
name|testName
init|=
name|destinationDescription
argument_list|(
name|destinationType
argument_list|)
operator|+
literal|"."
operator|+
name|destinationName
decl_stmt|;
name|Destination
name|destination
init|=
name|destinationFactory
operator|.
name|createDestination
argument_list|(
name|testName
argument_list|,
name|destinationType
argument_list|)
decl_stmt|;
name|scenarioInstance
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|suite
operator|.
name|addTest
argument_list|(
operator|new
name|ScenarioTestCase
argument_list|(
name|scenarioInstance
argument_list|,
name|testName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
name|String
name|destinationDescription
parameter_list|(
name|int
name|destinationType
parameter_list|)
block|{
switch|switch
condition|(
name|destinationType
condition|)
block|{
case|case
name|DestinationFactory
operator|.
name|QUEUE
case|:
return|return
literal|"queue"
return|;
case|case
name|DestinationFactory
operator|.
name|TOPIC
case|:
return|return
literal|"topic"
return|;
default|default:
return|return
literal|"Unknown"
return|;
block|}
block|}
block|}
end_class

end_unit

