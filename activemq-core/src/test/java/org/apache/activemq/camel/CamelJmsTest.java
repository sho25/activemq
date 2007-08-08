begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|demo
operator|.
name|DefaultQueueSender
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
name|CamelTemplate
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
name|spring
operator|.
name|SpringTestSupport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|ClassPathXmlApplicationContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ConnectionFactory
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
name|javax
operator|.
name|jms
operator|.
name|Message
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ObjectMessage
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TextMessage
import|;
end_import

begin_comment
comment|/**  * @version $Revision: $  */
end_comment

begin_class
specifier|public
class|class
name|CamelJmsTest
extends|extends
name|SpringTestSupport
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|CamelJmsTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|expectedBody
init|=
literal|"<hello>world!</hello>"
decl_stmt|;
specifier|public
name|void
name|testSendingViaJmsIsReceivedByCamel
parameter_list|()
throws|throws
name|Exception
block|{
name|MockEndpoint
name|result
init|=
name|resolveMandatoryEndpoint
argument_list|(
literal|"mock:result"
argument_list|,
name|MockEndpoint
operator|.
name|class
argument_list|)
decl_stmt|;
name|result
operator|.
name|expectedBodiesReceived
argument_list|(
name|expectedBody
argument_list|)
expr_stmt|;
name|result
operator|.
name|message
argument_list|(
literal|0
argument_list|)
operator|.
name|header
argument_list|(
literal|"foo"
argument_list|)
operator|.
name|isEqualTo
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
comment|// lets create a message
name|Destination
name|destination
init|=
name|getMandatoryBean
argument_list|(
name|Destination
operator|.
name|class
argument_list|,
literal|"sendTo"
argument_list|)
decl_stmt|;
name|ConnectionFactory
name|factory
init|=
name|getMandatoryBean
argument_list|(
name|ConnectionFactory
operator|.
name|class
argument_list|,
literal|"connectionFactory"
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
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
name|session
operator|.
name|createProducer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
comment|// now lets send a message
name|ObjectMessage
name|message
init|=
name|session
operator|.
name|createObjectMessage
argument_list|(
name|expectedBody
argument_list|)
decl_stmt|;
name|message
operator|.
name|setStringProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|result
operator|.
name|assertIsSatisfied
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received message: "
operator|+
name|result
operator|.
name|getReceivedExchanges
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testConsumingViaJMSReceivesMessageFromCamel
parameter_list|()
throws|throws
name|Exception
block|{
comment|// lets create a message
name|Destination
name|destination
init|=
name|getMandatoryBean
argument_list|(
name|Destination
operator|.
name|class
argument_list|,
literal|"consumeFrom"
argument_list|)
decl_stmt|;
name|ConnectionFactory
name|factory
init|=
name|getMandatoryBean
argument_list|(
name|ConnectionFactory
operator|.
name|class
argument_list|,
literal|"connectionFactory"
argument_list|)
decl_stmt|;
name|CamelTemplate
name|template
init|=
name|getMandatoryBean
argument_list|(
name|CamelTemplate
operator|.
name|class
argument_list|,
literal|"camelTemplate"
argument_list|)
decl_stmt|;
name|Connection
name|connection
init|=
name|factory
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|Session
name|session
init|=
name|connection
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Consuming from: "
operator|+
name|destination
argument_list|)
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
decl_stmt|;
comment|// now lets send a message
name|template
operator|.
name|sendBody
argument_list|(
literal|"seda:consumer"
argument_list|,
name|expectedBody
argument_list|)
expr_stmt|;
name|Message
name|message
init|=
name|consumer
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
literal|"Should have received a message from destination: "
operator|+
name|destination
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|TextMessage
name|textMessage
init|=
name|assertIsInstanceOf
argument_list|(
name|TextMessage
operator|.
name|class
argument_list|,
name|message
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Message body"
argument_list|,
name|expectedBody
argument_list|,
name|textMessage
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received message: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|int
name|getExpectedRouteCount
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
specifier|protected
name|ClassPathXmlApplicationContext
name|createApplicationContext
parameter_list|()
block|{
return|return
operator|new
name|ClassPathXmlApplicationContext
argument_list|(
literal|"org/apache/activemq/camel/spring.xml"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

