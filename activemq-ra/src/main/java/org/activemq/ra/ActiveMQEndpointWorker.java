begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
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
name|activemq
operator|.
name|ActiveMQConnection
import|;
end_import

begin_import
import|import
name|org
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
name|ActiveMQEndpointWorker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Method
name|ON_MESSAGE_METHOD
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
name|threadLocal
init|=
operator|new
name|ThreadLocal
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
name|ActiveMQResourceAdapter
name|adapter
decl_stmt|;
specifier|protected
name|ActiveMQEndpointActivationKey
name|endpointActivationKey
decl_stmt|;
specifier|protected
name|MessageEndpointFactory
name|endpointFactory
decl_stmt|;
specifier|protected
name|WorkManager
name|workManager
decl_stmt|;
specifier|protected
name|boolean
name|transacted
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
name|ActiveMQDestination
name|dest
decl_stmt|;
specifier|private
name|boolean
name|running
decl_stmt|;
specifier|private
name|Work
name|connectWork
decl_stmt|;
specifier|protected
name|ActiveMQConnection
name|connection
decl_stmt|;
specifier|private
name|long
name|reconnectDelay
init|=
name|INITIAL_RECONNECT_DELAY
decl_stmt|;
comment|/**      * @param s      */
specifier|public
specifier|static
name|void
name|safeClose
parameter_list|(
name|Session
name|s
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|s
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
block|{         }
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
block|{         }
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
block|{         }
block|}
specifier|public
name|ActiveMQEndpointWorker
parameter_list|(
specifier|final
name|ActiveMQResourceAdapter
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
name|adapter
operator|=
name|adapter
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
specifier|public
name|void
name|release
parameter_list|()
block|{             }
specifier|synchronized
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isRunning
argument_list|()
condition|)
return|return;
if|if
condition|(
name|connection
operator|!=
literal|null
condition|)
return|return;
name|ActiveMQActivationSpec
name|activationSpec
init|=
name|endpointActivationKey
operator|.
name|getActivationSpec
argument_list|()
decl_stmt|;
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
name|start
argument_list|()
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
name|reconnect
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
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
name|activationSpec
operator|.
name|getMaxMessagesPerSessionsIntValue
argument_list|()
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
name|activationSpec
operator|.
name|getMaxMessagesPerSessionsIntValue
argument_list|()
argument_list|,
name|activationSpec
operator|.
name|getNoLocalBooleanValue
argument_list|()
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
name|log
operator|.
name|debug
argument_list|(
literal|"Fail to to connect: "
operator|+
name|error
argument_list|,
name|error
argument_list|)
expr_stmt|;
name|reconnect
argument_list|(
name|error
argument_list|)
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
name|ActiveMQActivationSpec
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
specifier|synchronized
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|WorkException
throws|,
name|ResourceException
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
name|log
operator|.
name|debug
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
name|log
operator|.
name|debug
argument_list|(
literal|"Started"
argument_list|)
expr_stmt|;
block|}
comment|/**      *       */
specifier|synchronized
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|InterruptedException
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
name|serverSessionPool
operator|.
name|close
argument_list|()
expr_stmt|;
name|disconnect
argument_list|()
expr_stmt|;
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
specifier|synchronized
specifier|private
name|void
name|connect
parameter_list|()
block|{
if|if
condition|(
operator|!
name|running
condition|)
return|return;
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
name|log
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
comment|/**      *       */
specifier|synchronized
specifier|private
name|void
name|disconnect
parameter_list|()
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
specifier|synchronized
specifier|private
name|void
name|reconnect
parameter_list|(
name|JMSException
name|error
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Reconnect cause: "
argument_list|,
name|error
argument_list|)
expr_stmt|;
comment|// Only log errors if the server is really down..  And not a temp failure.
if|if
condition|(
name|reconnectDelay
operator|==
name|MAX_RECONNECT_DELAY
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Endpoint connection to JMS broker failed: "
operator|+
name|error
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
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
name|disconnect
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|reconnectDelay
argument_list|)
expr_stmt|;
comment|// Use exponential rollback.
name|reconnectDelay
operator|*=
literal|2
expr_stmt|;
if|if
condition|(
name|reconnectDelay
operator|>
name|MAX_RECONNECT_DELAY
condition|)
name|reconnectDelay
operator|=
name|MAX_RECONNECT_DELAY
expr_stmt|;
name|connect
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{         }
block|}
specifier|protected
name|void
name|registerThreadSession
parameter_list|(
name|Session
name|session
parameter_list|)
block|{
name|threadLocal
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
name|threadLocal
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
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

