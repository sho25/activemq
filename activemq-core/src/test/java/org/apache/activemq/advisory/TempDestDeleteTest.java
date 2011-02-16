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
name|advisory
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
name|ArrayBlockingQueue
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
name|BlockingQueue
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
name|MessageListener
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
name|region
operator|.
name|RegionBroker
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
name|ActiveMQTempQueue
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
name|ActiveMQTempTopic
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

begin_comment
comment|/**  *   */
end_comment

begin_class
specifier|public
class|class
name|TempDestDeleteTest
extends|extends
name|EmbeddedBrokerTestSupport
implements|implements
name|ConsumerListener
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
name|TempDestDeleteTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|int
name|consumerCounter
decl_stmt|;
specifier|protected
name|ConsumerEventSource
name|topicConsumerEventSource
decl_stmt|;
specifier|protected
name|BlockingQueue
argument_list|<
name|ConsumerEvent
argument_list|>
name|eventQueue
init|=
operator|new
name|ArrayBlockingQueue
argument_list|<
name|ConsumerEvent
argument_list|>
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
specifier|private
name|ConsumerEventSource
name|queueConsumerEventSource
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|Session
name|session
decl_stmt|;
specifier|private
name|ActiveMQTempTopic
name|tempTopic
decl_stmt|;
specifier|private
name|ActiveMQTempQueue
name|tempQueue
decl_stmt|;
specifier|public
name|void
name|testDeleteTempTopicDeletesAvisoryTopics
parameter_list|()
throws|throws
name|Exception
block|{
name|topicConsumerEventSource
operator|.
name|start
argument_list|()
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|createConsumer
argument_list|(
name|tempTopic
argument_list|)
decl_stmt|;
name|assertConsumerEvent
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Topic
name|advisoryTopic
init|=
name|AdvisorySupport
operator|.
name|getConsumerAdvisoryTopic
argument_list|(
name|tempTopic
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|destinationExists
argument_list|(
name|advisoryTopic
argument_list|)
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Once we delete the topic, the advisory topic for the destination
comment|// should also be deleted.
name|tempTopic
operator|.
name|delete
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|destinationExists
argument_list|(
name|advisoryTopic
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testDeleteTempQueueDeletesAvisoryTopics
parameter_list|()
throws|throws
name|Exception
block|{
name|queueConsumerEventSource
operator|.
name|start
argument_list|()
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|createConsumer
argument_list|(
name|tempQueue
argument_list|)
decl_stmt|;
name|assertConsumerEvent
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Topic
name|advisoryTopic
init|=
name|AdvisorySupport
operator|.
name|getConsumerAdvisoryTopic
argument_list|(
name|tempQueue
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|destinationExists
argument_list|(
name|advisoryTopic
argument_list|)
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Once we delete the queue, the advisory topic for the destination
comment|// should also be deleted.
name|tempQueue
operator|.
name|delete
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|destinationExists
argument_list|(
name|advisoryTopic
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|destinationExists
parameter_list|(
name|Destination
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
name|RegionBroker
name|rb
init|=
operator|(
name|RegionBroker
operator|)
name|broker
operator|.
name|getBroker
argument_list|()
operator|.
name|getAdaptor
argument_list|(
name|RegionBroker
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|rb
operator|.
name|getTopicRegion
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|containsKey
argument_list|(
name|dest
argument_list|)
operator|||
name|rb
operator|.
name|getQueueRegion
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|containsKey
argument_list|(
name|dest
argument_list|)
operator|||
name|rb
operator|.
name|getTempTopicRegion
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|containsKey
argument_list|(
name|dest
argument_list|)
operator|||
name|rb
operator|.
name|getTempQueueRegion
argument_list|()
operator|.
name|getDestinationMap
argument_list|()
operator|.
name|containsKey
argument_list|(
name|dest
argument_list|)
return|;
block|}
specifier|public
name|void
name|onConsumerEvent
parameter_list|(
name|ConsumerEvent
name|event
parameter_list|)
block|{
name|eventQueue
operator|.
name|add
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
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
name|session
operator|=
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
expr_stmt|;
name|tempTopic
operator|=
operator|(
name|ActiveMQTempTopic
operator|)
name|session
operator|.
name|createTemporaryTopic
argument_list|()
expr_stmt|;
name|topicConsumerEventSource
operator|=
operator|new
name|ConsumerEventSource
argument_list|(
name|connection
argument_list|,
name|tempTopic
argument_list|)
expr_stmt|;
name|topicConsumerEventSource
operator|.
name|setConsumerListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|tempQueue
operator|=
operator|(
name|ActiveMQTempQueue
operator|)
name|session
operator|.
name|createTemporaryQueue
argument_list|()
expr_stmt|;
name|queueConsumerEventSource
operator|=
operator|new
name|ConsumerEventSource
argument_list|(
name|connection
argument_list|,
name|tempQueue
argument_list|)
expr_stmt|;
name|queueConsumerEventSource
operator|.
name|setConsumerListener
argument_list|(
name|this
argument_list|)
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
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|assertConsumerEvent
parameter_list|(
name|int
name|count
parameter_list|,
name|boolean
name|started
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|ConsumerEvent
name|event
init|=
name|waitForConsumerEvent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Consumer count"
argument_list|,
name|count
argument_list|,
name|event
operator|.
name|getConsumerCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"started"
argument_list|,
name|started
argument_list|,
name|event
operator|.
name|isStarted
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|MessageConsumer
name|createConsumer
parameter_list|(
name|Destination
name|dest
parameter_list|)
throws|throws
name|JMSException
block|{
specifier|final
name|String
name|consumerText
init|=
literal|"Consumer: "
operator|+
operator|(
operator|++
name|consumerCounter
operator|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating consumer: "
operator|+
name|consumerText
operator|+
literal|" on destination: "
operator|+
name|dest
argument_list|)
expr_stmt|;
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Received message by: "
operator|+
name|consumerText
operator|+
literal|" message: "
operator|+
name|message
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|consumer
return|;
block|}
specifier|protected
name|ConsumerEvent
name|waitForConsumerEvent
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|ConsumerEvent
name|answer
init|=
name|eventQueue
operator|.
name|poll
argument_list|(
literal|1000
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Should have received a consumer event!"
argument_list|,
name|answer
operator|!=
literal|null
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
block|}
end_class

end_unit

