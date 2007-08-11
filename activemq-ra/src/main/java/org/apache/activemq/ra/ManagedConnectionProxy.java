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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|ConnectionMetaData
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
name|ExceptionListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|IllegalStateException
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
name|QueueSession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|ServerSessionPool
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
name|jms
operator|.
name|TopicConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicSession
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
name|ActiveMQQueueSession
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
name|ActiveMQTopicSession
import|;
end_import

begin_comment
comment|/**  * Acts as a pass through proxy for a JMS Connection object.  * It intercepts events that are of interest of the ActiveMQManagedConnection.  *  * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|ManagedConnectionProxy
implements|implements
name|Connection
implements|,
name|QueueConnection
implements|,
name|TopicConnection
implements|,
name|ExceptionListener
block|{
specifier|private
name|ActiveMQManagedConnection
name|managedConnection
decl_stmt|;
specifier|private
name|ArrayList
name|sessions
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|private
name|ExceptionListener
name|exceptionListener
decl_stmt|;
specifier|public
name|ManagedConnectionProxy
parameter_list|(
name|ActiveMQManagedConnection
name|managedConnection
parameter_list|)
block|{
name|this
operator|.
name|managedConnection
operator|=
name|managedConnection
expr_stmt|;
block|}
comment|/**      * Used to let the ActiveMQManagedConnection that this connection      * handel is not needed by the app.      *      * @throws JMSException      */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|managedConnection
operator|!=
literal|null
condition|)
block|{
name|managedConnection
operator|.
name|proxyClosedEvent
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Called by the ActiveMQManagedConnection to invalidate this proxy.      */
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
name|exceptionListener
operator|=
literal|null
expr_stmt|;
name|managedConnection
operator|=
literal|null
expr_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|sessions
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ManagedSessionProxy
name|p
init|=
operator|(
name|ManagedSessionProxy
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
block|{
name|p
operator|.
name|cleanup
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|ignore
parameter_list|)
block|{             }
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      *       */
specifier|private
name|Connection
name|getConnection
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|managedConnection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The Connection is closed"
argument_list|)
throw|;
block|}
return|return
name|managedConnection
operator|.
name|getPhysicalConnection
argument_list|()
return|;
block|}
comment|/**      * @param transacted      * @param acknowledgeMode      * @return      * @throws JMSException      */
specifier|public
name|Session
name|createSession
parameter_list|(
name|boolean
name|transacted
parameter_list|,
name|int
name|acknowledgeMode
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|createSessionProxy
argument_list|(
name|transacted
argument_list|,
name|acknowledgeMode
argument_list|)
return|;
block|}
comment|/**      * @param acknowledgeMode      * @param transacted      * @return      * @throws JMSException      */
specifier|private
name|ManagedSessionProxy
name|createSessionProxy
parameter_list|(
name|boolean
name|transacted
parameter_list|,
name|int
name|acknowledgeMode
parameter_list|)
throws|throws
name|JMSException
block|{
name|ActiveMQSession
name|session
init|=
operator|(
name|ActiveMQSession
operator|)
name|getConnection
argument_list|()
operator|.
name|createSession
argument_list|(
name|transacted
argument_list|,
name|acknowledgeMode
argument_list|)
decl_stmt|;
name|ManagedTransactionContext
name|txContext
init|=
operator|new
name|ManagedTransactionContext
argument_list|(
name|managedConnection
operator|.
name|getTransactionContext
argument_list|()
argument_list|)
decl_stmt|;
name|session
operator|.
name|setTransactionContext
argument_list|(
name|txContext
argument_list|)
expr_stmt|;
name|ManagedSessionProxy
name|p
init|=
operator|new
name|ManagedSessionProxy
argument_list|(
name|session
argument_list|)
decl_stmt|;
name|p
operator|.
name|setUseSharedTxContext
argument_list|(
name|managedConnection
operator|.
name|isInManagedTx
argument_list|()
argument_list|)
expr_stmt|;
name|sessions
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
specifier|public
name|void
name|setUseSharedTxContext
parameter_list|(
name|boolean
name|enable
parameter_list|)
throws|throws
name|JMSException
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|sessions
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ManagedSessionProxy
name|p
init|=
operator|(
name|ManagedSessionProxy
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|p
operator|.
name|setUseSharedTxContext
argument_list|(
name|enable
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @param transacted      * @param acknowledgeMode      * @return      * @throws JMSException      */
specifier|public
name|QueueSession
name|createQueueSession
parameter_list|(
name|boolean
name|transacted
parameter_list|,
name|int
name|acknowledgeMode
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
operator|new
name|ActiveMQQueueSession
argument_list|(
name|createSessionProxy
argument_list|(
name|transacted
argument_list|,
name|acknowledgeMode
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * @param transacted      * @param acknowledgeMode      * @return      * @throws JMSException      */
specifier|public
name|TopicSession
name|createTopicSession
parameter_list|(
name|boolean
name|transacted
parameter_list|,
name|int
name|acknowledgeMode
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
operator|new
name|ActiveMQTopicSession
argument_list|(
name|createSessionProxy
argument_list|(
name|transacted
argument_list|,
name|acknowledgeMode
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * @return      * @throws JMSException      */
specifier|public
name|String
name|getClientID
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getConnection
argument_list|()
operator|.
name|getClientID
argument_list|()
return|;
block|}
comment|/**      * @return      * @throws JMSException      */
specifier|public
name|ExceptionListener
name|getExceptionListener
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getConnection
argument_list|()
operator|.
name|getExceptionListener
argument_list|()
return|;
block|}
comment|/**      * @return      * @throws JMSException      */
specifier|public
name|ConnectionMetaData
name|getMetaData
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|getConnection
argument_list|()
operator|.
name|getMetaData
argument_list|()
return|;
block|}
comment|/**      * @param clientID      * @throws JMSException      */
specifier|public
name|void
name|setClientID
parameter_list|(
name|String
name|clientID
parameter_list|)
throws|throws
name|JMSException
block|{
name|getConnection
argument_list|()
operator|.
name|setClientID
argument_list|(
name|clientID
argument_list|)
expr_stmt|;
block|}
comment|/**      * @param listener      * @throws JMSException      */
specifier|public
name|void
name|setExceptionListener
parameter_list|(
name|ExceptionListener
name|listener
parameter_list|)
throws|throws
name|JMSException
block|{
name|getConnection
argument_list|()
expr_stmt|;
name|exceptionListener
operator|=
name|listener
expr_stmt|;
block|}
comment|/**      * @throws JMSException      */
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|JMSException
block|{
name|getConnection
argument_list|()
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|/**      * @throws JMSException      */
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|JMSException
block|{
name|getConnection
argument_list|()
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**      * @param queue      * @param messageSelector      * @param sessionPool      * @param maxMessages      * @return      * @throws JMSException      */
specifier|public
name|ConnectionConsumer
name|createConnectionConsumer
parameter_list|(
name|Queue
name|queue
parameter_list|,
name|String
name|messageSelector
parameter_list|,
name|ServerSessionPool
name|sessionPool
parameter_list|,
name|int
name|maxMessages
parameter_list|)
throws|throws
name|JMSException
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Not Supported."
argument_list|)
throw|;
block|}
comment|/**      * @param topic      * @param messageSelector      * @param sessionPool      * @param maxMessages      * @return      * @throws JMSException      */
specifier|public
name|ConnectionConsumer
name|createConnectionConsumer
parameter_list|(
name|Topic
name|topic
parameter_list|,
name|String
name|messageSelector
parameter_list|,
name|ServerSessionPool
name|sessionPool
parameter_list|,
name|int
name|maxMessages
parameter_list|)
throws|throws
name|JMSException
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Not Supported."
argument_list|)
throw|;
block|}
comment|/**      * @param destination      * @param messageSelector      * @param sessionPool      * @param maxMessages      * @return      * @throws JMSException      */
specifier|public
name|ConnectionConsumer
name|createConnectionConsumer
parameter_list|(
name|Destination
name|destination
parameter_list|,
name|String
name|messageSelector
parameter_list|,
name|ServerSessionPool
name|sessionPool
parameter_list|,
name|int
name|maxMessages
parameter_list|)
throws|throws
name|JMSException
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Not Supported."
argument_list|)
throw|;
block|}
comment|/**      * @param topic      * @param subscriptionName      * @param messageSelector      * @param sessionPool      * @param maxMessages      * @return      * @throws JMSException      */
specifier|public
name|ConnectionConsumer
name|createDurableConnectionConsumer
parameter_list|(
name|Topic
name|topic
parameter_list|,
name|String
name|subscriptionName
parameter_list|,
name|String
name|messageSelector
parameter_list|,
name|ServerSessionPool
name|sessionPool
parameter_list|,
name|int
name|maxMessages
parameter_list|)
throws|throws
name|JMSException
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"Not Supported."
argument_list|)
throw|;
block|}
comment|/**      * @return Returns the managedConnection.      */
specifier|public
name|ActiveMQManagedConnection
name|getManagedConnection
parameter_list|()
block|{
return|return
name|managedConnection
return|;
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
if|if
condition|(
name|exceptionListener
operator|!=
literal|null
operator|&&
name|managedConnection
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|exceptionListener
operator|.
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{
comment|// We can never trust user code so ignore any exceptions.
block|}
block|}
block|}
block|}
end_class

end_unit

