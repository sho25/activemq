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
name|broker
operator|.
name|virtual
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
name|broker
operator|.
name|BrokerService
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
name|broker
operator|.
name|region
operator|.
name|DestinationInterceptor
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
name|broker
operator|.
name|region
operator|.
name|virtual
operator|.
name|VirtualDestination
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
name|broker
operator|.
name|region
operator|.
name|virtual
operator|.
name|VirtualDestinationInterceptor
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
name|broker
operator|.
name|region
operator|.
name|virtual
operator|.
name|VirtualTopic
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
name|ActiveMQQueue
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
name|ActiveMQConnectionFactory
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
name|spring
operator|.
name|ConsumerBean
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
name|util
operator|.
name|Wait
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|MessageProducer
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
import|import static
name|junit
operator|.
name|framework
operator|.
name|TestCase
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|TestCase
operator|.
name|assertNotNull
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

begin_comment
comment|// https://issues.apache.org/jira/browse/AMQ-6643
end_comment

begin_class
specifier|public
class|class
name|VirtualTopicWildcardTest
block|{
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
name|VirtualTopicWildcardTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|int
name|total
init|=
literal|3
decl_stmt|;
specifier|protected
name|Connection
name|connection
decl_stmt|;
name|BrokerService
name|brokerService
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|brokerService
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|start
argument_list|()
expr_stmt|;
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|afer
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|brokerService
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWildcardAndSimpleConsumerShareMessages
parameter_list|()
throws|throws
name|Exception
block|{
name|ConsumerBean
name|messageList1
init|=
operator|new
name|ConsumerBean
argument_list|(
literal|"1:"
argument_list|)
decl_stmt|;
name|ConsumerBean
name|messageList2
init|=
operator|new
name|ConsumerBean
argument_list|(
literal|"2:"
argument_list|)
decl_stmt|;
name|ConsumerBean
name|messageList3
init|=
operator|new
name|ConsumerBean
argument_list|(
literal|"3:"
argument_list|)
decl_stmt|;
name|messageList1
operator|.
name|setVerbose
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|messageList2
operator|.
name|setVerbose
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|messageList3
operator|.
name|setVerbose
argument_list|(
literal|true
argument_list|)
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
name|Destination
name|producerDestination
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"VirtualTopic.TEST.A.IT"
argument_list|)
decl_stmt|;
name|Destination
name|destination1
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Consumer.1.VirtualTopic.TEST.>"
argument_list|)
decl_stmt|;
name|Destination
name|destination2
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Consumer.1.VirtualTopic.TEST.A.IT"
argument_list|)
decl_stmt|;
name|Destination
name|destination3
init|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Consumer.1.VirtualTopic.TEST.B.IT"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending to: "
operator|+
name|producerDestination
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Consuming from: "
operator|+
name|destination1
operator|+
literal|" and "
operator|+
name|destination2
operator|+
literal|", and "
operator|+
name|destination3
argument_list|)
expr_stmt|;
name|MessageConsumer
name|c1
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|MessageConsumer
name|c2
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination2
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// this consumer should get no messages
name|MessageConsumer
name|c3
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination3
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|c1
operator|.
name|setMessageListener
argument_list|(
name|messageList1
argument_list|)
expr_stmt|;
name|c2
operator|.
name|setMessageListener
argument_list|(
name|messageList2
argument_list|)
expr_stmt|;
name|c3
operator|.
name|setMessageListener
argument_list|(
name|messageList3
argument_list|)
expr_stmt|;
comment|// create topic producer
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|producerDestination
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|producer
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|total
condition|;
name|i
operator|++
control|)
block|{
name|producer
operator|.
name|send
argument_list|(
name|createMessage
argument_list|(
name|session
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertMessagesArrived
argument_list|(
name|messageList1
argument_list|,
name|messageList2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|messageList3
operator|.
name|getMessages
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Message
name|createMessage
parameter_list|(
name|Session
name|session
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"val="
operator|+
name|i
argument_list|)
return|;
block|}
specifier|private
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|brokerService
operator|.
name|getVmConnectorURI
argument_list|()
argument_list|)
decl_stmt|;
name|cf
operator|.
name|setWatchTopicAdvisories
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|cf
operator|.
name|createConnection
argument_list|()
return|;
block|}
specifier|protected
name|void
name|assertMessagesArrived
parameter_list|(
specifier|final
name|ConsumerBean
name|messageList1
parameter_list|,
specifier|final
name|ConsumerBean
name|messageList2
parameter_list|)
block|{
try|try
block|{
name|assertTrue
argument_list|(
literal|"expected"
argument_list|,
name|Wait
operator|.
name|waitFor
argument_list|(
operator|new
name|Wait
operator|.
name|Condition
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"One: "
operator|+
name|messageList1
operator|.
name|getMessages
argument_list|()
operator|.
name|size
argument_list|()
operator|+
literal|", Two:"
operator|+
name|messageList2
operator|.
name|getMessages
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|messageList1
operator|.
name|getMessages
argument_list|()
operator|.
name|size
argument_list|()
operator|+
name|messageList2
operator|.
name|getMessages
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|2
operator|*
name|total
return|;
block|}
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
name|broker
operator|.
name|setAdvisorySupport
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|VirtualTopic
name|virtualTopic
init|=
operator|new
name|VirtualTopic
argument_list|()
decl_stmt|;
name|VirtualDestinationInterceptor
name|interceptor
init|=
operator|new
name|VirtualDestinationInterceptor
argument_list|()
decl_stmt|;
name|interceptor
operator|.
name|setVirtualDestinations
argument_list|(
operator|new
name|VirtualDestination
index|[]
block|{
name|virtualTopic
block|}
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationInterceptors
argument_list|(
operator|new
name|DestinationInterceptor
index|[]
block|{
name|interceptor
block|}
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
block|}
end_class

end_unit

