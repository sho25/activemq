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
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|CopyOnWriteArrayList
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
name|ConnectionEvent
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
name|ConnectionEventListener
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
name|ConnectionRequestInfo
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
name|LocalTransaction
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
name|ManagedConnection
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
name|ManagedConnectionMetaData
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|XAResource
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
name|LocalTransactionEventListener
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
name|TransactionContext
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

begin_comment
comment|/**  * ActiveMQManagedConnection maps to real physical connection to the server.  * Since a ManagedConnection has to provide a transaction managment interface to  * the physical connection, and sessions are the objects implement transaction  * managment interfaces in the JMS API, this object also maps to a singe  * physical JMS session.<p/> The side-effect is that JMS connection the  * application gets will allways create the same session object. This is good if  * running in an app server since the sessions are elisted in the context  * transaction. This is bad if used outside of an app server since the user may  * be trying to create 2 different sessions to coordinate 2 different uow.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQManagedConnection
implements|implements
name|ManagedConnection
implements|,
name|ExceptionListener
block|{
comment|// TODO:
comment|// ,
comment|// DissociatableManagedConnection
comment|// {
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
name|ActiveMQManagedConnection
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|PrintWriter
name|logWriter
decl_stmt|;
specifier|private
specifier|final
name|ActiveMQConnection
name|physicalConnection
decl_stmt|;
specifier|private
specifier|final
name|TransactionContext
name|transactionContext
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|ManagedConnectionProxy
argument_list|>
name|proxyConnections
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|ManagedConnectionProxy
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|ConnectionEventListener
argument_list|>
name|listeners
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<
name|ConnectionEventListener
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|LocalAndXATransaction
name|localAndXATransaction
decl_stmt|;
specifier|private
name|Subject
name|subject
decl_stmt|;
specifier|private
name|ActiveMQConnectionRequestInfo
name|info
decl_stmt|;
specifier|private
name|boolean
name|destroyed
decl_stmt|;
specifier|public
name|ActiveMQManagedConnection
parameter_list|(
name|Subject
name|subject
parameter_list|,
name|ActiveMQConnection
name|physicalConnection
parameter_list|,
name|ActiveMQConnectionRequestInfo
name|info
parameter_list|)
throws|throws
name|ResourceException
block|{
try|try
block|{
name|this
operator|.
name|subject
operator|=
name|subject
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
name|this
operator|.
name|physicalConnection
operator|=
name|physicalConnection
expr_stmt|;
name|this
operator|.
name|transactionContext
operator|=
operator|new
name|TransactionContext
argument_list|(
name|physicalConnection
argument_list|)
expr_stmt|;
name|this
operator|.
name|localAndXATransaction
operator|=
operator|new
name|LocalAndXATransaction
argument_list|(
name|transactionContext
argument_list|)
block|{
specifier|public
name|void
name|setInManagedTx
parameter_list|(
name|boolean
name|inManagedTx
parameter_list|)
throws|throws
name|JMSException
block|{
name|super
operator|.
name|setInManagedTx
argument_list|(
name|inManagedTx
argument_list|)
expr_stmt|;
for|for
control|(
name|ManagedConnectionProxy
name|proxy
range|:
name|proxyConnections
control|)
block|{
name|proxy
operator|.
name|setUseSharedTxContext
argument_list|(
name|inManagedTx
argument_list|)
expr_stmt|;
block|}
block|}
block|}
expr_stmt|;
name|this
operator|.
name|transactionContext
operator|.
name|setLocalTransactionEventListener
argument_list|(
operator|new
name|LocalTransactionEventListener
argument_list|()
block|{
specifier|public
name|void
name|beginEvent
parameter_list|()
block|{
name|fireBeginEvent
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|commitEvent
parameter_list|()
block|{
name|fireCommitEvent
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|rollbackEvent
parameter_list|()
block|{
name|fireRollbackEvent
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|physicalConnection
operator|.
name|setExceptionListener
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"Could not create a new connection: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|boolean
name|isInManagedTx
parameter_list|()
block|{
return|return
name|localAndXATransaction
operator|.
name|isInManagedTx
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|boolean
name|matches
parameter_list|(
name|Object
name|x
parameter_list|,
name|Object
name|y
parameter_list|)
block|{
if|if
condition|(
name|x
operator|==
literal|null
operator|^
name|y
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|x
operator|!=
literal|null
operator|&&
operator|!
name|x
operator|.
name|equals
argument_list|(
name|y
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|associate
parameter_list|(
name|Subject
name|subject
parameter_list|,
name|ActiveMQConnectionRequestInfo
name|info
parameter_list|)
throws|throws
name|JMSException
block|{
comment|// Do we need to change the associated userid/password
if|if
condition|(
operator|!
name|matches
argument_list|(
name|info
operator|.
name|getUserName
argument_list|()
argument_list|,
name|this
operator|.
name|info
operator|.
name|getUserName
argument_list|()
argument_list|)
operator|||
operator|!
name|matches
argument_list|(
name|info
operator|.
name|getPassword
argument_list|()
argument_list|,
name|this
operator|.
name|info
operator|.
name|getPassword
argument_list|()
argument_list|)
condition|)
block|{
name|physicalConnection
operator|.
name|changeUserInfo
argument_list|(
name|info
operator|.
name|getUserName
argument_list|()
argument_list|,
name|info
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Do we need to set the clientId?
if|if
condition|(
name|info
operator|.
name|getClientid
argument_list|()
operator|!=
literal|null
operator|&&
name|info
operator|.
name|getClientid
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|physicalConnection
operator|.
name|setClientID
argument_list|(
name|info
operator|.
name|getClientid
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|subject
operator|=
name|subject
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
block|}
specifier|public
name|Connection
name|getPhysicalConnection
parameter_list|()
block|{
return|return
name|physicalConnection
return|;
block|}
specifier|private
name|void
name|fireBeginEvent
parameter_list|()
block|{
name|ConnectionEvent
name|event
init|=
operator|new
name|ConnectionEvent
argument_list|(
name|ActiveMQManagedConnection
operator|.
name|this
argument_list|,
name|ConnectionEvent
operator|.
name|LOCAL_TRANSACTION_STARTED
argument_list|)
decl_stmt|;
for|for
control|(
name|ConnectionEventListener
name|l
range|:
name|listeners
control|)
block|{
name|l
operator|.
name|localTransactionStarted
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|fireCommitEvent
parameter_list|()
block|{
name|ConnectionEvent
name|event
init|=
operator|new
name|ConnectionEvent
argument_list|(
name|ActiveMQManagedConnection
operator|.
name|this
argument_list|,
name|ConnectionEvent
operator|.
name|LOCAL_TRANSACTION_COMMITTED
argument_list|)
decl_stmt|;
for|for
control|(
name|ConnectionEventListener
name|l
range|:
name|listeners
control|)
block|{
name|l
operator|.
name|localTransactionCommitted
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|fireRollbackEvent
parameter_list|()
block|{
name|ConnectionEvent
name|event
init|=
operator|new
name|ConnectionEvent
argument_list|(
name|ActiveMQManagedConnection
operator|.
name|this
argument_list|,
name|ConnectionEvent
operator|.
name|LOCAL_TRANSACTION_ROLLEDBACK
argument_list|)
decl_stmt|;
for|for
control|(
name|ConnectionEventListener
name|l
range|:
name|listeners
control|)
block|{
name|l
operator|.
name|localTransactionRolledback
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|fireCloseEvent
parameter_list|(
name|ManagedConnectionProxy
name|proxy
parameter_list|)
block|{
name|ConnectionEvent
name|event
init|=
operator|new
name|ConnectionEvent
argument_list|(
name|ActiveMQManagedConnection
operator|.
name|this
argument_list|,
name|ConnectionEvent
operator|.
name|CONNECTION_CLOSED
argument_list|)
decl_stmt|;
name|event
operator|.
name|setConnectionHandle
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
for|for
control|(
name|ConnectionEventListener
name|l
range|:
name|listeners
control|)
block|{
name|l
operator|.
name|connectionClosed
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|fireErrorOccurredEvent
parameter_list|(
name|Exception
name|error
parameter_list|)
block|{
name|ConnectionEvent
name|event
init|=
operator|new
name|ConnectionEvent
argument_list|(
name|ActiveMQManagedConnection
operator|.
name|this
argument_list|,
name|ConnectionEvent
operator|.
name|CONNECTION_ERROR_OCCURRED
argument_list|,
name|error
argument_list|)
decl_stmt|;
for|for
control|(
name|ConnectionEventListener
name|l
range|:
name|listeners
control|)
block|{
name|l
operator|.
name|connectionErrorOccurred
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @see javax.resource.spi.ManagedConnection#getConnection(javax.security.auth.Subject,      *      javax.resource.spi.ConnectionRequestInfo)      */
specifier|public
name|Object
name|getConnection
parameter_list|(
name|Subject
name|subject
parameter_list|,
name|ConnectionRequestInfo
name|info
parameter_list|)
throws|throws
name|ResourceException
block|{
name|ManagedConnectionProxy
name|proxy
init|=
operator|new
name|ManagedConnectionProxy
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|proxyConnections
operator|.
name|add
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
return|return
name|proxy
return|;
block|}
specifier|private
name|boolean
name|isDestroyed
parameter_list|()
block|{
return|return
name|destroyed
return|;
block|}
comment|/**      * Close down the physical connection to the server.      *       * @see javax.resource.spi.ManagedConnection#destroy()      */
specifier|public
name|void
name|destroy
parameter_list|()
throws|throws
name|ResourceException
block|{
comment|// Have we already been destroyed??
if|if
condition|(
name|isDestroyed
argument_list|()
condition|)
block|{
return|return;
block|}
name|cleanup
argument_list|()
expr_stmt|;
try|try
block|{
name|physicalConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|destroyed
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Error occurred during close of a JMS connection."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Cleans up all proxy handles attached to this physical connection so that      * they cannot be used anymore.      *       * @see javax.resource.spi.ManagedConnection#cleanup()      */
specifier|public
name|void
name|cleanup
parameter_list|()
throws|throws
name|ResourceException
block|{
comment|// Have we already been destroyed??
if|if
condition|(
name|isDestroyed
argument_list|()
condition|)
block|{
return|return;
block|}
for|for
control|(
name|ManagedConnectionProxy
name|proxy
range|:
name|proxyConnections
control|)
block|{
name|proxy
operator|.
name|cleanup
argument_list|()
expr_stmt|;
block|}
name|proxyConnections
operator|.
name|clear
argument_list|()
expr_stmt|;
try|try
block|{
name|physicalConnection
operator|.
name|cleanup
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"Could cleanup the ActiveMQ connection: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// defer transaction cleanup till after close so that close is aware of the current tx
name|localAndXATransaction
operator|.
name|cleanup
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see javax.resource.spi.ManagedConnection#associateConnection(java.lang.Object)      */
specifier|public
name|void
name|associateConnection
parameter_list|(
name|Object
name|connection
parameter_list|)
throws|throws
name|ResourceException
block|{
if|if
condition|(
name|connection
operator|instanceof
name|ManagedConnectionProxy
condition|)
block|{
name|ManagedConnectionProxy
name|proxy
init|=
operator|(
name|ManagedConnectionProxy
operator|)
name|connection
decl_stmt|;
name|proxyConnections
operator|.
name|add
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"Not supported : associating connection instance of "
operator|+
name|connection
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * @see javax.resource.spi.ManagedConnection#addConnectionEventListener(javax.resource.spi.ConnectionEventListener)      */
specifier|public
name|void
name|addConnectionEventListener
parameter_list|(
name|ConnectionEventListener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see javax.resource.spi.ManagedConnection#removeConnectionEventListener(javax.resource.spi.ConnectionEventListener)      */
specifier|public
name|void
name|removeConnectionEventListener
parameter_list|(
name|ConnectionEventListener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see javax.resource.spi.ManagedConnection#getXAResource()      */
specifier|public
name|XAResource
name|getXAResource
parameter_list|()
throws|throws
name|ResourceException
block|{
return|return
name|localAndXATransaction
return|;
block|}
comment|/**      * @see javax.resource.spi.ManagedConnection#getLocalTransaction()      */
specifier|public
name|LocalTransaction
name|getLocalTransaction
parameter_list|()
throws|throws
name|ResourceException
block|{
return|return
name|localAndXATransaction
return|;
block|}
comment|/**      * @see javax.resource.spi.ManagedConnection#getMetaData()      */
specifier|public
name|ManagedConnectionMetaData
name|getMetaData
parameter_list|()
throws|throws
name|ResourceException
block|{
return|return
operator|new
name|ManagedConnectionMetaData
argument_list|()
block|{
specifier|public
name|String
name|getEISProductName
parameter_list|()
throws|throws
name|ResourceException
block|{
if|if
condition|(
name|physicalConnection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"Not connected."
argument_list|)
throw|;
block|}
try|try
block|{
return|return
name|physicalConnection
operator|.
name|getMetaData
argument_list|()
operator|.
name|getJMSProviderName
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"Error accessing provider."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|String
name|getEISProductVersion
parameter_list|()
throws|throws
name|ResourceException
block|{
if|if
condition|(
name|physicalConnection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"Not connected."
argument_list|)
throw|;
block|}
try|try
block|{
return|return
name|physicalConnection
operator|.
name|getMetaData
argument_list|()
operator|.
name|getProviderVersion
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"Error accessing provider."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|int
name|getMaxConnections
parameter_list|()
throws|throws
name|ResourceException
block|{
if|if
condition|(
name|physicalConnection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"Not connected."
argument_list|)
throw|;
block|}
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
specifier|public
name|String
name|getUserName
parameter_list|()
throws|throws
name|ResourceException
block|{
if|if
condition|(
name|physicalConnection
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"Not connected."
argument_list|)
throw|;
block|}
try|try
block|{
return|return
name|physicalConnection
operator|.
name|getClientID
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ResourceException
argument_list|(
literal|"Error accessing provider."
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
comment|/**      * @see javax.resource.spi.ManagedConnection#setLogWriter(java.io.PrintWriter)      */
specifier|public
name|void
name|setLogWriter
parameter_list|(
name|PrintWriter
name|logWriter
parameter_list|)
throws|throws
name|ResourceException
block|{
name|this
operator|.
name|logWriter
operator|=
name|logWriter
expr_stmt|;
block|}
comment|/**      * @see javax.resource.spi.ManagedConnection#getLogWriter()      */
specifier|public
name|PrintWriter
name|getLogWriter
parameter_list|()
throws|throws
name|ResourceException
block|{
return|return
name|logWriter
return|;
block|}
comment|/**      * @param subject subject to match      * @param info cri to match      * @return whether the subject and cri match sufficiently to allow using this connection under the new circumstances      */
specifier|public
name|boolean
name|matches
parameter_list|(
name|Subject
name|subject
parameter_list|,
name|ConnectionRequestInfo
name|info
parameter_list|)
block|{
comment|// Check to see if it is our info class
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|info
operator|.
name|getClass
argument_list|()
operator|!=
name|ActiveMQConnectionRequestInfo
operator|.
name|class
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Do the subjects match?
if|if
condition|(
name|subject
operator|==
literal|null
operator|^
name|this
operator|.
name|subject
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|subject
operator|!=
literal|null
operator|&&
operator|!
name|subject
operator|.
name|equals
argument_list|(
name|this
operator|.
name|subject
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Does the info match?
return|return
name|info
operator|.
name|equals
argument_list|(
name|this
operator|.
name|info
argument_list|)
return|;
block|}
comment|/**      * When a proxy is closed this cleans up the proxy and notifies the      * ConnectionEventListeners that a connection closed.      *       * @param proxy      */
specifier|public
name|void
name|proxyClosedEvent
parameter_list|(
name|ManagedConnectionProxy
name|proxy
parameter_list|)
block|{
name|proxyConnections
operator|.
name|remove
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
name|proxy
operator|.
name|cleanup
argument_list|()
expr_stmt|;
name|fireCloseEvent
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onException
parameter_list|(
name|JMSException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Connection failed: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Cause: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
for|for
control|(
name|ManagedConnectionProxy
name|proxy
range|:
name|proxyConnections
control|)
block|{
name|proxy
operator|.
name|onException
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
comment|// Let the container know that the error occurred.
name|fireErrorOccurredEvent
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the transactionContext.      */
specifier|public
name|TransactionContext
name|getTransactionContext
parameter_list|()
block|{
return|return
name|transactionContext
return|;
block|}
block|}
end_class

end_unit

