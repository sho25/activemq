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
name|ra
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|atomic
operator|.
name|AtomicBoolean
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
name|ConnectionConsumer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ExceptionListener
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
name|javax
operator|.
name|resource
operator|.
name|ResourceException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|endpoint
operator|.
name|MessageEndpointFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|work
operator|.
name|Work
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|work
operator|.
name|WorkException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|resource
operator|.
name|spi
operator|.
name|work
operator|.
name|WorkManager
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
name|ActiveMQConnection
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
name|ActiveMQDestination
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
comment|/**  * @version $Revision$ $Date$  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQEndpointWorker
block|{
specifier|public
specifier|static
specifier|final
name|Method
name|ON_MESSAGE_METHOD
decl_stmt|;
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
name|ActiveMQEndpointWorker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|INITIAL_RECONNECT_DELAY
init|=
literal|1000
decl_stmt|;
comment|// 1 second.
specifier|private
specifier|static
specifier|final
name|long
name|MAX_RECONNECT_DELAY
init|=
literal|1000
operator|*
literal|30
decl_stmt|;
comment|// 30 seconds.
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|Session
argument_list|>
name|THREAD_LOCAL
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Session
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
try|try
block|{
name|ON_MESSAGE_METHOD
operator|=
name|MessageListener
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"onMessage"
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Message
operator|.
name|class
block|}
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
name|ExceptionInInitializerError
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
specifier|final
name|ActiveMQEndpointActivationKey
name|endpointActivationKey
decl_stmt|;
specifier|protected
specifier|final
name|MessageEndpointFactory
name|endpointFactory
decl_stmt|;
specifier|protected
specifier|final
name|WorkManager
name|workManager
decl_stmt|;
specifier|protected
specifier|final
name|boolean
name|transacted
decl_stmt|;
specifier|private
specifier|final
name|ActiveMQDestination
name|dest
decl_stmt|;
specifier|private
specifier|final
name|Work
name|connectWork
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|connecting
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|shutdownMutex
init|=
literal|"shutdownMutex"
decl_stmt|;
specifier|private
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|private
name|ConnectionConsumer
name|consumer
decl_stmt|;
specifier|private
name|ServerSessionPoolImpl
name|serverSessionPool
decl_stmt|;
specifier|private
name|boolean
name|running
decl_stmt|;
specifier|protected
name|ActiveMQEndpointWorker
parameter_list|(
specifier|final
name|MessageResourceAdapter
name|adapter
parameter_list|,
name|ActiveMQEndpointActivationKey
name|key
parameter_list|)
throws|throws
name|ResourceException
block|{
name|this
operator|.
name|endpointActivationKey
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|endpointFactory
operator|=
name|endpointActivationKey
operator|.
name|getMessageEndpointFactory
argument_list|()
expr_stmt|;
name|this
operator|.
name|workManager
operator|=
name|adapter
operator|.
name|getBootstrapContext
argument_list|()
operator|.
name|getWorkManager
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|transacted
operator|=
name|endpointFactory
operator|.
name|isDeliveryTransacted
argument_list|(
name|ON_MESSAGE_METHOD
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"Endpoint does not implement the onMessage method."
argument_list|)
throw|;
block|}
name|connectWork
operator|=
operator|new
name|Work
argument_list|()
block|{
name|long
name|currentReconnectDelay
init|=
name|INITIAL_RECONNECT_DELAY
decl_stmt|;
specifier|public
name|void
name|release
parameter_list|()
block|{
comment|//
block|}
specifier|public
specifier|synchronized
name|void
name|run
parameter_list|()
block|{
name|currentReconnectDelay
operator|=
name|INITIAL_RECONNECT_DELAY
expr_stmt|;
name|MessageActivationSpec
name|activationSpec
init|=
name|endpointActivationKey
operator|.
name|getActivationSpec
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Establishing connection to broker ["
operator|+
name|adapter
operator|.
name|getInfo
argument_list|()
operator|.
name|getServerUrl
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|connecting
operator|.
name|get
argument_list|()
operator|&&
name|running
condition|)
block|{
try|try
block|{
name|connection
operator|=
name|adapter
operator|.
name|makeConnection
argument_list|(
name|activationSpec
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setExceptionListener
argument_list|(
operator|new
name|ExceptionListener
argument_list|()
block|{
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|error
parameter_list|)
block|{
if|if
condition|(
operator|!
name|serverSessionPool
operator|.
name|isClosing
argument_list|()
condition|)
block|{
comment|// initiate reconnection only once, i.e. on initial exception
comment|// and only if not already trying to connect
name|LOG
operator|.
name|error
argument_list|(
literal|"Connection to broker failed: "
operator|+
name|error
operator|.
name|getMessage
argument_list|()
argument_list|,
name|error
argument_list|)
expr_stmt|;
if|if
condition|(
name|connecting
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
synchronized|synchronized
init|(
name|connectWork
init|)
block|{
name|disconnect
argument_list|()
expr_stmt|;
name|serverSessionPool
operator|.
name|closeIdleSessions
argument_list|()
expr_stmt|;
name|connect
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// connection attempt has already been initiated
name|LOG
operator|.
name|info
argument_list|(
literal|"Connection attempt already in progress, ignoring connection exception"
argument_list|)
expr_stmt|;
block|}
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
name|int
name|prefetchSize
init|=
name|activationSpec
operator|.
name|getMaxMessagesPerSessionsIntValue
argument_list|()
operator|*
name|activationSpec
operator|.
name|getMaxSessionsIntValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|activationSpec
operator|.
name|isDurableSubscription
argument_list|()
condition|)
block|{
name|consumer
operator|=
name|connection
operator|.
name|createDurableConnectionConsumer
argument_list|(
operator|(
name|Topic
operator|)
name|dest
argument_list|,
name|activationSpec
operator|.
name|getSubscriptionName
argument_list|()
argument_list|,
name|emptyToNull
argument_list|(
name|activationSpec
operator|.
name|getMessageSelector
argument_list|()
argument_list|)
argument_list|,
name|serverSessionPool
argument_list|,
name|prefetchSize
argument_list|,
name|activationSpec
operator|.
name|getNoLocalBooleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|consumer
operator|=
name|connection
operator|.
name|createConnectionConsumer
argument_list|(
name|dest
argument_list|,
name|emptyToNull
argument_list|(
name|activationSpec
operator|.
name|getMessageSelector
argument_list|()
argument_list|)
argument_list|,
name|serverSessionPool
argument_list|,
name|prefetchSize
argument_list|,
name|activationSpec
operator|.
name|getNoLocalBooleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|connecting
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully established connection to broker ["
operator|+
name|adapter
operator|.
name|getInfo
argument_list|()
operator|.
name|getServerUrl
argument_list|()
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not release connection lock"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|error
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failed to connect: "
operator|+
name|error
operator|.
name|getMessage
argument_list|()
argument_list|,
name|error
argument_list|)
expr_stmt|;
block|}
name|disconnect
argument_list|()
expr_stmt|;
name|pause
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|pause
parameter_list|(
name|JMSException
name|error
parameter_list|)
block|{
if|if
condition|(
name|currentReconnectDelay
operator|==
name|MAX_RECONNECT_DELAY
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to connect to broker ["
operator|+
name|adapter
operator|.
name|getInfo
argument_list|()
operator|.
name|getServerUrl
argument_list|()
operator|+
literal|"]: "
operator|+
name|error
operator|.
name|getMessage
argument_list|()
argument_list|,
name|error
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Endpoint will try to reconnect to the JMS broker in "
operator|+
operator|(
name|MAX_RECONNECT_DELAY
operator|/
literal|1000
operator|)
operator|+
literal|" seconds"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
synchronized|synchronized
init|(
name|shutdownMutex
init|)
block|{
comment|// shutdownMutex will be notified by stop() method in
comment|// order to accelerate shutdown of endpoint
name|shutdownMutex
operator|.
name|wait
argument_list|(
name|currentReconnectDelay
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|interrupted
argument_list|()
expr_stmt|;
block|}
name|currentReconnectDelay
operator|*=
literal|2
expr_stmt|;
if|if
condition|(
name|currentReconnectDelay
operator|>
name|MAX_RECONNECT_DELAY
condition|)
block|{
name|currentReconnectDelay
operator|=
name|MAX_RECONNECT_DELAY
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
name|MessageActivationSpec
name|activationSpec
init|=
name|endpointActivationKey
operator|.
name|getActivationSpec
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"javax.jms.Queue"
operator|.
name|equals
argument_list|(
name|activationSpec
operator|.
name|getDestinationType
argument_list|()
argument_list|)
condition|)
block|{
name|dest
operator|=
operator|new
name|ActiveMQQueue
argument_list|(
name|activationSpec
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"javax.jms.Topic"
operator|.
name|equals
argument_list|(
name|activationSpec
operator|.
name|getDestinationType
argument_list|()
argument_list|)
condition|)
block|{
name|dest
operator|=
operator|new
name|ActiveMQTopic
argument_list|(
name|activationSpec
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"Unknown destination type: "
operator|+
name|activationSpec
operator|.
name|getDestinationType
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * @param c      */
specifier|public
specifier|static
name|void
name|safeClose
parameter_list|(
name|Connection
name|c
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|c
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Closing connection to broker"
argument_list|)
expr_stmt|;
name|c
operator|.
name|close
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
comment|//
block|}
block|}
comment|/**      * @param cc      */
specifier|public
specifier|static
name|void
name|safeClose
parameter_list|(
name|ConnectionConsumer
name|cc
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|cc
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Closing ConnectionConsumer"
argument_list|)
expr_stmt|;
name|cc
operator|.
name|close
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
comment|//
block|}
block|}
comment|/**      *       */
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|ResourceException
block|{
synchronized|synchronized
init|(
name|connectWork
init|)
block|{
if|if
condition|(
name|running
condition|)
return|return;
name|running
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|connecting
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting"
argument_list|)
expr_stmt|;
name|serverSessionPool
operator|=
operator|new
name|ServerSessionPoolImpl
argument_list|(
name|this
argument_list|,
name|endpointActivationKey
operator|.
name|getActivationSpec
argument_list|()
operator|.
name|getMaxSessionsIntValue
argument_list|()
argument_list|)
expr_stmt|;
name|connect
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Ignoring start command, EndpointWorker is already trying to connect"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      *       */
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|InterruptedException
block|{
synchronized|synchronized
init|(
name|shutdownMutex
init|)
block|{
if|if
condition|(
operator|!
name|running
condition|)
return|return;
name|running
operator|=
literal|false
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping"
argument_list|)
expr_stmt|;
comment|// wake up pausing reconnect attempt
name|shutdownMutex
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
name|serverSessionPool
operator|.
name|close
argument_list|()
expr_stmt|;
name|disconnect
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|isRunning
parameter_list|()
block|{
return|return
name|running
return|;
block|}
specifier|private
name|void
name|connect
parameter_list|()
block|{
synchronized|synchronized
init|(
name|connectWork
init|)
block|{
if|if
condition|(
operator|!
name|running
condition|)
block|{
return|return;
block|}
try|try
block|{
name|workManager
operator|.
name|scheduleWork
argument_list|(
name|connectWork
argument_list|,
name|WorkManager
operator|.
name|INDEFINITE
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|WorkException
name|e
parameter_list|)
block|{
name|running
operator|=
literal|false
expr_stmt|;
name|LOG
operator|.
name|error
argument_list|(
literal|"Work Manager did not accept work: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      *       */
specifier|private
name|void
name|disconnect
parameter_list|()
block|{
synchronized|synchronized
init|(
name|connectWork
init|)
block|{
name|safeClose
argument_list|(
name|consumer
argument_list|)
expr_stmt|;
name|consumer
operator|=
literal|null
expr_stmt|;
name|safeClose
argument_list|(
name|connection
argument_list|)
expr_stmt|;
name|connection
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|registerThreadSession
parameter_list|(
name|Session
name|session
parameter_list|)
block|{
name|THREAD_LOCAL
operator|.
name|set
argument_list|(
name|session
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|unregisterThreadSession
parameter_list|(
name|Session
name|session
parameter_list|)
block|{
name|THREAD_LOCAL
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|ActiveMQConnection
name|getConnection
parameter_list|()
block|{
comment|// make sure we only return a working connection
comment|// in particular make sure that we do not return null
comment|// after the resource adapter got disconnected from
comment|// the broker via the disconnect() method
synchronized|synchronized
init|(
name|connectWork
init|)
block|{
return|return
name|connection
return|;
block|}
block|}
specifier|private
name|String
name|emptyToNull
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
operator|||
name|value
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|value
return|;
block|}
block|}
end_class

end_unit

