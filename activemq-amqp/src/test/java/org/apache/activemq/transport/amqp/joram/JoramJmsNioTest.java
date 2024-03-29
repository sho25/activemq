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
name|transport
operator|.
name|amqp
operator|.
name|joram
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Suite
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|connection
operator|.
name|ConnectionTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|connection
operator|.
name|TopicConnectionTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|message
operator|.
name|MessageBodyTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|message
operator|.
name|MessageDefaultTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|message
operator|.
name|MessageTypeTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|message
operator|.
name|headers
operator|.
name|MessageHeaderTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|message
operator|.
name|properties
operator|.
name|JMSXPropertyTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|message
operator|.
name|properties
operator|.
name|MessagePropertyConversionTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|message
operator|.
name|properties
operator|.
name|MessagePropertyTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|queue
operator|.
name|QueueBrowserTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|queue
operator|.
name|TemporaryQueueTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|selector
operator|.
name|SelectorSyntaxTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|selector
operator|.
name|SelectorTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|session
operator|.
name|QueueSessionTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|session
operator|.
name|SessionTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|session
operator|.
name|TopicSessionTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|objectweb
operator|.
name|jtests
operator|.
name|jms
operator|.
name|conform
operator|.
name|topic
operator|.
name|TemporaryTopicTest
import|;
end_import

begin_comment
comment|/**  * Run the JoramJmsTests using amqp+nio  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Suite
operator|.
name|class
argument_list|)
annotation|@
name|Suite
operator|.
name|SuiteClasses
argument_list|(
block|{
name|TopicSessionTest
operator|.
name|class
block|,
name|MessageHeaderTest
operator|.
name|class
block|,
name|QueueBrowserTest
operator|.
name|class
block|,
name|MessageTypeTest
operator|.
name|class
block|,
name|TemporaryTopicTest
operator|.
name|class
block|,
name|TopicConnectionTest
operator|.
name|class
block|,
name|SelectorSyntaxTest
operator|.
name|class
block|,
name|QueueSessionTest
operator|.
name|class
block|,
name|SelectorTest
operator|.
name|class
block|,
name|TemporaryQueueTest
operator|.
name|class
block|,
name|ConnectionTest
operator|.
name|class
block|,
name|SessionTest
operator|.
name|class
block|,
name|JMSXPropertyTest
operator|.
name|class
block|,
name|MessageBodyTest
operator|.
name|class
block|,
name|MessageDefaultTest
operator|.
name|class
block|,
name|MessagePropertyConversionTest
operator|.
name|class
block|,
name|MessagePropertyTest
operator|.
name|class
block|}
argument_list|)
specifier|public
class|class
name|JoramJmsNioTest
block|{
annotation|@
name|Rule
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"joram.jms.test.file"
argument_list|,
name|getJmsTestFileName
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|String
name|getJmsTestFileName
parameter_list|()
block|{
return|return
literal|"providerNIO.properties"
return|;
block|}
block|}
end_class

end_unit

