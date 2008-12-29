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
name|jpa
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
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|persistence
operator|.
name|EntityManager
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|persistence
operator|.
name|Query
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
name|AbstractMessageStore
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
name|jpa
operator|.
name|model
operator|.
name|StoredMessage
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
name|usage
operator|.
name|MemoryUsage
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
name|usage
operator|.
name|SystemUsage
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

begin_class
specifier|public
class|class
name|JPAMessageStore
extends|extends
name|AbstractMessageStore
block|{
specifier|protected
specifier|final
name|JPAPersistenceAdapter
name|adapter
decl_stmt|;
specifier|protected
specifier|final
name|WireFormat
name|wireFormat
decl_stmt|;
specifier|protected
specifier|final
name|String
name|destinationName
decl_stmt|;
specifier|protected
name|AtomicLong
name|lastMessageId
init|=
operator|new
name|AtomicLong
argument_list|(
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|public
name|JPAMessageStore
parameter_list|(
name|JPAPersistenceAdapter
name|adapter
parameter_list|,
name|ActiveMQDestination
name|destination
parameter_list|)
block|{
name|super
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|this
operator|.
name|adapter
operator|=
name|adapter
expr_stmt|;
name|this
operator|.
name|destinationName
operator|=
name|destination
operator|.
name|getQualifiedName
argument_list|()
expr_stmt|;
name|this
operator|.
name|wireFormat
operator|=
name|this
operator|.
name|adapter
operator|.
name|getWireFormat
argument_list|()
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
name|EntityManager
name|manager
init|=
name|adapter
operator|.
name|beginEntityManager
argument_list|(
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|ByteSequence
name|sequence
init|=
name|wireFormat
operator|.
name|marshal
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|sequence
operator|.
name|compact
argument_list|()
expr_stmt|;
name|StoredMessage
name|sm
init|=
operator|new
name|StoredMessage
argument_list|()
decl_stmt|;
name|sm
operator|.
name|setDestination
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
name|sm
operator|.
name|setId
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
operator|.
name|getBrokerSequenceId
argument_list|()
argument_list|)
expr_stmt|;
name|sm
operator|.
name|setMessageId
argument_list|(
name|message
operator|.
name|getMessageId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sm
operator|.
name|setExiration
argument_list|(
name|message
operator|.
name|getExpiration
argument_list|()
argument_list|)
expr_stmt|;
name|sm
operator|.
name|setData
argument_list|(
name|sequence
operator|.
name|data
argument_list|)
expr_stmt|;
name|manager
operator|.
name|persist
argument_list|(
name|sm
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|adapter
operator|.
name|rollbackEntityManager
argument_list|(
name|context
argument_list|,
name|manager
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|adapter
operator|.
name|commitEntityManager
argument_list|(
name|context
argument_list|,
name|manager
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Message
name|getMessage
parameter_list|(
name|MessageId
name|identity
parameter_list|)
throws|throws
name|IOException
block|{
name|Message
name|rc
decl_stmt|;
name|EntityManager
name|manager
init|=
name|adapter
operator|.
name|beginEntityManager
argument_list|(
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|StoredMessage
name|message
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|identity
operator|.
name|getBrokerSequenceId
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|message
operator|=
name|manager
operator|.
name|find
argument_list|(
name|StoredMessage
operator|.
name|class
argument_list|,
name|identity
operator|.
name|getBrokerSequenceId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Query
name|query
init|=
name|manager
operator|.
name|createQuery
argument_list|(
literal|"select m from StoredMessage m where m.messageId=?1"
argument_list|)
decl_stmt|;
name|query
operator|.
name|setParameter
argument_list|(
literal|1
argument_list|,
name|identity
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|message
operator|=
operator|(
name|StoredMessage
operator|)
name|query
operator|.
name|getSingleResult
argument_list|()
expr_stmt|;
block|}
name|rc
operator|=
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
name|message
operator|.
name|getData
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|adapter
operator|.
name|rollbackEntityManager
argument_list|(
literal|null
argument_list|,
name|manager
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|adapter
operator|.
name|commitEntityManager
argument_list|(
literal|null
argument_list|,
name|manager
argument_list|)
expr_stmt|;
return|return
name|rc
return|;
block|}
specifier|public
name|int
name|getMessageCount
parameter_list|()
throws|throws
name|IOException
block|{
name|Long
name|rc
decl_stmt|;
name|EntityManager
name|manager
init|=
name|adapter
operator|.
name|beginEntityManager
argument_list|(
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Query
name|query
init|=
name|manager
operator|.
name|createQuery
argument_list|(
literal|"select count(m) from StoredMessage m"
argument_list|)
decl_stmt|;
name|rc
operator|=
operator|(
name|Long
operator|)
name|query
operator|.
name|getSingleResult
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|adapter
operator|.
name|rollbackEntityManager
argument_list|(
literal|null
argument_list|,
name|manager
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|adapter
operator|.
name|commitEntityManager
argument_list|(
literal|null
argument_list|,
name|manager
argument_list|)
expr_stmt|;
return|return
name|rc
operator|.
name|intValue
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|recover
parameter_list|(
name|MessageRecoveryListener
name|container
parameter_list|)
throws|throws
name|Exception
block|{
name|EntityManager
name|manager
init|=
name|adapter
operator|.
name|beginEntityManager
argument_list|(
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Query
name|query
init|=
name|manager
operator|.
name|createQuery
argument_list|(
literal|"select m from StoredMessage m where m.destination=?1 order by m.id asc"
argument_list|)
decl_stmt|;
name|query
operator|.
name|setParameter
argument_list|(
literal|1
argument_list|,
name|destinationName
argument_list|)
expr_stmt|;
for|for
control|(
name|StoredMessage
name|m
range|:
operator|(
name|List
argument_list|<
name|StoredMessage
argument_list|>
operator|)
name|query
operator|.
name|getResultList
argument_list|()
control|)
block|{
name|Message
name|message
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
name|m
operator|.
name|getData
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|container
operator|.
name|recoverMessage
argument_list|(
name|message
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
name|adapter
operator|.
name|rollbackEntityManager
argument_list|(
literal|null
argument_list|,
name|manager
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|adapter
operator|.
name|commitEntityManager
argument_list|(
literal|null
argument_list|,
name|manager
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|recoverNextMessages
parameter_list|(
name|int
name|maxReturned
parameter_list|,
name|MessageRecoveryListener
name|listener
parameter_list|)
throws|throws
name|Exception
block|{
name|EntityManager
name|manager
init|=
name|adapter
operator|.
name|beginEntityManager
argument_list|(
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|Query
name|query
init|=
name|manager
operator|.
name|createQuery
argument_list|(
literal|"select m from StoredMessage m where m.destination=?1 and m.id>?2 order by m.id asc"
argument_list|)
decl_stmt|;
name|query
operator|.
name|setParameter
argument_list|(
literal|1
argument_list|,
name|destinationName
argument_list|)
expr_stmt|;
name|query
operator|.
name|setParameter
argument_list|(
literal|2
argument_list|,
name|lastMessageId
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|.
name|setMaxResults
argument_list|(
name|maxReturned
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|StoredMessage
name|m
range|:
operator|(
name|List
argument_list|<
name|StoredMessage
argument_list|>
operator|)
name|query
operator|.
name|getResultList
argument_list|()
control|)
block|{
name|Message
name|message
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
name|m
operator|.
name|getData
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|listener
operator|.
name|recoverMessage
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|lastMessageId
operator|.
name|set
argument_list|(
name|m
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|>=
name|maxReturned
condition|)
block|{
return|return;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|adapter
operator|.
name|rollbackEntityManager
argument_list|(
literal|null
argument_list|,
name|manager
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|adapter
operator|.
name|commitEntityManager
argument_list|(
literal|null
argument_list|,
name|manager
argument_list|)
expr_stmt|;
block|}
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
name|EntityManager
name|manager
init|=
name|adapter
operator|.
name|beginEntityManager
argument_list|(
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|Query
name|query
init|=
name|manager
operator|.
name|createQuery
argument_list|(
literal|"delete from StoredMessage m where m.destination=?1"
argument_list|)
decl_stmt|;
name|query
operator|.
name|setParameter
argument_list|(
literal|1
argument_list|,
name|destinationName
argument_list|)
expr_stmt|;
name|query
operator|.
name|executeUpdate
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|adapter
operator|.
name|rollbackEntityManager
argument_list|(
name|context
argument_list|,
name|manager
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|adapter
operator|.
name|commitEntityManager
argument_list|(
name|context
argument_list|,
name|manager
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
name|MessageAck
name|ack
parameter_list|)
throws|throws
name|IOException
block|{
name|EntityManager
name|manager
init|=
name|adapter
operator|.
name|beginEntityManager
argument_list|(
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|Query
name|query
init|=
name|manager
operator|.
name|createQuery
argument_list|(
literal|"delete from StoredMessage m where m.id=?1"
argument_list|)
decl_stmt|;
name|query
operator|.
name|setParameter
argument_list|(
literal|1
argument_list|,
name|ack
operator|.
name|getLastMessageId
argument_list|()
operator|.
name|getBrokerSequenceId
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|.
name|executeUpdate
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|adapter
operator|.
name|rollbackEntityManager
argument_list|(
name|context
argument_list|,
name|manager
argument_list|)
expr_stmt|;
throw|throw
name|IOExceptionSupport
operator|.
name|create
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|adapter
operator|.
name|commitEntityManager
argument_list|(
name|context
argument_list|,
name|manager
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|resetBatching
parameter_list|()
block|{
name|lastMessageId
operator|.
name|set
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

