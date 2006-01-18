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
name|broker
operator|.
name|ft
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
name|AtomicBoolean
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
name|ConnectionContext
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
name|InsertableMutableBrokerFilter
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
name|MutableBrokerFilter
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
name|Command
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
name|ConnectionInfo
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
name|ExceptionResponse
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
name|Message
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
name|MessageAck
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
name|MessageDispatch
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
name|MessageDispatchNotification
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
name|ProducerInfo
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
name|RemoveInfo
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
name|Response
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
name|SessionInfo
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
name|TransactionId
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
name|TransactionInfo
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
name|transport
operator|.
name|Transport
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
comment|/**  * The Message Broker which passes messages to a slave  *   * @version $Revision: 1.8 $  */
end_comment

begin_class
specifier|public
class|class
name|MasterBroker
extends|extends
name|InsertableMutableBrokerFilter
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
name|MasterBroker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Transport
name|slave
decl_stmt|;
specifier|private
name|AtomicBoolean
name|started
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|public
name|MasterBroker
parameter_list|(
name|MutableBrokerFilter
name|parent
parameter_list|,
name|Transport
name|slave
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|slave
operator|=
name|slave
expr_stmt|;
block|}
specifier|public
name|void
name|startProcessing
parameter_list|()
block|{
name|started
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
name|stopProcessing
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stopProcessing
parameter_list|()
block|{
if|if
condition|(
name|started
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * A client is establishing a connection with the broker.      * @param context      * @param info       * @param client      */
specifier|public
name|void
name|addConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|)
throws|throws
name|Throwable
block|{
name|super
operator|.
name|addConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|sendAsyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
comment|/**      * A client is disconnecting from the broker.      * @param context the environment the operation is being executed under.      * @param info       * @param client      * @param error null if the client requested the disconnect or the error that caused the client to disconnect.      */
specifier|public
name|void
name|removeConnection
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ConnectionInfo
name|info
parameter_list|,
name|Throwable
name|error
parameter_list|)
throws|throws
name|Throwable
block|{
name|super
operator|.
name|removeConnection
argument_list|(
name|context
argument_list|,
name|info
argument_list|,
name|error
argument_list|)
expr_stmt|;
name|sendAsyncToSlave
argument_list|(
operator|new
name|RemoveInfo
argument_list|(
name|info
operator|.
name|getConnectionId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds a session.      * @param context      * @param info      * @throws Throwable      */
specifier|public
name|void
name|addSession
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|SessionInfo
name|info
parameter_list|)
throws|throws
name|Throwable
block|{
name|super
operator|.
name|addSession
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|sendAsyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
comment|/**      * Removes a session.      * @param context      * @param info      * @throws Throwable      */
specifier|public
name|void
name|removeSession
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|SessionInfo
name|info
parameter_list|)
throws|throws
name|Throwable
block|{
name|super
operator|.
name|removeSession
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|sendAsyncToSlave
argument_list|(
operator|new
name|RemoveInfo
argument_list|(
name|info
operator|.
name|getSessionId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds a producer.      * @param context the enviorment the operation is being executed under.      */
specifier|public
name|void
name|addProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|info
parameter_list|)
throws|throws
name|Throwable
block|{
name|super
operator|.
name|addProducer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|sendAsyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
comment|/**      * Removes a producer.      * @param context the enviorment the operation is being executed under.      */
specifier|public
name|void
name|removeProducer
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|ProducerInfo
name|info
parameter_list|)
throws|throws
name|Throwable
block|{
name|super
operator|.
name|removeProducer
argument_list|(
name|context
argument_list|,
name|info
argument_list|)
expr_stmt|;
name|sendAsyncToSlave
argument_list|(
operator|new
name|RemoveInfo
argument_list|(
name|info
operator|.
name|getProducerId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|beginTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|Throwable
block|{
name|super
operator|.
name|beginTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
expr_stmt|;
name|TransactionInfo
name|info
init|=
operator|new
name|TransactionInfo
argument_list|(
name|context
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|xid
argument_list|,
name|TransactionInfo
operator|.
name|BEGIN
argument_list|)
decl_stmt|;
name|sendAsyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
comment|/**      * Prepares a transaction. Only valid for xa transactions.      * @param client      * @param xid      * @return      */
specifier|public
name|int
name|prepareTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|Throwable
block|{
name|int
name|result
init|=
name|super
operator|.
name|prepareTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
decl_stmt|;
name|TransactionInfo
name|info
init|=
operator|new
name|TransactionInfo
argument_list|(
name|context
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|xid
argument_list|,
name|TransactionInfo
operator|.
name|PREPARE
argument_list|)
decl_stmt|;
name|sendAsyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * Rollsback a transaction.      * @param client      * @param xid      */
specifier|public
name|void
name|rollbackTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|Throwable
block|{
name|super
operator|.
name|rollbackTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
expr_stmt|;
name|TransactionInfo
name|info
init|=
operator|new
name|TransactionInfo
argument_list|(
name|context
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|xid
argument_list|,
name|TransactionInfo
operator|.
name|ROLLBACK
argument_list|)
decl_stmt|;
name|sendAsyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
comment|/**      * Commits a transaction.      * @param client      * @param xid      * @param onePhase      */
specifier|public
name|void
name|commitTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|,
name|boolean
name|onePhase
parameter_list|)
throws|throws
name|Throwable
block|{
name|super
operator|.
name|commitTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|,
name|onePhase
argument_list|)
expr_stmt|;
name|TransactionInfo
name|info
init|=
operator|new
name|TransactionInfo
argument_list|(
name|context
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|xid
argument_list|,
name|TransactionInfo
operator|.
name|COMMIT_ONE_PHASE
argument_list|)
decl_stmt|;
name|sendAsyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
comment|/**      * Forgets a transaction.      * @param client      * @param xid      * @param onePhase      * @throws Throwable       */
specifier|public
name|void
name|forgetTransaction
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|TransactionId
name|xid
parameter_list|)
throws|throws
name|Throwable
block|{
name|super
operator|.
name|forgetTransaction
argument_list|(
name|context
argument_list|,
name|xid
argument_list|)
expr_stmt|;
name|TransactionInfo
name|info
init|=
operator|new
name|TransactionInfo
argument_list|(
name|context
operator|.
name|getConnectionId
argument_list|()
argument_list|,
name|xid
argument_list|,
name|TransactionInfo
operator|.
name|FORGET
argument_list|)
decl_stmt|;
name|sendAsyncToSlave
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
comment|/**      * Notifiy the Broker that a dispatch has happened      * @param messageDispatch      */
specifier|public
name|void
name|processDispatch
parameter_list|(
name|MessageDispatch
name|messageDispatch
parameter_list|)
block|{
name|super
operator|.
name|processDispatch
argument_list|(
name|messageDispatch
argument_list|)
expr_stmt|;
name|MessageDispatchNotification
name|mdn
init|=
operator|new
name|MessageDispatchNotification
argument_list|()
decl_stmt|;
name|mdn
operator|.
name|setConsumerId
argument_list|(
name|messageDispatch
operator|.
name|getConsumerId
argument_list|()
argument_list|)
expr_stmt|;
name|mdn
operator|.
name|setDeliverySequenceId
argument_list|(
name|messageDispatch
operator|.
name|getDeliverySequenceId
argument_list|()
argument_list|)
expr_stmt|;
name|mdn
operator|.
name|setDestination
argument_list|(
name|messageDispatch
operator|.
name|getDestination
argument_list|()
argument_list|)
expr_stmt|;
name|mdn
operator|.
name|setMessageId
argument_list|(
name|messageDispatch
operator|.
name|getMessage
argument_list|()
operator|.
name|getMessageId
argument_list|()
argument_list|)
expr_stmt|;
name|sendAsyncToSlave
argument_list|(
name|mdn
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|send
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|Throwable
block|{
name|super
operator|.
name|send
argument_list|(
name|context
argument_list|,
name|message
argument_list|)
expr_stmt|;
name|sendAsyncToSlave
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|acknowledge
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|Throwable
block|{
name|super
operator|.
name|acknowledge
argument_list|(
name|context
argument_list|,
name|ack
argument_list|)
expr_stmt|;
name|sendAsyncToSlave
argument_list|(
name|ack
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|sendToSlave
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
comment|/*         if (message.isPersistent()){             sendSyncToSlave(message);         }else{             sendAsyncToSlave(message);         }         */
name|sendAsyncToSlave
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|sendAsyncToSlave
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
try|try
block|{
name|slave
operator|.
name|oneway
argument_list|(
name|command
argument_list|)
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
name|error
argument_list|(
literal|"Slave Failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|stopProcessing
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|sendSyncToSlave
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
try|try
block|{
name|Response
name|response
init|=
name|slave
operator|.
name|request
argument_list|(
name|command
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|isException
argument_list|()
condition|)
block|{
name|ExceptionResponse
name|er
init|=
operator|(
name|ExceptionResponse
operator|)
name|response
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
literal|"Slave Failed"
argument_list|,
name|er
operator|.
name|getException
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Slave Failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

