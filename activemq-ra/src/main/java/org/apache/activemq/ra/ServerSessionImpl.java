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
name|MessageProducer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ServerSession
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
name|resource
operator|.
name|spi
operator|.
name|endpoint
operator|.
name|MessageEndpoint
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
name|WorkEvent
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
name|WorkListener
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
name|ActiveMQSession
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
name|ActiveMQSession
operator|.
name|DeliveryListener
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
class|class
name|ServerSessionImpl
implements|implements
name|ServerSession
implements|,
name|InboundContext
implements|,
name|Work
implements|,
name|DeliveryListener
block|{
specifier|public
specifier|static
specifier|final
name|Method
name|ON_MESSAGE_METHOD
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
specifier|private
specifier|static
name|int
name|nextLogId
init|=
literal|0
decl_stmt|;
specifier|synchronized
specifier|static
specifier|private
name|int
name|getNextLogId
parameter_list|()
block|{
return|return
name|nextLogId
operator|++
return|;
block|}
specifier|private
name|int
name|serverSessionId
init|=
name|getNextLogId
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ServerSessionImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|":"
operator|+
name|serverSessionId
argument_list|)
decl_stmt|;
specifier|private
name|ActiveMQSession
name|session
decl_stmt|;
specifier|private
name|WorkManager
name|workManager
decl_stmt|;
specifier|private
name|MessageEndpoint
name|endpoint
decl_stmt|;
specifier|private
name|MessageProducer
name|messageProducer
decl_stmt|;
specifier|private
specifier|final
name|ServerSessionPoolImpl
name|pool
decl_stmt|;
specifier|private
name|Object
name|runControlMutex
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
name|boolean
name|runningFlag
init|=
literal|false
decl_stmt|;
comment|/**       * True if an error was detected that cause this session to be stale.  When a session       * is stale, it should not be used again for proccessing.      */
specifier|private
name|boolean
name|stale
decl_stmt|;
comment|/**      * Does the TX commit need to be managed by the RA?      */
specifier|private
specifier|final
name|boolean
name|useRAManagedTx
decl_stmt|;
comment|/**      * The maximum number of messages to batch      */
specifier|private
specifier|final
name|int
name|batchSize
decl_stmt|;
comment|/**      * The current number of messages in the batch      */
specifier|private
name|int
name|currentBatchSize
decl_stmt|;
specifier|public
name|ServerSessionImpl
parameter_list|(
name|ServerSessionPoolImpl
name|pool
parameter_list|,
name|ActiveMQSession
name|session
parameter_list|,
name|WorkManager
name|workManager
parameter_list|,
name|MessageEndpoint
name|endpoint
parameter_list|,
name|boolean
name|useRAManagedTx
parameter_list|,
name|int
name|batchSize
parameter_list|)
throws|throws
name|JMSException
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
name|this
operator|.
name|workManager
operator|=
name|workManager
expr_stmt|;
name|this
operator|.
name|endpoint
operator|=
name|endpoint
expr_stmt|;
name|this
operator|.
name|useRAManagedTx
operator|=
name|useRAManagedTx
expr_stmt|;
name|this
operator|.
name|session
operator|.
name|setMessageListener
argument_list|(
operator|(
name|MessageListener
operator|)
name|endpoint
argument_list|)
expr_stmt|;
name|this
operator|.
name|session
operator|.
name|setDeliveryListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|batchSize
operator|=
name|batchSize
expr_stmt|;
block|}
specifier|public
name|Session
name|getSession
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|session
return|;
block|}
specifier|public
name|MessageProducer
name|getMessageProducer
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|messageProducer
operator|==
literal|null
condition|)
block|{
name|messageProducer
operator|=
name|getSession
argument_list|()
operator|.
name|createProducer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|messageProducer
return|;
block|}
comment|/**      * @see javax.jms.ServerSession#start()      */
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|JMSException
block|{
synchronized|synchronized
init|(
name|runControlMutex
init|)
block|{
if|if
condition|(
name|runningFlag
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Start request ignored, already running."
argument_list|)
expr_stmt|;
return|return;
block|}
name|runningFlag
operator|=
literal|true
expr_stmt|;
block|}
comment|// We get here because we need to start a async worker.
name|log
operator|.
name|debug
argument_list|(
literal|"Starting run."
argument_list|)
expr_stmt|;
try|try
block|{
name|workManager
operator|.
name|scheduleWork
argument_list|(
name|this
argument_list|,
name|WorkManager
operator|.
name|INDEFINITE
argument_list|,
literal|null
argument_list|,
operator|new
name|WorkListener
argument_list|()
block|{
comment|//The work listener is useful only for debugging...
specifier|public
name|void
name|workAccepted
parameter_list|(
name|WorkEvent
name|event
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Work accepted: "
operator|+
name|event
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|workRejected
parameter_list|(
name|WorkEvent
name|event
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Work rejected: "
operator|+
name|event
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|workStarted
parameter_list|(
name|WorkEvent
name|event
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Work started: "
operator|+
name|event
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|workCompleted
parameter_list|(
name|WorkEvent
name|event
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Work completed: "
operator|+
name|event
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|WorkException
name|e
parameter_list|)
block|{
throw|throw
operator|(
name|JMSException
operator|)
operator|new
name|JMSException
argument_list|(
literal|"Start failed: "
operator|+
name|e
argument_list|)
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see java.lang.Runnable#run()      */
specifier|synchronized
specifier|public
name|void
name|run
parameter_list|()
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Running"
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"run loop start"
argument_list|)
expr_stmt|;
try|try
block|{
name|InboundContextSupport
operator|.
name|register
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|currentBatchSize
operator|=
literal|0
expr_stmt|;
name|session
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|stale
operator|=
literal|true
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Endpoint failed to process message."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Endpoint failed to process message. Reason: "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|InboundContextSupport
operator|.
name|unregister
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"run loop end"
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|runControlMutex
init|)
block|{
comment|// This endpoint may have gone stale due to error
if|if
condition|(
name|stale
condition|)
block|{
name|runningFlag
operator|=
literal|false
expr_stmt|;
name|pool
operator|.
name|removeFromPool
argument_list|(
name|this
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
operator|!
name|session
operator|.
name|hasUncomsumedMessages
argument_list|()
condition|)
block|{
name|runningFlag
operator|=
literal|false
expr_stmt|;
name|pool
operator|.
name|returnToPool
argument_list|(
name|this
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
name|log
operator|.
name|debug
argument_list|(
literal|"Run finished"
argument_list|)
expr_stmt|;
block|}
comment|/**      * The ActiveMQSession's run method will call back to this method before       * dispactching a message to the MessageListener.      */
specifier|public
name|void
name|beforeDelivery
parameter_list|(
name|ActiveMQSession
name|session
parameter_list|,
name|Message
name|msg
parameter_list|)
block|{
if|if
condition|(
name|currentBatchSize
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|endpoint
operator|.
name|beforeDelivery
argument_list|(
name|ON_MESSAGE_METHOD
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Endpoint before delivery notification failure"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * The ActiveMQSession's run method will call back to this method after       * dispactching a message to the MessageListener.      */
specifier|public
name|void
name|afterDelivery
parameter_list|(
name|ActiveMQSession
name|session
parameter_list|,
name|Message
name|msg
parameter_list|)
block|{
if|if
condition|(
operator|++
name|currentBatchSize
operator|>=
name|batchSize
operator|||
operator|!
name|session
operator|.
name|hasUncomsumedMessages
argument_list|()
condition|)
block|{
name|currentBatchSize
operator|=
literal|0
expr_stmt|;
try|try
block|{
name|endpoint
operator|.
name|afterDelivery
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Endpoint after delivery notification failure"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|session
operator|.
name|getTransactionContext
argument_list|()
operator|.
name|isInLocalTransaction
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|useRAManagedTx
condition|)
block|{
comment|// Sanitiy Check: If the local transaction has not been commited..
comment|// Commit it now.
name|log
operator|.
name|warn
argument_list|(
literal|"Local transaction had not been commited.  Commiting now."
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|session
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Commit failed:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
comment|/**      * @see javax.resource.spi.work.Work#release()      */
specifier|public
name|void
name|release
parameter_list|()
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"release called"
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see java.lang.Object#toString()      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ServerSessionImpl:"
operator|+
name|serverSessionId
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
try|try
block|{
name|endpoint
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Endpoint did not release properly: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|session
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Session did not close properly: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

