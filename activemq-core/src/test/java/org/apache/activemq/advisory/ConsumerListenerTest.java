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
name|advisory
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
name|ArrayBlockingQueue
import|;
end_import

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
name|BlockingQueue
import|;
end_import

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
name|TimeUnit
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
name|advisory
operator|.
name|ConsumerEvent
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
name|advisory
operator|.
name|ConsumerEventSource
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
name|advisory
operator|.
name|ConsumerListener
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

begin_comment
comment|/**  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ConsumerListenerTest
extends|extends
name|EmbeddedBrokerTestSupport
implements|implements
name|ConsumerListener
block|{
specifier|protected
name|Session
name|consumerSession1
decl_stmt|;
specifier|protected
name|Session
name|consumerSession2
decl_stmt|;
specifier|protected
name|int
name|consumerCounter
decl_stmt|;
specifier|protected
name|ConsumerEventSource
name|consumerEventSource
decl_stmt|;
specifier|protected
name|BlockingQueue
name|eventQueue
init|=
operator|new
name|ArrayBlockingQueue
argument_list|(
literal|1000
argument_list|)
decl_stmt|;
specifier|private
name|Connection
name|connection
decl_stmt|;
specifier|public
name|void
name|testConsumerEvents
parameter_list|()
throws|throws
name|Exception
block|{
name|consumerEventSource
operator|.
name|start
argument_list|()
expr_stmt|;
name|consumerSession1
operator|=
name|createConsumer
argument_list|()
expr_stmt|;
name|assertConsumerEvent
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|consumerSession2
operator|=
name|createConsumer
argument_list|()
expr_stmt|;
name|assertConsumerEvent
argument_list|(
literal|2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|consumerSession1
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumerSession1
operator|=
literal|null
expr_stmt|;
name|assertConsumerEvent
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|consumerSession2
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumerSession2
operator|=
literal|null
expr_stmt|;
name|assertConsumerEvent
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testListenWhileAlreadyConsumersActive
parameter_list|()
throws|throws
name|Exception
block|{
name|consumerSession1
operator|=
name|createConsumer
argument_list|()
expr_stmt|;
name|consumerSession2
operator|=
name|createConsumer
argument_list|()
expr_stmt|;
name|consumerEventSource
operator|.
name|start
argument_list|()
expr_stmt|;
name|assertConsumerEvent
argument_list|(
literal|2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertConsumerEvent
argument_list|(
literal|2
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|consumerSession1
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumerSession1
operator|=
literal|null
expr_stmt|;
name|assertConsumerEvent
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|consumerSession2
operator|.
name|close
argument_list|()
expr_stmt|;
name|consumerSession2
operator|=
literal|null
expr_stmt|;
name|assertConsumerEvent
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|)
expr_stmt|;
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
name|consumerEventSource
operator|=
operator|new
name|ConsumerEventSource
argument_list|(
name|connection
argument_list|,
name|destination
argument_list|)
expr_stmt|;
name|consumerEventSource
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
name|consumerEventSource
operator|!=
literal|null
condition|)
block|{
name|consumerEventSource
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|consumerSession2
operator|!=
literal|null
condition|)
block|{
name|consumerSession2
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|consumerSession1
operator|!=
literal|null
condition|)
block|{
name|consumerSession1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|Session
name|createConsumer
parameter_list|()
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Creating consumer: "
operator|+
name|consumerText
operator|+
literal|" on destination: "
operator|+
name|destination
argument_list|)
expr_stmt|;
name|Session
name|answer
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
name|MessageConsumer
name|consumer
init|=
name|answer
operator|.
name|createConsumer
argument_list|(
name|destination
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
name|System
operator|.
name|out
operator|.
name|println
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
name|answer
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
operator|(
name|ConsumerEvent
operator|)
name|eventQueue
operator|.
name|poll
argument_list|(
literal|100000
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

