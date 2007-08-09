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
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|QueueDuplicatesTest
extends|extends
name|TestCase
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
name|QueueDuplicatesTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|DateFormat
name|formatter
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"HH:mm:ss SSS"
argument_list|)
decl_stmt|;
specifier|private
name|String
name|brokerUrl
decl_stmt|;
specifier|private
name|String
name|subject
decl_stmt|;
specifier|private
name|Connection
name|brokerConnection
decl_stmt|;
specifier|public
name|QueueDuplicatesTest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
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
name|String
name|peerUrl
init|=
literal|"peer://localhost:6099"
decl_stmt|;
name|subject
operator|=
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
expr_stmt|;
name|ActiveMQConnectionFactory
name|fac
init|=
name|createFactory
argument_list|(
name|peerUrl
argument_list|)
decl_stmt|;
name|brokerConnection
operator|=
name|fac
operator|.
name|createConnection
argument_list|()
expr_stmt|;
name|brokerConnection
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
name|brokerConnection
operator|!=
literal|null
condition|)
block|{
name|brokerConnection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testDuplicates
parameter_list|()
block|{
try|try
block|{
comment|// Get Session
name|Session
name|session
init|=
name|createSession
argument_list|(
name|brokerConnection
argument_list|)
decl_stmt|;
comment|// create consumer
name|Destination
name|dest
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|subject
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
comment|// subscribe to queue
name|consumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|SimpleConsumer
argument_list|()
argument_list|)
expr_stmt|;
comment|// create producer
name|Thread
name|sendingThread
init|=
operator|new
name|SendingThread
argument_list|(
name|brokerUrl
argument_list|,
name|subject
argument_list|)
decl_stmt|;
comment|// start producer
name|sendingThread
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// wait about 5 seconds
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|// unsubscribe consumer
name|consumer
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// wait another 5 seconds
name|Thread
operator|.
name|sleep
argument_list|(
literal|5000
argument_list|)
expr_stmt|;
comment|// create new consumer
name|consumer
operator|=
name|session
operator|.
name|createConsumer
argument_list|(
name|dest
argument_list|)
expr_stmt|;
comment|// subscribe to queue
name|consumer
operator|.
name|setMessageListener
argument_list|(
operator|new
name|SimpleConsumer
argument_list|()
argument_list|)
expr_stmt|;
comment|// sleep a little while longer
name|Thread
operator|.
name|sleep
argument_list|(
literal|15000
argument_list|)
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
name|Session
name|createSession
parameter_list|(
name|Connection
name|peerConnection
parameter_list|)
throws|throws
name|JMSException
block|{
comment|// Connect using peer to peer connection
name|Session
name|session
init|=
name|peerConnection
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
return|return
name|session
return|;
block|}
specifier|private
name|ActiveMQConnectionFactory
name|createFactory
parameter_list|(
name|String
name|brokerUrl
parameter_list|)
block|{
name|ActiveMQConnectionFactory
name|cf
init|=
operator|new
name|ActiveMQConnectionFactory
argument_list|()
decl_stmt|;
name|cf
operator|.
name|setBrokerURL
argument_list|(
name|brokerUrl
argument_list|)
expr_stmt|;
return|return
name|cf
return|;
block|}
specifier|private
class|class
name|SendingThread
extends|extends
name|Thread
block|{
specifier|private
name|String
name|brokerUrl
decl_stmt|;
specifier|private
name|String
name|subject
decl_stmt|;
name|SendingThread
parameter_list|(
name|String
name|brokerUrl
parameter_list|,
name|String
name|subject
parameter_list|)
block|{
name|this
operator|.
name|brokerUrl
operator|=
name|brokerUrl
expr_stmt|;
name|this
operator|.
name|subject
operator|=
name|subject
expr_stmt|;
name|setDaemon
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
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
name|createSession
argument_list|(
name|brokerConnection
argument_list|)
decl_stmt|;
name|Destination
name|dest
init|=
name|session
operator|.
name|createQueue
argument_list|(
name|subject
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
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|20
condition|;
name|i
operator|++
control|)
block|{
name|String
name|txt
init|=
literal|"Text Message: "
operator|+
name|i
decl_stmt|;
name|TextMessage
name|msg
init|=
name|session
operator|.
name|createTextMessage
argument_list|(
name|txt
argument_list|)
decl_stmt|;
name|producer
operator|.
name|send
argument_list|(
name|msg
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|formatter
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
operator|+
literal|" Sent ==> "
operator|+
name|msg
operator|+
literal|" to "
operator|+
name|subject
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
class|class
name|SimpleConsumer
implements|implements
name|MessageListener
block|{
specifier|private
name|Map
name|msgs
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
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
name|formatter
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|()
argument_list|)
operator|+
literal|" SimpleConsumer Message Received: "
operator|+
name|message
argument_list|)
expr_stmt|;
try|try
block|{
name|String
name|id
init|=
name|message
operator|.
name|getJMSMessageID
argument_list|()
decl_stmt|;
name|assertNull
argument_list|(
literal|"Message is duplicate: "
operator|+
name|id
argument_list|,
name|msgs
operator|.
name|get
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|msgs
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
block|}
end_class

end_unit

