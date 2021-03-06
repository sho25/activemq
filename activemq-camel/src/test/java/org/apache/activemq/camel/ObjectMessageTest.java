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
name|camel
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|ActiveMQConnectionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|Exchange
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|component
operator|.
name|jms
operator|.
name|JmsBinding
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|component
operator|.
name|jms
operator|.
name|JmsMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|component
operator|.
name|mock
operator|.
name|MockEndpoint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|test
operator|.
name|spring
operator|.
name|CamelSpringTestSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|camel
operator|.
name|util
operator|.
name|ExchangeHelper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xbean
operator|.
name|spring
operator|.
name|context
operator|.
name|ClassPathXmlApplicationContext
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

begin_import
import|import
name|org
operator|.
name|springframework
operator|.
name|context
operator|.
name|support
operator|.
name|AbstractApplicationContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
import|;
end_import

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

begin_class
specifier|public
class|class
name|ObjectMessageTest
extends|extends
name|CamelSpringTestSupport
block|{
annotation|@
name|Test
specifier|public
name|void
name|testUntrusted
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|Connection
name|conn
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|conn
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|sess
init|=
name|conn
operator|.
name|createSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|sess
operator|.
name|createProducer
argument_list|(
name|sess
operator|.
name|createTopic
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
decl_stmt|;
name|ObjectMessage
name|msg
init|=
name|sess
operator|.
name|createObjectMessage
argument_list|()
decl_stmt|;
name|ObjectPayload
name|payload
init|=
operator|new
name|ObjectPayload
argument_list|()
decl_stmt|;
name|payload
operator|.
name|payload
operator|=
literal|"test"
expr_stmt|;
name|msg
operator|.
name|setObject
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|MockEndpoint
name|resultActiveMQ
init|=
name|resolveMandatoryEndpoint
argument_list|(
literal|"mock:result-activemq"
argument_list|,
name|MockEndpoint
operator|.
name|class
argument_list|)
decl_stmt|;
name|resultActiveMQ
operator|.
name|expectedMessageCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|resultActiveMQ
operator|.
name|assertIsSatisfied
argument_list|()
expr_stmt|;
name|assertCorrectObjectReceived
argument_list|(
name|resultActiveMQ
argument_list|)
expr_stmt|;
name|MockEndpoint
name|resultTrusted
init|=
name|resolveMandatoryEndpoint
argument_list|(
literal|"mock:result-trusted"
argument_list|,
name|MockEndpoint
operator|.
name|class
argument_list|)
decl_stmt|;
name|resultTrusted
operator|.
name|expectedMessageCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|resultTrusted
operator|.
name|assertIsSatisfied
argument_list|()
expr_stmt|;
name|assertCorrectObjectReceived
argument_list|(
name|resultTrusted
argument_list|)
expr_stmt|;
name|MockEndpoint
name|resultCamel
init|=
name|resolveMandatoryEndpoint
argument_list|(
literal|"mock:result-camel"
argument_list|,
name|MockEndpoint
operator|.
name|class
argument_list|)
decl_stmt|;
name|resultCamel
operator|.
name|expectedMessageCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|resultCamel
operator|.
name|assertIsNotSatisfied
argument_list|()
expr_stmt|;
name|MockEndpoint
name|resultEmpty
init|=
name|resolveMandatoryEndpoint
argument_list|(
literal|"mock:result-empty"
argument_list|,
name|MockEndpoint
operator|.
name|class
argument_list|)
decl_stmt|;
name|resultEmpty
operator|.
name|expectedMessageCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|resultEmpty
operator|.
name|assertIsNotSatisfied
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|assertCorrectObjectReceived
parameter_list|(
name|MockEndpoint
name|result
parameter_list|)
block|{
name|Exchange
name|exchange
init|=
name|result
operator|.
name|getReceivedExchanges
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// This should be a JMS Exchange
name|assertNotNull
argument_list|(
name|ExchangeHelper
operator|.
name|getBinding
argument_list|(
name|exchange
argument_list|,
name|JmsBinding
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|JmsMessage
name|in
init|=
operator|(
name|JmsMessage
operator|)
name|exchange
operator|.
name|getIn
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|assertIsInstanceOf
argument_list|(
name|ObjectMessage
operator|.
name|class
argument_list|,
name|in
operator|.
name|getJmsMessage
argument_list|()
argument_list|)
expr_stmt|;
name|ObjectPayload
name|received
init|=
name|exchange
operator|.
name|getIn
argument_list|()
operator|.
name|getBody
argument_list|(
name|ObjectPayload
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|received
operator|.
name|payload
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|AbstractApplicationContext
name|createApplicationContext
parameter_list|()
block|{
name|AbstractApplicationContext
name|context
init|=
operator|new
name|ClassPathXmlApplicationContext
argument_list|(
literal|"org/apache/activemq/camel/jms-object-message.xml"
argument_list|)
decl_stmt|;
return|return
name|context
return|;
block|}
block|}
end_class

end_unit

