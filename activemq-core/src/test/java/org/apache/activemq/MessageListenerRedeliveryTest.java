begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2005-2006 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Queue
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

begin_class
specifier|public
class|class
name|MessageListenerRedeliveryTest
extends|extends
name|TestCase
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
name|MessageListenerRedeliveryTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|=
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
specifier|protected
name|RedeliveryPolicy
name|getRedeliveryPolicy
parameter_list|()
block|{
name|RedeliveryPolicy
name|redeliveryPolicy
init|=
operator|new
name|RedeliveryPolicy
argument_list|()
decl_stmt|;
name|redeliveryPolicy
operator|.
name|setInitialRedeliveryDelay
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|redeliveryPolicy
operator|.
name|setBackOffMultiplier
argument_list|(
operator|(
name|short
operator|)
literal|5
argument_list|)
expr_stmt|;
name|redeliveryPolicy
operator|.
name|setMaximumRedeliveries
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|redeliveryPolicy
operator|.
name|setUseExponentialBackOff
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|redeliveryPolicy
return|;
block|}
specifier|protected
name|Connection
name|createConnection
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
literal|"vm://localhost?broker.persistent=false"
argument_list|)
decl_stmt|;
name|factory
operator|.
name|setRedeliveryPolicy
argument_list|(
name|getRedeliveryPolicy
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|factory
operator|.
name|createConnection
argument_list|()
return|;
block|}
specifier|private
class|class
name|ConsumerMessageListenerTest
implements|implements
name|MessageListener
block|{
specifier|private
name|ActiveMQMessageConsumer
name|consumer
decl_stmt|;
specifier|public
name|int
name|counter
init|=
literal|0
decl_stmt|;
specifier|public
name|ConsumerMessageListenerTest
parameter_list|(
name|ActiveMQMessageConsumer
name|consumer
parameter_list|)
block|{
name|this
operator|.
name|consumer
operator|=
name|consumer
expr_stmt|;
block|}
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Message: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|counter
operator|++
expr_stmt|;
if|if
condition|(
name|counter
operator|<=
literal|2
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"ROLLBACK"
argument_list|)
expr_stmt|;
name|consumer
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"COMMIT"
argument_list|)
expr_stmt|;
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|consumer
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error when rolling back transaction"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
class|class
name|SessionMessageListenerTest
implements|implements
name|MessageListener
block|{
specifier|private
name|Session
name|session
decl_stmt|;
specifier|public
name|int
name|counter
init|=
literal|0
decl_stmt|;
specifier|public
name|SessionMessageListenerTest
parameter_list|(
name|Session
name|session
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
block|}
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
try|try
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Message: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|counter
operator|++
expr_stmt|;
if|if
condition|(
name|counter
operator|<
literal|2
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"ROLLBACK"
argument_list|)
expr_stmt|;
name|session
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|log
operator|.
name|info
argument_list|(
literal|"COMMIT"
argument_list|)
expr_stmt|;
name|message
operator|.
name|acknowledge
argument_list|()
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error when rolling back transaction"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|testQueueRollbackMessageListener
parameter_list|()
throws|throws
name|JMSException
block|{
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
literal|true
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|Queue
name|queue
init|=
name|session
operator|.
name|createQueue
argument_list|(
literal|"queue-"
operator|+
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|MessageProducer
name|producer
init|=
name|createProducer
argument_list|(
name|session
argument_list|,
name|queue
argument_list|)
decl_stmt|;
name|Message
name|message
init|=
name|createTextMessage
argument_list|(
name|session
argument_list|)
decl_stmt|;
name|producer
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
name|MessageConsumer
name|consumer
init|=
name|session
operator|.
name|createConsumer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|ActiveMQMessageConsumer
name|mc
init|=
operator|(
name|ActiveMQMessageConsumer
operator|)
name|consumer
decl_stmt|;
name|mc
operator|.
name|setRedeliveryPolicy
argument_list|(
name|getRedeliveryPolicy
argument_list|()
argument_list|)
expr_stmt|;
name|SessionMessageListenerTest
name|listener
init|=
operator|new
name|SessionMessageListenerTest
argument_list|(
name|session
argument_list|)
decl_stmt|;
name|consumer
operator|.
name|setMessageListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|7000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{          }
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|listener
operator|.
name|counter
argument_list|)
expr_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|createTextMessage
argument_list|(
name|session
argument_list|)
argument_list|)
expr_stmt|;
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// ignore
block|}
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|listener
operator|.
name|counter
argument_list|)
expr_stmt|;
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|private
name|TextMessage
name|createTextMessage
parameter_list|(
name|Session
name|session
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|session
operator|.
name|createTextMessage
argument_list|(
literal|"Hello"
argument_list|)
return|;
block|}
specifier|private
name|MessageProducer
name|createProducer
parameter_list|(
name|Session
name|session
parameter_list|,
name|Destination
name|queue
parameter_list|)
throws|throws
name|JMSException
block|{
name|MessageProducer
name|producer
init|=
name|session
operator|.
name|createProducer
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|producer
operator|.
name|setDeliveryMode
argument_list|(
name|getDeliveryMode
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|producer
return|;
block|}
specifier|protected
name|int
name|getDeliveryMode
parameter_list|()
block|{
return|return
name|DeliveryMode
operator|.
name|PERSISTENT
return|;
block|}
block|}
end_class

end_unit

