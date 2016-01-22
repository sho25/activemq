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
import|import
name|javax
operator|.
name|jms
operator|.
name|InvalidSelectorException
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
name|ActiveMQTextMessage
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

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|SelectorTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testBooleanSelector
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"(trueProp OR falseProp) AND trueProp"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"(trueProp OR falseProp) AND falseProp"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"trueProp"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testXPathSelectors
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQTextMessage
name|message
init|=
operator|new
name|ActiveMQTextMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setJMSType
argument_list|(
literal|"xml"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setText
argument_list|(
literal|"<root><a key='first' num='1'/><b key='second' num='2'>b</b></root>"
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH 'root/a'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH '//root/b'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH 'root/c'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH '//root/b/text()=\"b\"'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH '//root/b=\"b\"'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH '//root/b=\"c\"'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH '//root/b!=\"c\"'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH '//root/*[@key=''second'']'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH '//root/*[@key=''third'']'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH '//root/a[@key=''first'']'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH '//root/a[@num=1]'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH '//root/a[@key=''second'']'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH '/root/*[@key=''first'' or @key=''third'']'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH '//root/*[@key=''third'' or @key=''forth'']'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH '/root/b=''b'' and /root/b[@key=''second'']'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH '/root/b=''b'' and /root/b[@key=''first'']'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH 'not(//root/a)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH 'not(//root/c)'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH '//root/a[not(@key=''first'')]'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH '//root/a[not(not(@key=''first''))]'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH 'string(//root/b)'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH 'string(//root/a)'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH 'sum(//@num)< 10'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH 'sum(//@num)> 10'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH '//root/a[@num> 1]'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"XPATH '//root/b[@num> 1]'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testJMSPropertySelectors
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
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
literal|"id:test:1:1:1:1"
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"JMSType = 'selector-test'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"JMSType = 'crap'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"JMSMessageID = 'id:test:1:1:1:1'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"JMSMessageID = 'id:not-test:1:1:1:1'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|message
operator|=
name|createMessage
argument_list|()
expr_stmt|;
name|message
operator|.
name|setJMSType
argument_list|(
literal|"1001"
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"JMSType='1001'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"JMSType='1001' OR JMSType='1002'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"JMSType = 'crap'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBasicSelectors
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"name = 'James'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"rank> 100"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"rank>= 123"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"rank>= 124"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPropertyTypes
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"byteProp = 123"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"byteProp = 10"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"byteProp2 = 33"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"byteProp2 = 10"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"shortProp = 123"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"shortProp = 10"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"shortProp = 123"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"shortProp = 10"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"intProp = 123"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"intProp = 10"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"longProp = 123"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"longProp = 10"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"floatProp = 123"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"floatProp = 10"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"doubleProp = 123"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"doubleProp = 10"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testAndSelectors
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"name = 'James' and rank< 200"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"name = 'James' and rank> 200"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"name = 'Foo' and rank< 200"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"unknown = 'Foo' and anotherUnknown< 200"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testOrSelectors
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"name = 'James' or rank< 200"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"name = 'James' or rank> 200"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"name = 'Foo' or rank< 200"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"name = 'Foo' or rank> 200"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"unknown = 'Foo' or anotherUnknown< 200"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testPlus
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"rank + 2 = 125"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"(rank + 2) = 125"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"125 = (rank + 2)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"rank + version = 125"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"rank + 2< 124"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"name + '!' = 'James!'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMinus
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"rank - 2 = 121"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"rank - version = 121"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"rank - 2> 122"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testMultiply
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"rank * 2 = 246"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"rank * version = 246"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"rank * 2< 130"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDivide
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"rank / version = 61.5"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"rank / 3> 100.0"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"rank / 3> 100"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"version / 2 = 1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBetween
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"rank between 100 and 150"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"rank between 10 and 120"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIn
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"name in ('James', 'Bob', 'Gromit')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"name in ('Bob', 'James', 'Gromit')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"name in ('Gromit', 'Bob', 'James')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"name in ('Gromit', 'Bob', 'Cheddar')"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"name not in ('Gromit', 'Bob', 'Cheddar')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIsNull
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"dummy is null"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"dummy is not null"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"name is not null"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"name is null"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testLike
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"modelClassId"
argument_list|,
literal|"com.whatever.something.foo.bar"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"modelInstanceId"
argument_list|,
literal|"170"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"modelRequestError"
argument_list|,
literal|"abc"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"modelCorrelatedClientId"
argument_list|,
literal|"whatever"
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"modelClassId LIKE 'com.whatever.something.%' AND modelInstanceId = '170' AND (modelRequestError IS NULL OR modelCorrelatedClientId = 'whatever')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"modelCorrelatedClientId"
argument_list|,
literal|"shouldFailNow"
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"modelClassId LIKE 'com.whatever.something.%' AND modelInstanceId = '170' AND (modelRequestError IS NULL OR modelCorrelatedClientId = 'whatever')"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|message
operator|=
name|createMessage
argument_list|()
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"modelClassId"
argument_list|,
literal|"com.whatever.something.foo.bar"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"modelInstanceId"
argument_list|,
literal|"170"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"modelCorrelatedClientId"
argument_list|,
literal|"shouldNotMatch"
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"modelClassId LIKE 'com.whatever.something.%' AND modelInstanceId = '170' AND (modelRequestError IS NULL OR modelCorrelatedClientId = 'whatever')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test cases from Mats Henricson      */
specifier|public
name|void
name|testMatsHenricsonUseCases
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"SessionserverId=1870414179"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|message
operator|.
name|setLongProperty
argument_list|(
literal|"SessionserverId"
argument_list|,
literal|1870414179
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"SessionserverId=1870414179"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|message
operator|.
name|setLongProperty
argument_list|(
literal|"SessionserverId"
argument_list|,
literal|1234
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"SessionserverId=1870414179"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"Command NOT IN ('MirrorLobbyRequest', 'MirrorLobbyReply')"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"Command"
argument_list|,
literal|"Cheese"
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"Command NOT IN ('MirrorLobbyRequest', 'MirrorLobbyReply')"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"Command"
argument_list|,
literal|"MirrorLobbyRequest"
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"Command NOT IN ('MirrorLobbyRequest', 'MirrorLobbyReply')"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"Command"
argument_list|,
literal|"MirrorLobbyReply"
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"Command NOT IN ('MirrorLobbyRequest', 'MirrorLobbyReply')"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testFloatComparisons
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"1.0< 1.1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"-1.1< 1.0"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"1.0E1< 1.1E1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"-1.1E1< 1.0E1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"1.< 1.1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"-1.1< 1."
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"1.E1< 1.1E1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"-1.1E1< 1.E1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|".1< .5"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"-.5< .1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|".1E1< .5E1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"-.5E1< .1E1"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"4E10< 5E10"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"5E8< 5E10"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"-4E10< 2E10"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"-5E8< 2E2"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"4E+10< 5E+10"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"4E-10< 5E-10"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testStringQuoteParsing
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"quote = '''In God We Trust'''"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testLikeComparisons
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"quote LIKE '''In G_d We Trust'''"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"quote LIKE '''In Gd_ We Trust'''"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"quote NOT LIKE '''In G_d We Trust'''"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"quote NOT LIKE '''In Gd_ We Trust'''"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"foo LIKE '%oo'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"foo LIKE '%ar'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"foo NOT LIKE '%oo'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"foo NOT LIKE '%ar'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"foo LIKE '!_%' ESCAPE '!'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"quote LIKE '!_%' ESCAPE '!'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"foo NOT LIKE '!_%' ESCAPE '!'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"quote NOT LIKE '!_%' ESCAPE '!'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"punctuation LIKE '!#$&()*+,-./:;<=>?@[\\]^`{|}~'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSpecialEscapeLiteral
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"foo LIKE '%_%' ESCAPE '%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"endingUnderScore LIKE '_D7xlJIQn$_' ESCAPE '$'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"endingUnderScore LIKE '_D7xlJIQn__' ESCAPE '_'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"endingUnderScore LIKE '%D7xlJIQn%_' ESCAPE '%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"endingUnderScore LIKE '%D7xlJIQn%'  ESCAPE '%'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// literal '%' at the end, no match
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"endingUnderScore LIKE '%D7xlJIQn%%'  ESCAPE '%'"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"endingUnderScore LIKE '_D7xlJIQn\\_' ESCAPE '\\'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"endingUnderScore LIKE '%D7xlJIQn\\_' ESCAPE '\\'"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testInvalidSelector
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertInvalidSelector
argument_list|(
name|message
argument_list|,
literal|"3+5"
argument_list|)
expr_stmt|;
name|assertInvalidSelector
argument_list|(
name|message
argument_list|,
literal|"True AND 3+5"
argument_list|)
expr_stmt|;
name|assertInvalidSelector
argument_list|(
name|message
argument_list|,
literal|"=TEST 'test'"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testFunctionSelector
parameter_list|()
throws|throws
name|Exception
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|()
decl_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"REGEX('1870414179', SessionserverId)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|message
operator|.
name|setLongProperty
argument_list|(
literal|"SessionserverId"
argument_list|,
literal|1870414179
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"REGEX('1870414179', SessionserverId)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"REGEX('[0-9]*', SessionserverId)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"REGEX('^[1-8]*$', SessionserverId)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"REGEX('^[1-8]*$', SessionserverId)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"INLIST(SPLIT('Tom,Dick,George',','), name)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"INLIST(SPLIT('Tom,James,George',','), name)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"INLIST(MAKELIST('Tom','Dick','George'), name)"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"INLIST(MAKELIST('Tom','James','George'), name)"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertSelector
argument_list|(
name|message
argument_list|,
literal|"REGEX('connection1111', REPLACE(JMSMessageID,':',''))"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Message
name|createMessage
parameter_list|()
throws|throws
name|JMSException
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|(
literal|"FOO.BAR"
argument_list|)
decl_stmt|;
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
name|setObjectProperty
argument_list|(
literal|"name"
argument_list|,
literal|"James"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setObjectProperty
argument_list|(
literal|"location"
argument_list|,
literal|"London"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setByteProperty
argument_list|(
literal|"byteProp"
argument_list|,
operator|(
name|byte
operator|)
literal|123
argument_list|)
expr_stmt|;
name|message
operator|.
name|setByteProperty
argument_list|(
literal|"byteProp2"
argument_list|,
operator|(
name|byte
operator|)
literal|33
argument_list|)
expr_stmt|;
name|message
operator|.
name|setShortProperty
argument_list|(
literal|"shortProp"
argument_list|,
operator|(
name|short
operator|)
literal|123
argument_list|)
expr_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"intProp"
argument_list|,
literal|123
argument_list|)
expr_stmt|;
name|message
operator|.
name|setLongProperty
argument_list|(
literal|"longProp"
argument_list|,
literal|123
argument_list|)
expr_stmt|;
name|message
operator|.
name|setFloatProperty
argument_list|(
literal|"floatProp"
argument_list|,
literal|123
argument_list|)
expr_stmt|;
name|message
operator|.
name|setDoubleProperty
argument_list|(
literal|"doubleProp"
argument_list|,
literal|123
argument_list|)
expr_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"rank"
argument_list|,
literal|123
argument_list|)
expr_stmt|;
name|message
operator|.
name|setIntProperty
argument_list|(
literal|"version"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"quote"
argument_list|,
literal|"'In God We Trust'"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"_foo"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"punctuation"
argument_list|,
literal|"!#$&()*+,-./:;<=>?@[\\]^`{|}~"
argument_list|)
expr_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"endingUnderScore"
argument_list|,
literal|"XD7xlJIQn_"
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
return|return
name|message
return|;
block|}
specifier|protected
name|void
name|assertInvalidSelector
parameter_list|(
name|Message
name|message
parameter_list|,
name|String
name|text
parameter_list|)
throws|throws
name|JMSException
block|{
try|try
block|{
name|SelectorParser
operator|.
name|parse
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Created a valid selector"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InvalidSelectorException
name|e
parameter_list|)
block|{         }
block|}
specifier|protected
name|void
name|assertSelector
parameter_list|(
name|Message
name|message
parameter_list|,
name|String
name|text
parameter_list|,
name|boolean
name|expected
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
name|expected
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Message
name|createMessage
parameter_list|(
name|String
name|subject
parameter_list|)
throws|throws
name|JMSException
block|{
name|ActiveMQMessage
name|message
init|=
operator|new
name|ActiveMQMessage
argument_list|()
decl_stmt|;
name|message
operator|.
name|setJMSDestination
argument_list|(
operator|new
name|ActiveMQTopic
argument_list|(
name|subject
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|message
return|;
block|}
block|}
end_class

end_unit

