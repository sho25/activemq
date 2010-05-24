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
name|store
operator|.
name|kahadaptor
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
name|util
operator|.
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|ConcurrentHashMap
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
name|XAException
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|broker
operator|.
name|BrokerServiceAware
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
name|XATransactionId
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
name|kaha
operator|.
name|RuntimeStoreException
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
name|store
operator|.
name|MessageStore
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
name|store
operator|.
name|ProxyMessageStore
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
name|store
operator|.
name|ProxyTopicMessageStore
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
name|store
operator|.
name|TopicMessageStore
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
name|store
operator|.
name|TransactionRecoveryListener
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
name|store
operator|.
name|TransactionStore
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
name|store
operator|.
name|journal
operator|.
name|JournalPersistenceAdapter
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
comment|/**  * Provides a TransactionStore implementation that can create transaction aware  * MessageStore objects from non transaction aware MessageStore objects.  *   * @version $Revision: 1.4 $  */
end_comment

begin_class
specifier|public
class|class
name|KahaTransactionStore
implements|implements
name|TransactionStore
implements|,
name|BrokerServiceAware
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
name|KahaTransactionStore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Map
name|transactions
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|private
name|Map
name|prepared
decl_stmt|;
specifier|private
name|KahaPersistenceAdapter
name|adaptor
decl_stmt|;
specifier|private
name|BrokerService
name|brokerService
decl_stmt|;
name|KahaTransactionStore
parameter_list|(
name|KahaPersistenceAdapter
name|adaptor
parameter_list|,
name|Map
name|preparedMap
parameter_list|)
block|{
name|this
operator|.
name|adaptor
operator|=
name|adaptor
expr_stmt|;
name|this
operator|.
name|prepared
operator|=
name|preparedMap
expr_stmt|;
block|}
specifier|public
name|MessageStore
name|proxy
parameter_list|(
name|MessageStore
name|messageStore
parameter_list|)
block|{
return|return
operator|new
name|ProxyMessageStore
argument_list|(
name|messageStore
argument_list|)
block|{
specifier|public
name|void
name|addMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|Message
name|send
parameter_list|)
throws|throws
name|IOException
block|{
name|KahaTransactionStore
operator|.
name|this
operator|.
name|addMessage
argument_list|(
name|getDelegate
argument_list|()
argument_list|,
name|send
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|IOException
block|{
name|KahaTransactionStore
operator|.
name|this
operator|.
name|removeMessage
argument_list|(
name|getDelegate
argument_list|()
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
specifier|public
name|TopicMessageStore
name|proxy
parameter_list|(
name|TopicMessageStore
name|messageStore
parameter_list|)
block|{
return|return
operator|new
name|ProxyTopicMessageStore
argument_list|(
name|messageStore
argument_list|)
block|{
specifier|public
name|void
name|addMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|Message
name|send
parameter_list|)
throws|throws
name|IOException
block|{
name|KahaTransactionStore
operator|.
name|this
operator|.
name|addMessage
argument_list|(
name|getDelegate
argument_list|()
argument_list|,
name|send
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeMessage
parameter_list|(
name|ConnectionContext
name|context
parameter_list|,
specifier|final
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|IOException
block|{
name|KahaTransactionStore
operator|.
name|this
operator|.
name|removeMessage
argument_list|(
name|getDelegate
argument_list|()
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
comment|/**      * @see org.apache.activemq.store.TransactionStore#prepare(TransactionId)      */
specifier|public
name|void
name|prepare
parameter_list|(
name|TransactionId
name|txid
parameter_list|)
block|{
name|KahaTransaction
name|tx
init|=
name|getTx
argument_list|(
name|txid
argument_list|)
decl_stmt|;
if|if
condition|(
name|tx
operator|!=
literal|null
condition|)
block|{
name|tx
operator|.
name|prepare
argument_list|()
expr_stmt|;
name|prepared
operator|.
name|put
argument_list|(
name|txid
argument_list|,
name|tx
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @throws XAException      * @see org.apache.activemq.store.TransactionStore#commit(org.apache.activemq.service.Transaction)      */
specifier|public
name|void
name|commit
parameter_list|(
name|TransactionId
name|txid
parameter_list|,
name|boolean
name|wasPrepared
parameter_list|,
name|Runnable
name|done
parameter_list|)
throws|throws
name|IOException
block|{
name|KahaTransaction
name|tx
init|=
name|getTx
argument_list|(
name|txid
argument_list|)
decl_stmt|;
if|if
condition|(
name|tx
operator|!=
literal|null
condition|)
block|{
name|tx
operator|.
name|commit
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|removeTx
argument_list|(
name|txid
argument_list|)
expr_stmt|;
block|}
name|done
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
comment|/**      * @see org.apache.activemq.store.TransactionStore#rollback(TransactionId)      */
specifier|public
name|void
name|rollback
parameter_list|(
name|TransactionId
name|txid
parameter_list|)
block|{
name|KahaTransaction
name|tx
init|=
name|getTx
argument_list|(
name|txid
argument_list|)
decl_stmt|;
if|if
condition|(
name|tx
operator|!=
literal|null
condition|)
block|{
name|tx
operator|.
name|rollback
argument_list|()
expr_stmt|;
name|removeTx
argument_list|(
name|txid
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{     }
specifier|public
specifier|synchronized
name|void
name|recover
parameter_list|(
name|TransactionRecoveryListener
name|listener
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|prepared
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|XATransactionId
name|xid
init|=
operator|(
name|XATransactionId
operator|)
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|KahaTransaction
name|kt
init|=
operator|(
name|KahaTransaction
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|listener
operator|.
name|recover
argument_list|(
name|xid
argument_list|,
name|kt
operator|.
name|getMessages
argument_list|()
argument_list|,
name|kt
operator|.
name|getAcks
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @param message      * @throws IOException      */
name|void
name|addMessage
parameter_list|(
specifier|final
name|MessageStore
name|destination
parameter_list|,
specifier|final
name|Message
name|message
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|message
operator|.
name|isInTransaction
argument_list|()
condition|)
block|{
name|KahaTransaction
name|tx
init|=
name|getOrCreateTx
argument_list|(
name|message
operator|.
name|getTransactionId
argument_list|()
argument_list|)
decl_stmt|;
name|tx
operator|.
name|add
argument_list|(
operator|(
name|KahaMessageStore
operator|)
name|destination
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|destination
operator|.
name|addMessage
argument_list|(
literal|null
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeStoreException
name|rse
parameter_list|)
block|{
if|if
condition|(
name|rse
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IOException
condition|)
block|{
name|brokerService
operator|.
name|handleIOException
argument_list|(
operator|(
name|IOException
operator|)
name|rse
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
name|rse
throw|;
block|}
block|}
comment|/**      * @param ack      * @throws IOException      */
specifier|final
name|void
name|removeMessage
parameter_list|(
specifier|final
name|MessageStore
name|destination
parameter_list|,
specifier|final
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|ack
operator|.
name|isInTransaction
argument_list|()
condition|)
block|{
name|KahaTransaction
name|tx
init|=
name|getOrCreateTx
argument_list|(
name|ack
operator|.
name|getTransactionId
argument_list|()
argument_list|)
decl_stmt|;
name|tx
operator|.
name|add
argument_list|(
operator|(
name|KahaMessageStore
operator|)
name|destination
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|destination
operator|.
name|removeMessage
argument_list|(
literal|null
argument_list|,
name|ack
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeStoreException
name|rse
parameter_list|)
block|{
if|if
condition|(
name|rse
operator|.
name|getCause
argument_list|()
operator|instanceof
name|IOException
condition|)
block|{
name|brokerService
operator|.
name|handleIOException
argument_list|(
operator|(
name|IOException
operator|)
name|rse
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
throw|throw
name|rse
throw|;
block|}
block|}
specifier|protected
specifier|synchronized
name|KahaTransaction
name|getTx
parameter_list|(
name|TransactionId
name|key
parameter_list|)
block|{
name|KahaTransaction
name|result
init|=
operator|(
name|KahaTransaction
operator|)
name|transactions
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
operator|(
name|KahaTransaction
operator|)
name|prepared
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|protected
specifier|synchronized
name|KahaTransaction
name|getOrCreateTx
parameter_list|(
name|TransactionId
name|key
parameter_list|)
block|{
name|KahaTransaction
name|result
init|=
operator|(
name|KahaTransaction
operator|)
name|transactions
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
name|result
operator|=
operator|new
name|KahaTransaction
argument_list|()
expr_stmt|;
name|transactions
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|protected
specifier|synchronized
name|void
name|removeTx
parameter_list|(
name|TransactionId
name|key
parameter_list|)
block|{
name|transactions
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|prepared
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|delete
parameter_list|()
block|{
name|transactions
operator|.
name|clear
argument_list|()
expr_stmt|;
name|prepared
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|MessageStore
name|getStoreById
parameter_list|(
name|Object
name|id
parameter_list|)
block|{
return|return
name|adaptor
operator|.
name|retrieveMessageStore
argument_list|(
name|id
argument_list|)
return|;
block|}
specifier|public
name|void
name|setBrokerService
parameter_list|(
name|BrokerService
name|brokerService
parameter_list|)
block|{
name|this
operator|.
name|brokerService
operator|=
name|brokerService
expr_stmt|;
block|}
block|}
end_class

end_unit

