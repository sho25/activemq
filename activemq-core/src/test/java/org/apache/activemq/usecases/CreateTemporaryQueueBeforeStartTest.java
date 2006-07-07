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
operator|.
name|usecases
package|;
end_package

begin_import
import|import
name|edu
operator|.
name|emory
operator|.
name|mathcs
operator|.
name|backport
operator|.
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|broker
operator|.
name|BrokerService
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
name|QueueReceiver
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueSender
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
name|Topic
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

begin_comment
comment|/**  * @author Peter Henning  * @version $Revision: 1.1.1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|CreateTemporaryQueueBeforeStartTest
extends|extends
name|TestCase
block|{
specifier|protected
name|String
name|bindAddress
init|=
literal|"tcp://localhost:61621"
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|private
name|BrokerService
name|broker
init|=
operator|new
name|BrokerService
argument_list|()
decl_stmt|;
specifier|public
name|void
name|testCreateTemporaryQueue
parameter_list|()
throws|throws
name|Exception
block|{
name|connection
operator|=
name|createConnection
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
name|Queue
name|queue
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"No queue created!"
argument_list|,
name|queue
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|Topic
name|topic
init|=
name|session
operator|.
name|createTemporaryTopic
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"No topic created!"
argument_list|,
name|topic
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testTryToReproduceNullPointerBug
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|url
init|=
name|bindAddress
decl_stmt|;
name|ActiveMQConnectionFactory
name|factory
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|url
argument_list|)
decl_stmt|;
name|QueueConnection
name|queueConnection
init|=
name|factory
operator|.
name|createQueueConnection
argument_list|()
decl_stmt|;
name|this
operator|.
name|connection
operator|=
name|queueConnection
expr_stmt|;
name|QueueSession
name|session
init|=
name|queueConnection
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
name|QueueSender
name|sender
init|=
name|session
operator|.
name|createSender
argument_list|(
literal|null
argument_list|)
decl_stmt|;
comment|//Unidentified
name|Queue
name|receiverQueue
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|QueueReceiver
name|receiver
init|=
name|session
operator|.
name|createReceiver
argument_list|(
name|receiverQueue
argument_list|)
decl_stmt|;
name|queueConnection
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testTemporaryQueueConsumer
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|NUMBER
init|=
literal|20
decl_stmt|;
specifier|final
name|AtomicInteger
name|count
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
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
name|NUMBER
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|thread
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|QueueConnection
name|connection
init|=
name|createConnection
argument_list|()
decl_stmt|;
name|QueueSession
name|session
init|=
name|connection
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
name|Queue
name|queue
init|=
name|session
operator|.
name|createTemporaryQueue
argument_list|()
decl_stmt|;
name|QueueReceiver
name|consumer
init|=
name|session
operator|.
name|createReceiver
argument_list|(
name|queue
argument_list|)
decl_stmt|;
name|connection
operator|.
name|start
argument_list|()
expr_stmt|;
if|if
condition|(
name|count
operator|.
name|incrementAndGet
argument_list|()
operator|>=
name|NUMBER
condition|)
block|{
synchronized|synchronized
init|(
name|count
init|)
block|{
name|count
operator|.
name|notify
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
name|thread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
name|int
name|maxWaitTime
init|=
literal|20000
decl_stmt|;
synchronized|synchronized
init|(
name|count
init|)
block|{
name|long
name|waitTime
init|=
name|maxWaitTime
decl_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
name|count
operator|.
name|get
argument_list|()
operator|<
name|NUMBER
condition|)
block|{
if|if
condition|(
name|waitTime
operator|<=
literal|0
condition|)
block|{
break|break;
block|}
else|else
block|{
name|count
operator|.
name|wait
argument_list|(
name|waitTime
argument_list|)
expr_stmt|;
name|waitTime
operator|=
name|maxWaitTime
operator|-
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
expr_stmt|;
block|}
block|}
block|}
name|assertTrue
argument_list|(
literal|"Unexpected count: "
operator|+
name|count
argument_list|,
name|count
operator|.
name|get
argument_list|()
operator|==
name|NUMBER
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|QueueConnection
name|createConnection
parameter_list|()
throws|throws
name|Exception
block|{
name|ActiveMQConnectionFactory
name|factory
init|=
name|createConnectionFactory
argument_list|()
decl_stmt|;
return|return
name|factory
operator|.
name|createQueueConnection
argument_list|()
return|;
block|}
specifier|protected
name|ActiveMQConnectionFactory
name|createConnectionFactory
parameter_list|()
throws|throws
name|Exception
block|{
return|return
operator|new
name|ActiveMQConnectionFactory
argument_list|(
name|bindAddress
argument_list|)
return|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|broker
operator|.
name|setPersistent
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|broker
operator|.
name|addConnector
argument_list|(
name|bindAddress
argument_list|)
expr_stmt|;
name|broker
operator|.
name|start
argument_list|()
expr_stmt|;
name|super
operator|.
name|setUp
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
name|broker
operator|.
name|stop
argument_list|()
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

