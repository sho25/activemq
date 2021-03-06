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
name|network
operator|.
name|jms
package|;
end_package

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
name|MessageListener
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
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueRequestor
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueSession
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
name|ActiveMQConnectionFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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

begin_class
specifier|public
class|class
name|QueueBridgeTest
extends|extends
name|TestCase
implements|implements
name|MessageListener
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|MESSAGE_COUNT
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|QueueBridgeTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|AbstractApplicationContext
name|context
decl_stmt|;
specifier|protected
name|QueueConnection
name|localConnection
decl_stmt|;
specifier|protected
name|QueueConnection
name|remoteConnection
decl_stmt|;
specifier|protected
name|QueueRequestor
name|requestor
decl_stmt|;
specifier|protected
name|QueueSession
name|requestServerSession
decl_stmt|;
specifier|protected
name|MessageConsumer
name|requestServerConsumer
decl_stmt|;
specifier|protected
name|MessageProducer
name|requestServerProducer
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|context
operator|=
name|createApplicationContext
argument_list|()
expr_stmt|;
name|createConnections
argument_list|()
expr_stmt|;
name|requestServerSession
operator|=
name|localConnection
operator|.
name|createQueueSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
expr_stmt|;
name|Queue
name|theQueue
init|=
name|requestServerSession
operator|.
name|createQueue
argument_list|(
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|requestServerConsumer
operator|=
name|requestServerSession
operator|.
name|createConsumer
argument_list|(
name|theQueue
argument_list|)
expr_stmt|;
name|requestServerConsumer
operator|.
name|setMessageListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|requestServerProducer
operator|=
name|requestServerSession
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|QueueSession
name|session
init|=
name|remoteConnection
operator|.
name|createQueueSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|requestor
operator|=
operator|new
name|QueueRequestor
argument_list|(
name|session
argument_list|,
name|theQueue
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|createConnections
parameter_list|()
throws|throws
name|JMSException
block|{
name|ActiveMQConnectionFactory
name|fac
init|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|context
operator|.
name|getBean
argument_list|(
literal|"localFactory"
argument_list|)
decl_stmt|;
name|localConnection
operator|=
name|fac
operator|.
name|createQueueConnection
argument_list|()
expr_stmt|;
name|localConnection
operator|.
name|start
argument_list|()
expr_stmt|;
name|fac
operator|=
operator|(
name|ActiveMQConnectionFactory
operator|)
name|context
operator|.
name|getBean
argument_list|(
literal|"remoteFactory"
argument_list|)
expr_stmt|;
name|remoteConnection
operator|=
name|fac
operator|.
name|createQueueConnection
argument_list|()
expr_stmt|;
name|remoteConnection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|AbstractApplicationContext
name|createApplicationContext
parameter_list|()
block|{
return|return
operator|new
name|ClassPathXmlApplicationContext
argument_list|(
literal|"org/apache/activemq/network/jms/queue-config.xml"
argument_list|)
return|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|localConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testQueueRequestorOverBridge
parameter_list|()
throws|throws
name|JMSException
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
name|MESSAGE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|TextMessage
name|msg
init|=
name|requestServerSession
operator|.
name|createTextMessage
argument_list|(
literal|"test msg: "
operator|+
name|i
argument_list|)
decl_stmt|;
name|TextMessage
name|result
init|=
operator|(
name|TextMessage
operator|)
name|requestor
operator|.
name|request
argument_list|(
name|msg
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|result
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
block|{
try|try
block|{
name|TextMessage
name|textMsg
init|=
operator|(
name|TextMessage
operator|)
name|msg
decl_stmt|;
name|String
name|payload
init|=
literal|"REPLY: "
operator|+
name|textMsg
operator|.
name|getText
argument_list|()
decl_stmt|;
name|Destination
name|replyTo
decl_stmt|;
name|replyTo
operator|=
name|msg
operator|.
name|getJMSReplyTo
argument_list|()
expr_stmt|;
name|textMsg
operator|.
name|clearBody
argument_list|()
expr_stmt|;
name|textMsg
operator|.
name|setText
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|requestServerProducer
operator|.
name|send
argument_list|(
name|replyTo
argument_list|,
name|textMsg
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

