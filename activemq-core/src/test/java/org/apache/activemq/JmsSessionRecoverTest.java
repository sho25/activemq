begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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

begin_comment
comment|/**  * Testcases to see if Session.recover() work.  * @version $Revision: 1.3 $  */
end_comment

begin_class
specifier|public
class|class
name|JmsSessionRecoverTest
extends|extends
name|TestCase
block|{
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|ActiveMQConnectionFactory
name|factory
decl_stmt|;
specifier|private
name|Destination
name|dest
decl_stmt|;
comment|/**      * @see junit.framework.TestCase#setUp()      */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|factory
operator|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
literal|"vm://localhost?broker.persistent=false"
argument_list|)
expr_stmt|;
name|connection
operator|=
name|factory
operator|.
name|createConnection
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see junit.framework.TestCase#tearDown()      */
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
name|connection
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**      *      * @throws JMSException      * @throws InterruptedException      */
specifier|public
name|void
name|testQueueSynchRecover
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
name|dest
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Queue-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|doTestSynchRecover
argument_list|()
expr_stmt|;
block|}
comment|/**      *      * @throws JMSException      * @throws InterruptedException      */
specifier|public
name|void
name|testQueueAsynchRecover
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
name|dest
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Queue-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|doTestAsynchRecover
argument_list|()
expr_stmt|;
block|}
comment|/**      *      * @throws JMSException      * @throws InterruptedException      */
specifier|public
name|void
name|testTopicSynchRecover
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
name|dest
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"Topic-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|doTestSynchRecover
argument_list|()
expr_stmt|;
block|}
comment|/**      *      * @throws JMSException      * @throws InterruptedException      */
specifier|public
name|void
name|testTopicAsynchRecover
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
name|dest
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"Topic-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|doTestAsynchRecover
argument_list|()
expr_stmt|;
block|}
comment|/**      *      * @throws JMSException      * @throws InterruptedException      */
specifier|public
name|void
name|testQueueAsynchRecoverWithAutoAck
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
name|dest
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
literal|"Queue-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|doTestAsynchRecoverWithAutoAck
argument_list|()
expr_stmt|;
block|}
comment|/**      *      * @throws JMSException      * @throws InterruptedException      */
specifier|public
name|void
name|testTopicAsynchRecoverWithAutoAck
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
name|dest
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"Topic-"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
name|doTestAsynchRecoverWithAutoAck
argument_list|()
expr_stmt|;
block|}
comment|/**      * Test to make sure that a Sync recover works.      *       * @throws JMSException      */
specifier|public
name|void
name|doTestSynchRecover
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
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
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"First"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Second"
argument_list|)
argument_list|)
expr_stmt|;
name|TextMessage
name|message
init|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"First"
argument_list|,
name|message
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|message
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|message
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Second"
argument_list|,
name|message
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|message
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|recover
argument_list|()
expr_stmt|;
name|message
operator|=
operator|(
name|TextMessage
operator|)
name|consumer
operator|.
name|receive
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Second"
argument_list|,
name|message
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|message
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
block|}
comment|/**      * Test to make sure that a Async recover works.      *       * @throws JMSException      * @throws InterruptedException      */
specifier|public
name|void
name|doTestAsynchRecover
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
specifier|final
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
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
specifier|final
name|String
name|errorMessage
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|null
block|}
decl_stmt|;
specifier|final
name|CountDownLatch
name|doneCountDownLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"First"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Second"
argument_list|)
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
name|int
name|counter
decl_stmt|;
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
block|{
name|counter
operator|++
expr_stmt|;
try|try
block|{
name|TextMessage
name|message
init|=
operator|(
name|TextMessage
operator|)
name|msg
decl_stmt|;
switch|switch
condition|(
name|counter
condition|)
block|{
case|case
literal|1
case|:
name|assertEquals
argument_list|(
literal|"First"
argument_list|,
name|message
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|message
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|assertEquals
argument_list|(
literal|"Second"
argument_list|,
name|message
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|message
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|recover
argument_list|()
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|assertEquals
argument_list|(
literal|"Second"
argument_list|,
name|message
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|message
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|doneCountDownLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
break|break;
default|default:
name|errorMessage
index|[
literal|0
index|]
operator|=
literal|"Got too many messages: "
operator|+
name|counter
expr_stmt|;
name|doneCountDownLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|errorMessage
index|[
literal|0
index|]
operator|=
literal|"Got exception: "
operator|+
name|e
expr_stmt|;
name|doneCountDownLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
if|if
condition|(
name|doneCountDownLatch
operator|.
name|await
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
if|if
condition|(
name|errorMessage
index|[
literal|0
index|]
operator|!=
literal|null
condition|)
block|{
name|fail
argument_list|(
name|errorMessage
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Timeout waiting for async message delivery to complete."
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Test to make sure that a Async recover works when using AUTO_ACKNOWLEDGE.      *       * @throws JMSException      * @throws InterruptedException      */
specifier|public
name|void
name|doTestAsynchRecoverWithAutoAck
parameter_list|()
throws|throws
name|JMSException
throws|,
name|InterruptedException
block|{
specifier|final
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
specifier|final
name|String
name|errorMessage
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|null
block|}
decl_stmt|;
specifier|final
name|CountDownLatch
name|doneCountDownLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
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
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|dest
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|DeliveryMode
operator|.
name|NON_PERSISTENT
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"First"
argument_list|)
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Second"
argument_list|)
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|MessageListener
argument_list|()
block|{
name|int
name|counter
decl_stmt|;
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|msg
parameter_list|)
block|{
name|counter
operator|++
expr_stmt|;
try|try
block|{
name|TextMessage
name|message
init|=
operator|(
name|TextMessage
operator|)
name|msg
decl_stmt|;
switch|switch
condition|(
name|counter
condition|)
block|{
case|case
literal|1
case|:
name|assertEquals
argument_list|(
literal|"First"
argument_list|,
name|message
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|message
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
comment|// This should rollback the delivery of this message.. and re-deliver.
name|assertEquals
argument_list|(
literal|"Second"
argument_list|,
name|message
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|message
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|recover
argument_list|()
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|assertEquals
argument_list|(
literal|"Second"
argument_list|,
name|message
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|message
operator|.
name|getJMSRedelivered
argument_list|()
argument_list|)
expr_stmt|;
name|doneCountDownLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
break|break;
default|default:
name|errorMessage
index|[
literal|0
index|]
operator|=
literal|"Got too many messages: "
operator|+
name|counter
expr_stmt|;
name|doneCountDownLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|errorMessage
index|[
literal|0
index|]
operator|=
literal|"Got exception: "
operator|+
name|e
expr_stmt|;
name|doneCountDownLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
if|if
condition|(
name|doneCountDownLatch
operator|.
name|await
argument_list|(
literal|5000
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
condition|)
block|{
if|if
condition|(
name|errorMessage
index|[
literal|0
index|]
operator|!=
literal|null
condition|)
block|{
name|fail
argument_list|(
name|errorMessage
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Timeout waiting for async message delivery to complete."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

