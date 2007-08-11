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
name|ArrayList
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
name|TopicReferenceStore
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
name|StoredMessageReference
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
name|StoredSubscription
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
name|StoredSubscription
operator|.
name|SubscriptionId
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

begin_class
specifier|public
class|class
name|JPATopicReferenceStore
extends|extends
name|JPAReferenceStore
implements|implements
name|TopicReferenceStore
block|{
specifier|private
name|Map
argument_list|<
name|SubscriptionId
argument_list|,
name|AtomicLong
argument_list|>
name|subscriberLastMessageMap
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|SubscriptionId
argument_list|,
name|AtomicLong
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|JPATopicReferenceStore
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
name|adapter
argument_list|,
name|destination
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
name|StoredSubscription
name|ss
init|=
name|findStoredSubscription
argument_list|(
name|manager
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
name|ss
operator|.
name|setLastAckedId
argument_list|(
name|messageId
operator|.
name|getBrokerSequenceId
argument_list|()
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
name|void
name|addSubsciption
parameter_list|(
name|SubscriptionInfo
name|info
parameter_list|,
name|boolean
name|retroactive
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
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|StoredSubscription
name|ss
init|=
operator|new
name|StoredSubscription
argument_list|()
decl_stmt|;
name|ss
operator|.
name|setClientId
argument_list|(
name|info
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
name|ss
operator|.
name|setSubscriptionName
argument_list|(
name|info
operator|.
name|getSubcriptionName
argument_list|()
argument_list|)
expr_stmt|;
name|ss
operator|.
name|setDestination
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
name|ss
operator|.
name|setSelector
argument_list|(
name|info
operator|.
name|getSelector
argument_list|()
argument_list|)
expr_stmt|;
name|ss
operator|.
name|setSubscribedDestination
argument_list|(
name|info
operator|.
name|getSubscribedDestination
argument_list|()
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
expr_stmt|;
name|ss
operator|.
name|setLastAckedId
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|retroactive
condition|)
block|{
name|Query
name|query
init|=
name|manager
operator|.
name|createQuery
argument_list|(
literal|"select max(m.id) from StoredMessageReference m"
argument_list|)
decl_stmt|;
name|Long
name|rc
init|=
operator|(
name|Long
operator|)
name|query
operator|.
name|getSingleResult
argument_list|()
decl_stmt|;
if|if
condition|(
name|rc
operator|!=
literal|null
condition|)
block|{
name|ss
operator|.
name|setLastAckedId
argument_list|(
name|rc
argument_list|)
expr_stmt|;
block|}
block|}
name|manager
operator|.
name|persist
argument_list|(
name|ss
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
name|StoredSubscription
name|ss
init|=
name|findStoredSubscription
argument_list|(
name|manager
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
name|manager
operator|.
name|remove
argument_list|(
name|ss
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
block|}
specifier|private
name|StoredSubscription
name|findStoredSubscription
parameter_list|(
name|EntityManager
name|manager
parameter_list|,
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|)
block|{
name|Query
name|query
init|=
name|manager
operator|.
name|createQuery
argument_list|(
literal|"select ss from StoredSubscription ss "
operator|+
literal|"where ss.clientId=?1 "
operator|+
literal|"and ss.subscriptionName=?2 "
operator|+
literal|"and ss.destination=?3"
argument_list|)
decl_stmt|;
name|query
operator|.
name|setParameter
argument_list|(
literal|1
argument_list|,
name|clientId
argument_list|)
expr_stmt|;
name|query
operator|.
name|setParameter
argument_list|(
literal|2
argument_list|,
name|subscriptionName
argument_list|)
expr_stmt|;
name|query
operator|.
name|setParameter
argument_list|(
literal|3
argument_list|,
name|destinationName
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|StoredSubscription
argument_list|>
name|resultList
init|=
name|query
operator|.
name|getResultList
argument_list|()
decl_stmt|;
if|if
condition|(
name|resultList
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
literal|null
return|;
return|return
name|resultList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
specifier|public
name|SubscriptionInfo
index|[]
name|getAllSubscriptions
parameter_list|()
throws|throws
name|IOException
block|{
name|SubscriptionInfo
name|rc
index|[]
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
name|ArrayList
argument_list|<
name|SubscriptionInfo
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|SubscriptionInfo
argument_list|>
argument_list|()
decl_stmt|;
name|Query
name|query
init|=
name|manager
operator|.
name|createQuery
argument_list|(
literal|"select ss from StoredSubscription ss where ss.destination=?1"
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
name|StoredSubscription
name|ss
range|:
operator|(
name|List
argument_list|<
name|StoredSubscription
argument_list|>
operator|)
name|query
operator|.
name|getResultList
argument_list|()
control|)
block|{
name|SubscriptionInfo
name|info
init|=
operator|new
name|SubscriptionInfo
argument_list|()
decl_stmt|;
name|info
operator|.
name|setClientId
argument_list|(
name|ss
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSelector
argument_list|(
name|ss
operator|.
name|getSelector
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSubscriptionName
argument_list|(
name|ss
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|setSubscribedDestination
argument_list|(
name|toSubscribedDestination
argument_list|(
name|ss
argument_list|)
argument_list|)
expr_stmt|;
name|l
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
name|rc
operator|=
operator|new
name|SubscriptionInfo
index|[
name|l
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|l
operator|.
name|toArray
argument_list|(
name|rc
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
specifier|private
name|ActiveMQDestination
name|toSubscribedDestination
parameter_list|(
name|StoredSubscription
name|ss
parameter_list|)
block|{
if|if
condition|(
name|ss
operator|.
name|getSubscribedDestination
argument_list|()
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|ActiveMQDestination
operator|.
name|createDestination
argument_list|(
name|ss
operator|.
name|getSubscribedDestination
argument_list|()
argument_list|,
name|ActiveMQDestination
operator|.
name|QUEUE_TYPE
argument_list|)
return|;
block|}
specifier|public
name|int
name|getMessageCount
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
literal|"select count(m) FROM StoredMessageReference m, StoredSubscription ss "
operator|+
literal|"where ss.clientId=?1 "
operator|+
literal|"and   ss.subscriptionName=?2 "
operator|+
literal|"and   ss.destination=?3 "
operator|+
literal|"and   m.destination=ss.destination and m.id> ss.lastAckedId"
argument_list|)
decl_stmt|;
name|query
operator|.
name|setParameter
argument_list|(
literal|1
argument_list|,
name|clientId
argument_list|)
expr_stmt|;
name|query
operator|.
name|setParameter
argument_list|(
literal|2
argument_list|,
name|subscriptionName
argument_list|)
expr_stmt|;
name|query
operator|.
name|setParameter
argument_list|(
literal|3
argument_list|,
name|destinationName
argument_list|)
expr_stmt|;
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
name|SubscriptionInfo
name|rc
init|=
literal|null
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
name|StoredSubscription
name|ss
init|=
name|findStoredSubscription
argument_list|(
name|manager
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|ss
operator|!=
literal|null
condition|)
block|{
name|rc
operator|=
operator|new
name|SubscriptionInfo
argument_list|()
expr_stmt|;
name|rc
operator|.
name|setClientId
argument_list|(
name|ss
operator|.
name|getClientId
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|.
name|setDestination
argument_list|(
name|destination
argument_list|)
expr_stmt|;
name|rc
operator|.
name|setSelector
argument_list|(
name|ss
operator|.
name|getSelector
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|.
name|setSubscriptionName
argument_list|(
name|ss
operator|.
name|getSubscriptionName
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|.
name|setSubscribedDestination
argument_list|(
name|toSubscribedDestination
argument_list|(
name|ss
argument_list|)
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
return|return
name|rc
return|;
block|}
specifier|public
name|void
name|recoverNextMessages
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
parameter_list|,
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
name|SubscriptionId
name|id
init|=
operator|new
name|SubscriptionId
argument_list|()
decl_stmt|;
name|id
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|id
operator|.
name|setSubscriptionName
argument_list|(
name|subscriptionName
argument_list|)
expr_stmt|;
name|id
operator|.
name|setDestination
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
name|AtomicLong
name|last
init|=
name|subscriberLastMessageMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|last
operator|==
literal|null
condition|)
block|{
name|StoredSubscription
name|ss
init|=
name|findStoredSubscription
argument_list|(
name|manager
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
name|ss
operator|.
name|getLastAckedId
argument_list|()
argument_list|)
expr_stmt|;
name|subscriberLastMessageMap
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|last
argument_list|)
expr_stmt|;
block|}
specifier|final
name|AtomicLong
name|lastMessageId
init|=
name|last
decl_stmt|;
name|Query
name|query
init|=
name|manager
operator|.
name|createQuery
argument_list|(
literal|"select m from StoredMessageReference m where m.destination=?1 and m.id>?2 order by m.id asc"
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
name|StoredMessageReference
name|m
range|:
operator|(
name|List
argument_list|<
name|StoredMessageReference
argument_list|>
operator|)
name|query
operator|.
name|getResultList
argument_list|()
control|)
block|{
name|MessageId
name|mid
init|=
operator|new
name|MessageId
argument_list|(
name|m
operator|.
name|getMessageId
argument_list|()
argument_list|)
decl_stmt|;
name|mid
operator|.
name|setBrokerSequenceId
argument_list|(
name|m
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|recoverMessageReference
argument_list|(
name|mid
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
name|recoverSubscription
parameter_list|(
name|String
name|clientId
parameter_list|,
name|String
name|subscriptionName
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
name|StoredSubscription
name|ss
init|=
name|findStoredSubscription
argument_list|(
name|manager
argument_list|,
name|clientId
argument_list|,
name|subscriptionName
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
name|manager
operator|.
name|createQuery
argument_list|(
literal|"select m from StoredMessageReference m where m.destination=?1 and m.id>?2 order by m.id asc"
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
name|ss
operator|.
name|getLastAckedId
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|StoredMessageReference
name|m
range|:
operator|(
name|List
argument_list|<
name|StoredMessageReference
argument_list|>
operator|)
name|query
operator|.
name|getResultList
argument_list|()
control|)
block|{
name|MessageId
name|mid
init|=
operator|new
name|MessageId
argument_list|(
name|m
operator|.
name|getMessageId
argument_list|()
argument_list|)
decl_stmt|;
name|mid
operator|.
name|setBrokerSequenceId
argument_list|(
name|m
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|listener
operator|.
name|recoverMessageReference
argument_list|(
name|mid
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
name|SubscriptionId
name|id
init|=
operator|new
name|SubscriptionId
argument_list|()
decl_stmt|;
name|id
operator|.
name|setClientId
argument_list|(
name|clientId
argument_list|)
expr_stmt|;
name|id
operator|.
name|setSubscriptionName
argument_list|(
name|subscriptionName
argument_list|)
expr_stmt|;
name|id
operator|.
name|setDestination
argument_list|(
name|destinationName
argument_list|)
expr_stmt|;
name|subscriberLastMessageMap
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

