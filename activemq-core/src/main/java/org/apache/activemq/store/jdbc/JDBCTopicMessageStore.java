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
name|concurrent
operator|.
name|ConcurrentHashMap
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
name|AtomicLong
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
name|ActiveMQTopic
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
name|MessageId
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
name|SubscriptionInfo
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
name|MessageRecoveryListener
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
name|util
operator|.
name|ByteSequence
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
name|util
operator|.
name|IOExceptionSupport
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
name|wireformat
operator|.
name|WireFormat
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 1.6 $  */
end_comment

begin_class
specifier|public
class|class
name|JDBCTopicMessageStore
extends|extends
name|JDBCMessageStore
implements|implements
name|TopicMessageStore
block|{
specifier|private
name|Map
name|subscriberLastMessageMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|()
decl_stmt|;
specifier|public
name|JDBCTopicMessageStore
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
name|ActiveMQTopic
name|topic
parameter_list|)
block|{
name|super
argument_list|(
name|persistenceAdapter
argument_list|,
name|adapter
argument_list|,
name|wireFormat
argument_list|,
name|topic
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
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|,
name|MessageId
name|messageId
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|seq
init|=
name|messageId
operator|.
name|getBrokerSequenceId
argument_list|()
decl_stmt|;
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
name|doSetLastAck
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
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
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to store acknowledgment for: "
operator|+
name|clientId
operator|+
literal|" on message "
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
comment|/**      * @throws Exception      */
specifier|public
name|void
name|recoverSubscription
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|,
specifier|final
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
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
name|adapter
operator|.
name|doRecoverSubscription
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
argument_list|,
operator|new
name|JDBCMessageRecoveryListener
argument_list|()
block|{
specifier|public
name|boolean
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
name|Exception
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
name|ByteSequence
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
return|return
name|listener
operator|.
name|recoverMessage
argument_list|(
name|msg
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|recoverMessageReference
parameter_list|(
name|String
name|reference
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|listener
operator|.
name|recoverMessageReference
argument_list|(
operator|new
name|MessageId
argument_list|(
name|reference
argument_list|)
argument_list|)
return|;
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
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to recover subscription: "
operator|+
name|clientId
operator|+
literal|". Reason: "
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
specifier|synchronized
name|void
name|recoverNextMessages
parameter_list|(
specifier|final
name|String
name|clientId
parameter_list|,
specifier|final
name|String
name|subscriptionName
parameter_list|,
specifier|final
name|int
name|maxReturned
parameter_list|,
specifier|final
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
name|TransactionContext
name|c
init|=
name|persistenceAdapter
operator|.
name|getTransactionContext
argument_list|()
decl_stmt|;
name|String
name|subcriberId
init|=
name|getSubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
name|AtomicLong
name|last
init|=
operator|(
name|AtomicLong
operator|)
name|subscriberLastMessageMap
operator|.
name|get
argument_list|(
name|subcriberId
argument_list|)
decl_stmt|;
if|if
condition|(
name|last
operator|==
literal|null
condition|)
block|{
name|long
name|lastAcked
init|=
name|adapter
operator|.
name|doGetLastAckedDurableSubscriberMessageId
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
name|last
operator|=
operator|new
name|AtomicLong
argument_list|(
name|lastAcked
argument_list|)
expr_stmt|;
name|subscriberLastMessageMap
operator|.
name|put
argument_list|(
name|subcriberId
argument_list|,
name|last
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AtomicLong
name|finalLast
init|=
name|last
decl_stmt|;
try|try
block|{
name|adapter
operator|.
name|doRecoverNextMessages
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
argument_list|,
name|last
operator|.
name|get
argument_list|()
argument_list|,
name|maxReturned
argument_list|,
operator|new
name|JDBCMessageRecoveryListener
argument_list|()
block|{
specifier|public
name|boolean
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
name|Exception
block|{
if|if
condition|(
name|listener
operator|.
name|hasSpace
argument_list|()
condition|)
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
name|ByteSequence
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
name|finalLast
operator|.
name|set
argument_list|(
name|sequenceId
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|recoverMessageReference
parameter_list|(
name|String
name|reference
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|listener
operator|.
name|recoverMessageReference
argument_list|(
operator|new
name|MessageId
argument_list|(
name|reference
argument_list|)
argument_list|)
return|;
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
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
name|last
operator|.
name|set
argument_list|(
name|finalLast
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|resetBatching
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|)
block|{
name|String
name|subcriberId
init|=
name|getSubscriptionKey
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
name|subscriberLastMessageMap
operator|.
name|remove
argument_list|(
name|subcriberId
argument_list|)
expr_stmt|;
block|}
comment|/**      * @see org.apache.activemq.store.TopicMessageStore#storeSubsciption(org.apache.activemq.service.SubscriptionInfo,      *      boolean)      */
specifier|public
name|void
name|addSubsciption
parameter_list|(
name|SubscriptionInfo
name|subscriptionInfo
parameter_list|,
name|boolean
name|retroactive
parameter_list|)
throws|throws
name|IOException
block|{
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
name|doSetSubscriberEntry
argument_list|(
name|c
argument_list|,
name|subscriptionInfo
argument_list|,
name|retroactive
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to lookup subscription for info: "
operator|+
name|subscriptionInfo
operator|.
name|getClientId
argument_list|()
operator|+
literal|". Reason: "
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
comment|/**      * @see org.apache.activemq.store.TopicMessageStore#lookupSubscription(String,      *      String)      */
specifier|public
name|SubscriptionInfo
name|lookupSubscription
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|)
throws|throws
name|IOException
block|{
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
name|doGetSubscriberEntry
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to lookup subscription for: "
operator|+
name|clientId
operator|+
literal|". Reason: "
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
name|deleteSubscription
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|)
throws|throws
name|IOException
block|{
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
name|adapter
operator|.
name|doDeleteSubscription
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to remove subscription for: "
operator|+
name|clientId
operator|+
literal|". Reason: "
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
name|resetBatching
argument_list|(
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|SubscriptionInfo
index|[]
name|getAllSubscriptions
parameter_list|()
throws|throws
name|IOException
block|{
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
name|doGetAllSubscriptions
argument_list|(
name|c
argument_list|,
name|destination
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to lookup subscriptions. Reason: "
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
name|int
name|getMessageCount
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriberName
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|result
init|=
literal|0
decl_stmt|;
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
name|result
operator|=
name|adapter
operator|.
name|doGetDurableSubscriberMessageCount
argument_list|(
name|c
argument_list|,
name|destination
argument_list|,
name|clientId
argument_list|,
name|subscriberName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|JDBCPersistenceAdapter
operator|.
name|log
argument_list|(
literal|"JDBC Failure: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
literal|"Failed to get Message Count: "
operator|+
name|clientId
operator|+
literal|". Reason: "
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
return|return
name|result
return|;
block|}
specifier|protected
name|String
name|getSubscriptionKey
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriberName
parameter_list|)
block|{
name|String
name|result
init|=
name|clientId
operator|+
literal|":"
decl_stmt|;
name|result
operator|+=
name|subscriberName
operator|!=
literal|null
condition|?
name|subscriberName
else|:
literal|"NOT_SET"
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

