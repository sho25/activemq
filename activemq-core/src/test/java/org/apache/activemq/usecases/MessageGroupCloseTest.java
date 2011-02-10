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
name|usecases
package|;
end_package

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
name|javax
operator|.
name|jms
operator|.
name|*
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
name|java
operator|.
name|util
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
name|CountDownLatch
import|;
end_import

begin_comment
comment|/*  * Test plan:  * Producer: publish messages into a queue, with 10 message groups, closing the group with seq=-1 on message 5 and message 10  * Consumers: 2 consumers created after all messages are sent  *  * Expected: for each group, messages 1-5 are handled by one consumer and messages 6-10 are handled by the other consumer.  Messages  * 1 and 6 have the JMSXGroupFirstForConsumer property set to true.  */
end_comment

begin_class
specifier|public
class|class
name|MessageGroupCloseTest
extends|extends
name|TestCase
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
name|MessageGroupNewConsumerTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
comment|// Released after all messages are created
specifier|private
name|CountDownLatch
name|latchMessagesCreated
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
name|int
name|messagesSent
decl_stmt|,
name|messagesRecvd1
decl_stmt|,
name|messagesRecvd2
decl_stmt|,
name|messageGroupCount
decl_stmt|,
name|errorCountFirstForConsumer
decl_stmt|,
name|errorCountWrongConsumerClose
decl_stmt|,
name|errorCountDuplicateClose
decl_stmt|;
comment|// groupID, count
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|messageGroups1
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|messageGroups2
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|HashSet
argument_list|<
name|String
argument_list|>
name|closedGroups1
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|HashSet
argument_list|<
name|String
argument_list|>
name|closedGroups2
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// with the prefetch too high, this bug is not realized
specifier|private
specifier|static
specifier|final
name|String
name|connStr
init|=
comment|//"tcp://localhost:61616";
literal|"vm://localhost?broker.persistent=false&broker.useJmx=false&jms.prefetchPolicy.all=1"
decl_stmt|;
specifier|public
name|void
name|testNewConsumer
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|connStr
argument_list|)
decl_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|String
name|queueName
init|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
specifier|final
name|Thread
name|producerThread
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
name|MessageProducer
name|prod
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|10
condition|;
name|j
operator|++
control|)
block|{
name|int
name|seq
init|=
name|j
operator|+
literal|1
decl_stmt|;
if|if
condition|(
operator|(
name|j
operator|+
literal|1
operator|)
operator|%
literal|5
operator|==
literal|0
condition|)
block|{
name|seq
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|Message
name|message
init|=
name|generateMessage
argument_list|(
name|session
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
name|seq
argument_list|)
decl_stmt|;
name|prod
operator|.
name|send
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|messagesSent
operator|++
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Sent message: group="
operator|+
name|i
operator|+
literal|", seq="
operator|+
name|seq
argument_list|)
expr_stmt|;
comment|//Thread.sleep(20);
block|}
if|if
condition|(
name|i
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Sent messages: group="
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|messageGroupCount
operator|++
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|messagesSent
operator|+
literal|" messages sent"
argument_list|)
expr_stmt|;
name|latchMessagesCreated
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|prod
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Producer failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
specifier|final
name|Thread
name|consumerThread1
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|latchMessagesCreated
operator|.
name|await
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"starting consumer1"
argument_list|)
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
name|MessageConsumer
name|con1
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Message
name|message
init|=
name|con1
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
break|break;
name|LOG
operator|.
name|info
argument_list|(
literal|"Con1: got message "
operator|+
name|formatMessage
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
name|checkMessage
argument_list|(
name|message
argument_list|,
literal|"Con1"
argument_list|,
name|messageGroups1
argument_list|,
name|closedGroups1
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|messagesRecvd1
operator|++
expr_stmt|;
if|if
condition|(
name|messagesRecvd1
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Con1: got messages count="
operator|+
name|messagesRecvd1
argument_list|)
expr_stmt|;
block|}
comment|//Thread.sleep(50);
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Con1: total messages="
operator|+
name|messagesRecvd1
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Con1: total message groups="
operator|+
name|messageGroups1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|con1
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Consumer 1 failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
specifier|final
name|Thread
name|consumerThread2
init|=
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|latchMessagesCreated
operator|.
name|await
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"starting consumer2"
argument_list|)
expr_stmt|;
name|Session
name|session
init|=
name|connection
operator|.
name|createSession
argument_list|(
literal|true
argument_list|,
name|Session
operator|.
name|SESSION_TRANSACTED
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
name|MessageConsumer
name|con2
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|Message
name|message
init|=
name|con2
operator|.
name|receive
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Con2: got message "
operator|+
name|formatMessage
argument_list|(
name|message
argument_list|)
argument_list|)
expr_stmt|;
name|checkMessage
argument_list|(
name|message
argument_list|,
literal|"Con2"
argument_list|,
name|messageGroups2
argument_list|,
name|closedGroups2
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
name|messagesRecvd2
operator|++
expr_stmt|;
if|if
condition|(
name|messagesRecvd2
operator|%
literal|100
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Con2: got messages count="
operator|+
name|messagesRecvd2
argument_list|)
expr_stmt|;
block|}
comment|//Thread.sleep(50);
block|}
name|con2
operator|.
name|close
argument_list|()
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Con2: total messages="
operator|+
name|messagesRecvd2
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Con2: total message groups="
operator|+
name|messageGroups2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Consumer 2 failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
decl_stmt|;
name|consumerThread2
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumerThread1
operator|.
name|start
argument_list|()
expr_stmt|;
name|producerThread
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// wait for threads to finish
name|producerThread
operator|.
name|join
argument_list|()
expr_stmt|;
name|consumerThread1
operator|.
name|join
argument_list|()
expr_stmt|;
name|consumerThread2
operator|.
name|join
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// check results
name|assertEquals
argument_list|(
literal|"consumers should get all the messages"
argument_list|,
name|messagesSent
argument_list|,
name|messagesRecvd1
operator|+
name|messagesRecvd2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"not all message groups closed for consumer 1"
argument_list|,
name|messageGroups1
operator|.
name|size
argument_list|()
argument_list|,
name|closedGroups1
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"not all message groups closed for consumer 2"
argument_list|,
name|messageGroups2
operator|.
name|size
argument_list|()
argument_list|,
name|closedGroups2
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"producer failed to send any messages"
argument_list|,
name|messagesSent
operator|>
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"JMSXGroupFirstForConsumer not set"
argument_list|,
literal|0
argument_list|,
name|errorCountFirstForConsumer
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong consumer got close message"
argument_list|,
literal|0
argument_list|,
name|errorCountWrongConsumerClose
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"consumer got duplicate close message"
argument_list|,
literal|0
argument_list|,
name|errorCountDuplicateClose
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Message
name|generateMessage
parameter_list|(
name|Session
name|session
parameter_list|,
name|String
name|groupId
parameter_list|,
name|int
name|seq
parameter_list|)
throws|throws
name|JMSException
block|{
name|TextMessage
name|m
init|=
name|session
operator|.
name|createTextMessage
argument_list|()
decl_stmt|;
name|m
operator|.
name|setJMSType
argument_list|(
literal|"TEST_MESSAGE"
argument_list|)
expr_stmt|;
name|m
operator|.
name|setStringProperty
argument_list|(
literal|"JMSXGroupID"
argument_list|,
name|groupId
argument_list|)
expr_stmt|;
name|m
operator|.
name|setIntProperty
argument_list|(
literal|"JMSXGroupSeq"
argument_list|,
name|seq
argument_list|)
expr_stmt|;
name|m
operator|.
name|setText
argument_list|(
literal|"<?xml?><testMessage/>"
argument_list|)
expr_stmt|;
return|return
name|m
return|;
block|}
specifier|public
name|String
name|formatMessage
parameter_list|(
name|Message
name|m
parameter_list|)
block|{
try|try
block|{
return|return
literal|"group="
operator|+
name|m
operator|.
name|getStringProperty
argument_list|(
literal|"JMSXGroupID"
argument_list|)
operator|+
literal|", seq="
operator|+
name|m
operator|.
name|getIntProperty
argument_list|(
literal|"JMSXGroupSeq"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
return|;
block|}
block|}
specifier|public
name|void
name|checkMessage
parameter_list|(
name|Message
name|m
parameter_list|,
name|String
name|consumerId
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|messageGroups
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|closedGroups
parameter_list|)
throws|throws
name|JMSException
block|{
name|String
name|groupId
init|=
name|m
operator|.
name|getStringProperty
argument_list|(
literal|"JMSXGroupID"
argument_list|)
decl_stmt|;
name|int
name|seq
init|=
name|m
operator|.
name|getIntProperty
argument_list|(
literal|"JMSXGroupSeq"
argument_list|)
decl_stmt|;
name|Integer
name|count
init|=
name|messageGroups
operator|.
name|get
argument_list|(
name|groupId
argument_list|)
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|null
condition|)
block|{
comment|// first time seeing this group
if|if
condition|(
operator|!
name|m
operator|.
name|propertyExists
argument_list|(
literal|"JMSXGroupFirstForConsumer"
argument_list|)
operator|||
operator|!
name|m
operator|.
name|getBooleanProperty
argument_list|(
literal|"JMSXGroupFirstForConsumer"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|consumerId
operator|+
literal|": JMSXGroupFirstForConsumer not set for group="
operator|+
name|groupId
operator|+
literal|", seq="
operator|+
name|seq
argument_list|)
expr_stmt|;
name|errorCountFirstForConsumer
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|seq
operator|==
operator|-
literal|1
condition|)
block|{
name|closedGroups
operator|.
name|add
argument_list|(
name|groupId
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|consumerId
operator|+
literal|": wrong consumer got close message for group="
operator|+
name|groupId
argument_list|)
expr_stmt|;
name|errorCountWrongConsumerClose
operator|++
expr_stmt|;
block|}
name|messageGroups
operator|.
name|put
argument_list|(
name|groupId
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// existing group
if|if
condition|(
name|closedGroups
operator|.
name|contains
argument_list|(
name|groupId
argument_list|)
condition|)
block|{
comment|// group reassigned to same consumer
name|closedGroups
operator|.
name|remove
argument_list|(
name|groupId
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|m
operator|.
name|propertyExists
argument_list|(
literal|"JMSXGroupFirstForConsumer"
argument_list|)
operator|||
operator|!
name|m
operator|.
name|getBooleanProperty
argument_list|(
literal|"JMSXGroupFirstForConsumer"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|consumerId
operator|+
literal|": JMSXGroupFirstForConsumer not set for group="
operator|+
name|groupId
operator|+
literal|", seq="
operator|+
name|seq
argument_list|)
expr_stmt|;
name|errorCountFirstForConsumer
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|seq
operator|==
operator|-
literal|1
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|consumerId
operator|+
literal|": consumer got duplicate close message for group="
operator|+
name|groupId
argument_list|)
expr_stmt|;
name|errorCountDuplicateClose
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|seq
operator|==
operator|-
literal|1
condition|)
block|{
name|closedGroups
operator|.
name|add
argument_list|(
name|groupId
argument_list|)
expr_stmt|;
block|}
name|messageGroups
operator|.
name|put
argument_list|(
name|groupId
argument_list|,
name|count
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

