begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** *<a href="http://activemq.org">ActiveMQ: The Open Source Message Fabric</a> * * Copyright 2005 (C) LogicBlaze, Inc. http://www.logicblaze.com * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * * http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. * **/
end_comment

begin_package
package|package
name|org
operator|.
name|activemq
operator|.
name|store
operator|.
name|jdbc
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|Packet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|command
operator|.
name|WireFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|ByteArrayPacket
import|;
end_import

begin_import
import|import
name|org
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
name|Message
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
name|MessageAck
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
name|MessageId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|store
operator|.
name|MessageRecoveryListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|store
operator|.
name|MessageStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|activemq
operator|.
name|util
operator|.
name|IOExceptionSupport
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.10 $  */
end_comment

begin_class
specifier|public
class|class
name|JDBCMessageStore
implements|implements
name|MessageStore
block|{
specifier|protected
specifier|final
name|WireFormat
name|wireFormat
decl_stmt|;
specifier|protected
specifier|final
name|ActiveMQDestination
name|destination
decl_stmt|;
specifier|protected
specifier|final
name|JDBCAdapter
name|adapter
decl_stmt|;
specifier|protected
specifier|final
name|JDBCPersistenceAdapter
name|persistenceAdapter
decl_stmt|;
specifier|public
name|JDBCMessageStore
parameter_list|(
name|JDBCPersistenceAdapter
name|persistenceAdapter
parameter_list|,
name|JDBCAdapter
name|adapter
parameter_list|,
name|WireFormat
name|wireFormat
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|this
operator|.
name|persistenceAdapter
operator|=
name|persistenceAdapter
expr_stmt|;
name|this
operator|.
name|adapter
operator|=
name|adapter
expr_stmt|;
name|this
operator|.
name|wireFormat
operator|=
name|wireFormat
expr_stmt|;
name|this
operator|.
name|destination
operator|=
name|destination
expr_stmt|;
block|}
specifier|public
name|void
name|addMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|Message
name|message
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Serialize the Message..
name|byte
name|data
index|[]
decl_stmt|;
try|try
block|{
name|Packet
name|packet
init|=
name|wireFormat
operator|.
name|marshal
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|data
operator|=
name|packet
operator|.
name|sliceAsBytes
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to broker message: "
operator|+
name|message
operator|.
name|getMessageId
argument_list|()
operator|+
literal|" in container: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// Get a connection and insert the message into the DB.
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|adapter
operator|.
name|doAddMessage
argument_list|(
name|c
argument_list|,
name|message
operator|.
name|getMessageId
argument_list|()
argument_list|,
name|destination
argument_list|,
name|data
argument_list|,
name|message
operator|.
name|getExpiration
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to broker message: "
operator|+
name|message
operator|.
name|getMessageId
argument_list|()
operator|+
literal|" in container: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addMessageReference
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageId
name|messageId
parameter_list|,
name|long
name|expirationTime
parameter_list|,
name|String
name|messageRef
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Get a connection and insert the message into the DB.
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|adapter
operator|.
name|doAddMessageReference
argument_list|(
name|c
argument_list|,
name|messageId
argument_list|,
name|destination
argument_list|,
name|expirationTime
argument_list|,
name|messageRef
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to broker message: "
operator|+
name|messageId
operator|+
literal|" in container: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|Message
name|getMessage
parameter_list|(
name|MessageId
name|messageId
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|id
init|=
name|messageId
operator|.
name|getBrokerSequenceId
argument_list|()
decl_stmt|;
comment|// Get a connection and pull the message out of the DB
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
name|data
index|[]
init|=
name|adapter
operator|.
name|doGetMessage
argument_list|(
name|c
argument_list|,
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Message
name|answer
init|=
operator|(
name|Message
operator|)
name|wireFormat
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteArrayPacket
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|answer
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to broker message: "
operator|+
name|messageId
operator|+
literal|" in container: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to broker message: "
operator|+
name|messageId
operator|+
literal|" in container: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getMessageReference
parameter_list|(
name|MessageId
name|messageId
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|id
init|=
name|messageId
operator|.
name|getBrokerSequenceId
argument_list|()
decl_stmt|;
comment|// Get a connection and pull the message out of the DB
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|adapter
operator|.
name|doGetMessageReference
argument_list|(
name|c
argument_list|,
name|id
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to broker message: "
operator|+
name|messageId
operator|+
literal|" in container: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to broker message: "
operator|+
name|messageId
operator|+
literal|" in container: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removeMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|seq
init|=
name|ack
operator|.
name|getLastMessageId
argument_list|()
operator|.
name|getBrokerSequenceId
argument_list|()
decl_stmt|;
comment|// Get a connection and remove the message from the DB
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|adapter
operator|.
name|doRemoveMessage
argument_list|(
name|c
argument_list|,
name|seq
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to broker message: "
operator|+
name|ack
operator|.
name|getLastMessageId
argument_list|()
operator|+
literal|" in container: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|recover
parameter_list|(
specifier|final
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Throwable
block|{
comment|// Get all the Message ids out of the database.
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
decl_stmt|;
try|try
block|{
name|c
operator|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
expr_stmt|;
name|adapter
operator|.
name|doRecover
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
operator|new
name|JDBCMessageRecoveryListener
argument_list|()
block|{
specifier|public
name|void
name|recoverMessage
parameter_list|(
name|long
name|sequenceId
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|Throwable
block|{
name|Message
name|msg
init|=
operator|(
name|Message
operator|)
name|wireFormat
operator|.
name|unmarshal
argument_list|(
operator|new
name|ByteArrayPacket
argument_list|(
name|data
argument_list|)
argument_list|)
decl_stmt|;
name|msg
operator|.
name|getMessageId
argument_list|()
operator|.
name|setBrokerSequenceId
argument_list|(
name|sequenceId
argument_list|)
expr_stmt|;
name|listener
operator|.
name|recoverMessage
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|recoverMessageReference
parameter_list|(
name|String
name|reference
parameter_list|)
throws|throws
name|IOException
throws|,
name|Throwable
block|{
name|listener
operator|.
name|recoverMessageReference
argument_list|(
name|reference
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to recover container. Reason: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{     }
specifier|public
name|void
name|stop
parameter_list|(
name|long
name|timeout
parameter_list|)
throws|throws
name|IOException
block|{     }
comment|/**      * @see org.activemq.store.MessageStore#removeAllMessages(ConnectionContext)      */
specifier|public
name|void
name|removeAllMessages
parameter_list|(
name|ConnectionContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Get a connection and remove the message from the DB
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|(
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|adapter
operator|.
name|doRemoveAllMessages
argument_list|(
name|c
argument_list|,
name|destination
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to broker remove all messages: "
operator|+
name|e
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|ActiveMQDestination
name|getDestination
parameter_list|()
block|{
return|return
name|destination
return|;
block|}
block|}
end_class

end_unit

