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
name|policy
package|;
end_package

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
name|DeliveryMode
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
name|Queue
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueBrowser
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
name|javax
operator|.
name|jms
operator|.
name|Topic
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
name|TestSupport
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
name|broker
operator|.
name|region
operator|.
name|policy
operator|.
name|DeadLetterStrategy
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
name|policy
operator|.
name|PolicyEntry
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
name|policy
operator|.
name|PolicyMap
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

begin_comment
comment|/**  * @version $Revision$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|DeadLetterTestSupport
extends|extends
name|TestSupport
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
name|DeadLetterTestSupport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|int
name|messageCount
init|=
literal|10
decl_stmt|;
specifier|protected
name|long
name|timeToLive
decl_stmt|;
specifier|protected
name|Connection
name|connection
decl_stmt|;
specifier|protected
name|Session
name|session
decl_stmt|;
specifier|protected
name|MessageConsumer
name|consumer
decl_stmt|;
specifier|protected
name|MessageProducer
name|producer
decl_stmt|;
specifier|protected
name|int
name|deliveryMode
init|=
name|DeliveryMode
operator|.
name|PERSISTENT
decl_stmt|;
specifier|protected
name|boolean
name|durableSubscriber
decl_stmt|;
specifier|protected
name|Destination
name|dlqDestination
decl_stmt|;
specifier|protected
name|MessageConsumer
name|dlqConsumer
decl_stmt|;
specifier|protected
name|QueueBrowser
name|dlqBrowser
decl_stmt|;
specifier|protected
name|BrokerService
name|broker
decl_stmt|;
specifier|protected
name|boolean
name|transactedMode
decl_stmt|;
specifier|protected
name|int
name|acknowledgeMode
init|=
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
decl_stmt|;
specifier|private
name|Destination
name|destination
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
name|broker
operator|=
name|createBroker
argument_list|()
expr_stmt|;
name|broker
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
name|setClientID
argument_list|(
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|=
name|connection
operator|.
name|createSession
argument_list|(
name|transactedMode
argument_list|,
name|acknowledgeMode
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
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
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
specifier|abstract
name|void
name|doTest
parameter_list|()
throws|throws
name|Exception
function_decl|;
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
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|PolicyEntry
name|policy
init|=
operator|new
name|PolicyEntry
argument_list|()
decl_stmt|;
name|DeadLetterStrategy
name|defaultDeadLetterStrategy
init|=
name|policy
operator|.
name|getDeadLetterStrategy
argument_list|()
decl_stmt|;
if|if
condition|(
name|defaultDeadLetterStrategy
operator|!=
literal|null
condition|)
block|{
name|defaultDeadLetterStrategy
operator|.
name|setProcessNonPersistent
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|PolicyMap
name|pMap
init|=
operator|new
name|PolicyMap
argument_list|()
decl_stmt|;
name|pMap
operator|.
name|setDefaultEntry
argument_list|(
name|policy
argument_list|)
expr_stmt|;
name|broker
operator|.
name|setDestinationPolicy
argument_list|(
name|pMap
argument_list|)
expr_stmt|;
return|return
name|broker
return|;
block|}
specifier|protected
name|void
name|makeConsumer
parameter_list|()
throws|throws
name|JMSException
block|{
name|Destination
name|destination
init|=
name|getDestination
argument_list|()
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
if|if
condition|(
name|durableSubscriber
condition|)
block|{
name|consumer
operator|=
name|session
operator|.
name|createDurableSubscriber
argument_list|(
operator|(
name|Topic
operator|)
name|destination
argument_list|,
name|destination
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|destination
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|makeDlqConsumer
parameter_list|()
throws|throws
name|JMSException
block|{
name|dlqDestination
operator|=
name|createDlqDestination
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Consuming from dead letter on: "
operator|+
name|dlqDestination
argument_list|)
expr_stmt|;
name|dlqConsumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|dlqDestination
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|makeDlqBrowser
parameter_list|()
throws|throws
name|JMSException
block|{
name|dlqDestination
operator|=
name|createDlqDestination
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Browsing dead letter on: "
operator|+
name|dlqDestination
argument_list|)
expr_stmt|;
name|dlqBrowser
operator|=
name|session
operator|.
name|createBrowser
argument_list|(
operator|(
name|Queue
operator|)
name|dlqDestination
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|sendMessages
parameter_list|()
throws|throws
name|JMSException
block|{
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
name|producer
operator|=
name|session
operator|.
name|createProducer
argument_list|(
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|deliveryMode
argument_list|)
expr_stmt|;
name|producer
operator|.
name|setTimeToLive
argument_list|(
name|timeToLive
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sending "
operator|+
name|messageCount
operator|+
literal|" messages to: "
operator|+
name|getDestination
argument_list|()
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
name|messageCount
condition|;
name|i
operator|++
control|)
block|{
name|Message
name|message
init|=
name|createMessage
argument_list|(
name|session
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|TextMessage
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
name|getMessageText
argument_list|(
name|i
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|String
name|getMessageText
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
literal|"message: "
operator|+
name|i
return|;
block|}
specifier|protected
name|void
name|assertMessage
parameter_list|(
name|Message
name|message
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received message: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"No message received for index: "
operator|+
name|i
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Should be a TextMessage not: "
operator|+
name|message
argument_list|,
name|message
operator|instanceof
name|TextMessage
argument_list|)
expr_stmt|;
name|TextMessage
name|textMessage
init|=
operator|(
name|TextMessage
operator|)
name|message
decl_stmt|;
name|assertEquals
argument_list|(
literal|"text of message: "
operator|+
name|i
argument_list|,
name|getMessageText
argument_list|(
name|i
argument_list|)
argument_list|,
name|textMessage
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|Destination
name|createDlqDestination
parameter_list|()
function_decl|;
specifier|public
name|void
name|testTransientTopicMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|topic
operator|=
literal|true
expr_stmt|;
name|deliveryMode
operator|=
name|DeliveryMode
operator|.
name|NON_PERSISTENT
expr_stmt|;
name|durableSubscriber
operator|=
literal|true
expr_stmt|;
name|doTest
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testDurableTopicMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|topic
operator|=
literal|true
expr_stmt|;
name|deliveryMode
operator|=
name|DeliveryMode
operator|.
name|PERSISTENT
expr_stmt|;
name|durableSubscriber
operator|=
literal|true
expr_stmt|;
name|doTest
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testTransientQueueMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|topic
operator|=
literal|false
expr_stmt|;
name|deliveryMode
operator|=
name|DeliveryMode
operator|.
name|NON_PERSISTENT
expr_stmt|;
name|durableSubscriber
operator|=
literal|false
expr_stmt|;
name|doTest
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testDurableQueueMessage
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|topic
operator|=
literal|false
expr_stmt|;
name|deliveryMode
operator|=
name|DeliveryMode
operator|.
name|PERSISTENT
expr_stmt|;
name|durableSubscriber
operator|=
literal|false
expr_stmt|;
name|doTest
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Destination
name|getDestination
parameter_list|()
block|{
if|if
condition|(
name|destination
operator|==
literal|null
condition|)
block|{
name|destination
operator|=
name|createDestination
argument_list|()
expr_stmt|;
block|}
return|return
name|destination
return|;
block|}
block|}
end_class

end_unit

