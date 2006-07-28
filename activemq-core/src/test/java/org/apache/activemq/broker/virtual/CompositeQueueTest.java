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
name|EmbeddedBrokerTestSupport
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
name|xbean
operator|.
name|XBeanBrokerFactory
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
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|CompositeQueueTest
extends|extends
name|EmbeddedBrokerTestSupport
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|CompositeQueueTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|public
name|void
name|testVirtualTopicCreation
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|connection
operator|==
literal|null
condition|)
block|{
name|connection
operator|=
name|createConnection
argument_list|()
expr_stmt|;
block|}
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|ConsumerBean
name|messageList1
init|=
operator|new
name|ConsumerBean
argument_list|()
decl_stmt|;
name|ConsumerBean
name|messageList2
init|=
operator|new
name|ConsumerBean
argument_list|()
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
name|getProducerDestination
argument_list|()
decl_stmt|;
name|Destination
name|destination1
init|=
name|getConsumer1Dsetination
argument_list|()
decl_stmt|;
name|Destination
name|destination2
init|=
name|getConsumer2Dsetination
argument_list|()
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Sending to: "
operator|+
name|producerDestination
argument_list|)
expr_stmt|;
name|log
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
name|int
name|total
init|=
literal|10
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
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"message: "
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|messageList1
operator|.
name|assertMessagesArrived
argument_list|(
name|total
argument_list|)
expr_stmt|;
name|messageList2
operator|.
name|assertMessagesArrived
argument_list|(
name|total
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Destination
name|getConsumer1Dsetination
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
literal|"FOO"
argument_list|)
return|;
block|}
specifier|protected
name|Destination
name|getConsumer2Dsetination
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQTopic
argument_list|(
literal|"BAR"
argument_list|)
return|;
block|}
specifier|protected
name|Destination
name|getProducerDestination
parameter_list|()
block|{
return|return
operator|new
name|ActiveMQQueue
argument_list|(
literal|"MY.QUEUE"
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
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
block|{
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|BrokerService
name|createBroker
parameter_list|()
throws|throws
name|Exception
block|{
name|XBeanBrokerFactory
name|factory
init|=
operator|new
name|XBeanBrokerFactory
argument_list|()
decl_stmt|;
name|BrokerService
name|answer
init|=
name|factory
operator|.
name|createBroker
argument_list|(
operator|new
name|URI
argument_list|(
name|getBrokerConfigUri
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// lets disable persistence as we are a test
name|answer
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|String
name|getBrokerConfigUri
parameter_list|()
block|{
return|return
literal|"org/apache/activemq/broker/virtual/composite-queue.xml"
return|;
block|}
block|}
end_class

end_unit

