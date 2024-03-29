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
name|jms
operator|.
name|pool
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

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
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|QueueConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|QueueSender
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
name|Session
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
name|TopicConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|TopicPublisher
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
name|javax
operator|.
name|jms
operator|.
name|XAConnection
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|XAConnectionFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|spi
operator|.
name|ObjectFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|HeuristicMixedException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|HeuristicRollbackException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|InvalidTransactionException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|NotSupportedException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|RollbackException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|Status
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|Synchronization
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|SystemException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|Transaction
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|transaction
operator|.
name|TransactionManager
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
name|javax
operator|.
name|transaction
operator|.
name|xa
operator|.
name|Xid
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
name|ActiveMQXAConnectionFactory
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
name|ActiveMQXASession
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|XAConnectionPoolTest
extends|extends
name|JmsPoolTestSupport
block|{
comment|// https://issues.apache.org/jira/browse/AMQ-3251
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testAfterCompletionCanClose
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Vector
argument_list|<
name|Synchronization
argument_list|>
name|syncs
init|=
operator|new
name|Vector
argument_list|<
name|Synchronization
argument_list|>
argument_list|()
decl_stmt|;
name|ActiveMQTopic
name|topic
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|XaPooledConnectionFactory
name|pcf
init|=
operator|new
name|XaPooledConnectionFactory
argument_list|()
decl_stmt|;
name|pcf
operator|.
name|setConnectionFactory
argument_list|(
operator|new
name|XAConnectionFactoryOnly
argument_list|(
operator|new
name|ActiveMQXAConnectionFactory
argument_list|(
literal|"vm://test?broker.persistent=false"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Xid
name|xid
init|=
name|createXid
argument_list|()
decl_stmt|;
comment|// simple TM that is in a tx and will track syncs
name|pcf
operator|.
name|setTransactionManager
argument_list|(
operator|new
name|TransactionManager
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|begin
parameter_list|()
throws|throws
name|NotSupportedException
throws|,
name|SystemException
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|HeuristicMixedException
throws|,
name|HeuristicRollbackException
throws|,
name|IllegalStateException
throws|,
name|RollbackException
throws|,
name|SecurityException
throws|,
name|SystemException
block|{             }
annotation|@
name|Override
specifier|public
name|int
name|getStatus
parameter_list|()
throws|throws
name|SystemException
block|{
return|return
name|Status
operator|.
name|STATUS_ACTIVE
return|;
block|}
annotation|@
name|Override
specifier|public
name|Transaction
name|getTransaction
parameter_list|()
throws|throws
name|SystemException
block|{
return|return
operator|new
name|Transaction
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|HeuristicMixedException
throws|,
name|HeuristicRollbackException
throws|,
name|RollbackException
throws|,
name|SecurityException
throws|,
name|SystemException
block|{                     }
annotation|@
name|Override
specifier|public
name|boolean
name|delistResource
parameter_list|(
name|XAResource
name|xaRes
parameter_list|,
name|int
name|flag
parameter_list|)
throws|throws
name|IllegalStateException
throws|,
name|SystemException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|enlistResource
parameter_list|(
name|XAResource
name|xaRes
parameter_list|)
throws|throws
name|IllegalStateException
throws|,
name|RollbackException
throws|,
name|SystemException
block|{
try|try
block|{
name|xaRes
operator|.
name|start
argument_list|(
name|xid
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XAException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SystemException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getStatus
parameter_list|()
throws|throws
name|SystemException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|registerSynchronization
parameter_list|(
name|Synchronization
name|synch
parameter_list|)
throws|throws
name|IllegalStateException
throws|,
name|RollbackException
throws|,
name|SystemException
block|{
name|syncs
operator|.
name|add
argument_list|(
name|synch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|IllegalStateException
throws|,
name|SystemException
block|{                     }
annotation|@
name|Override
specifier|public
name|void
name|setRollbackOnly
parameter_list|()
throws|throws
name|IllegalStateException
throws|,
name|SystemException
block|{                     }
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|resume
parameter_list|(
name|Transaction
name|tobj
parameter_list|)
throws|throws
name|IllegalStateException
throws|,
name|InvalidTransactionException
throws|,
name|SystemException
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|IllegalStateException
throws|,
name|SecurityException
throws|,
name|SystemException
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|setRollbackOnly
parameter_list|()
throws|throws
name|IllegalStateException
throws|,
name|SystemException
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|setTransactionTimeout
parameter_list|(
name|int
name|seconds
parameter_list|)
throws|throws
name|SystemException
block|{             }
annotation|@
name|Override
specifier|public
name|Transaction
name|suspend
parameter_list|()
throws|throws
name|SystemException
block|{
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|TopicConnection
name|connection
init|=
operator|(
name|TopicConnection
operator|)
name|pcf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|TopicSession
name|session
init|=
name|connection
operator|.
name|createTopicSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|session
operator|instanceof
name|PooledSession
argument_list|)
expr_stmt|;
name|PooledSession
name|pooledSession
init|=
operator|(
name|PooledSession
operator|)
name|session
decl_stmt|;
name|assertTrue
argument_list|(
name|pooledSession
operator|.
name|getInternalSession
argument_list|()
operator|instanceof
name|ActiveMQXASession
argument_list|)
expr_stmt|;
name|TopicPublisher
name|publisher
init|=
name|session
operator|.
name|createPublisher
argument_list|(
name|topic
argument_list|)
decl_stmt|;
name|publisher
operator|.
name|publish
argument_list|(
name|session
operator|.
name|createMessage
argument_list|()
argument_list|)
expr_stmt|;
comment|// simulate a commit
for|for
control|(
name|Synchronization
name|sync
range|:
name|syncs
control|)
block|{
name|sync
operator|.
name|beforeCompletion
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Synchronization
name|sync
range|:
name|syncs
control|)
block|{
name|sync
operator|.
name|afterCompletion
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|pcf
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|static
name|long
name|txGenerator
init|=
literal|22
decl_stmt|;
specifier|public
name|Xid
name|createXid
parameter_list|()
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|DataOutputStream
name|os
init|=
operator|new
name|DataOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|os
operator|.
name|writeLong
argument_list|(
operator|++
name|txGenerator
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|byte
index|[]
name|bs
init|=
name|baos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
return|return
operator|new
name|Xid
argument_list|()
block|{
specifier|public
name|int
name|getFormatId
parameter_list|()
block|{
return|return
literal|86
return|;
block|}
specifier|public
name|byte
index|[]
name|getGlobalTransactionId
parameter_list|()
block|{
return|return
name|bs
return|;
block|}
specifier|public
name|byte
index|[]
name|getBranchQualifier
parameter_list|()
block|{
return|return
name|bs
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testAckModeOfPoolNonXAWithTM
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Vector
argument_list|<
name|Synchronization
argument_list|>
name|syncs
init|=
operator|new
name|Vector
argument_list|<
name|Synchronization
argument_list|>
argument_list|()
decl_stmt|;
name|ActiveMQTopic
name|topic
init|=
operator|new
name|ActiveMQTopic
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|XaPooledConnectionFactory
name|pcf
init|=
operator|new
name|XaPooledConnectionFactory
argument_list|()
decl_stmt|;
name|pcf
operator|.
name|setConnectionFactory
argument_list|(
operator|new
name|XAConnectionFactoryOnly
argument_list|(
operator|new
name|ActiveMQXAConnectionFactory
argument_list|(
literal|"vm://test?broker.persistent=false&broker.useJmx=false&jms.xaAckMode="
operator|+
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// simple TM that is in a tx and will track syncs
name|pcf
operator|.
name|setTransactionManager
argument_list|(
operator|new
name|TransactionManager
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|begin
parameter_list|()
throws|throws
name|NotSupportedException
throws|,
name|SystemException
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|HeuristicMixedException
throws|,
name|HeuristicRollbackException
throws|,
name|IllegalStateException
throws|,
name|RollbackException
throws|,
name|SecurityException
throws|,
name|SystemException
block|{             }
annotation|@
name|Override
specifier|public
name|int
name|getStatus
parameter_list|()
throws|throws
name|SystemException
block|{
return|return
name|Status
operator|.
name|STATUS_ACTIVE
return|;
block|}
annotation|@
name|Override
specifier|public
name|Transaction
name|getTransaction
parameter_list|()
throws|throws
name|SystemException
block|{
return|return
operator|new
name|Transaction
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|HeuristicMixedException
throws|,
name|HeuristicRollbackException
throws|,
name|RollbackException
throws|,
name|SecurityException
throws|,
name|SystemException
block|{                     }
annotation|@
name|Override
specifier|public
name|boolean
name|delistResource
parameter_list|(
name|XAResource
name|xaRes
parameter_list|,
name|int
name|flag
parameter_list|)
throws|throws
name|IllegalStateException
throws|,
name|SystemException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|enlistResource
parameter_list|(
name|XAResource
name|xaRes
parameter_list|)
throws|throws
name|IllegalStateException
throws|,
name|RollbackException
throws|,
name|SystemException
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getStatus
parameter_list|()
throws|throws
name|SystemException
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|registerSynchronization
parameter_list|(
name|Synchronization
name|synch
parameter_list|)
throws|throws
name|IllegalStateException
throws|,
name|RollbackException
throws|,
name|SystemException
block|{
name|syncs
operator|.
name|add
argument_list|(
name|synch
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|IllegalStateException
throws|,
name|SystemException
block|{                     }
annotation|@
name|Override
specifier|public
name|void
name|setRollbackOnly
parameter_list|()
throws|throws
name|IllegalStateException
throws|,
name|SystemException
block|{                     }
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|resume
parameter_list|(
name|Transaction
name|tobj
parameter_list|)
throws|throws
name|IllegalStateException
throws|,
name|InvalidTransactionException
throws|,
name|SystemException
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|IllegalStateException
throws|,
name|SecurityException
throws|,
name|SystemException
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|setRollbackOnly
parameter_list|()
throws|throws
name|IllegalStateException
throws|,
name|SystemException
block|{             }
annotation|@
name|Override
specifier|public
name|void
name|setTransactionTimeout
parameter_list|(
name|int
name|seconds
parameter_list|)
throws|throws
name|SystemException
block|{             }
annotation|@
name|Override
specifier|public
name|Transaction
name|suspend
parameter_list|()
throws|throws
name|SystemException
block|{
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|TopicConnection
name|connection
init|=
operator|(
name|TopicConnection
operator|)
name|pcf
operator|.
name|createConnection
argument_list|()
decl_stmt|;
name|TopicSession
name|session
init|=
name|connection
operator|.
name|createTopicSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"client ack is enforce"
argument_list|,
name|Session
operator|.
name|CLIENT_ACKNOWLEDGE
argument_list|,
name|session
operator|.
name|getAcknowledgeMode
argument_list|()
argument_list|)
expr_stmt|;
name|TopicPublisher
name|publisher
init|=
name|session
operator|.
name|createPublisher
argument_list|(
name|topic
argument_list|)
decl_stmt|;
name|publisher
operator|.
name|publish
argument_list|(
name|session
operator|.
name|createMessage
argument_list|()
argument_list|)
expr_stmt|;
comment|// simulate a commit
for|for
control|(
name|Synchronization
name|sync
range|:
name|syncs
control|)
block|{
name|sync
operator|.
name|beforeCompletion
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Synchronization
name|sync
range|:
name|syncs
control|)
block|{
name|sync
operator|.
name|afterCompletion
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|pcf
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testInstanceOf
parameter_list|()
throws|throws
name|Exception
block|{
name|XaPooledConnectionFactory
name|pcf
init|=
operator|new
name|XaPooledConnectionFactory
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|pcf
operator|instanceof
name|QueueConnectionFactory
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pcf
operator|instanceof
name|TopicConnectionFactory
argument_list|)
expr_stmt|;
name|pcf
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testBindable
parameter_list|()
throws|throws
name|Exception
block|{
name|XaPooledConnectionFactory
name|pcf
init|=
operator|new
name|XaPooledConnectionFactory
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|pcf
operator|instanceof
name|ObjectFactory
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|ObjectFactory
operator|)
name|pcf
operator|)
operator|.
name|getObjectInstance
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|instanceof
name|XaPooledConnectionFactory
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pcf
operator|.
name|isTmFromJndi
argument_list|()
argument_list|)
expr_stmt|;
name|pcf
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testBindableEnvOverrides
parameter_list|()
throws|throws
name|Exception
block|{
name|XaPooledConnectionFactory
name|pcf
init|=
operator|new
name|XaPooledConnectionFactory
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|pcf
operator|instanceof
name|ObjectFactory
argument_list|)
expr_stmt|;
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|environment
init|=
operator|new
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|environment
operator|.
name|put
argument_list|(
literal|"tmFromJndi"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|ObjectFactory
operator|)
name|pcf
operator|)
operator|.
name|getObjectInstance
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|environment
argument_list|)
operator|instanceof
name|XaPooledConnectionFactory
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|pcf
operator|.
name|isTmFromJndi
argument_list|()
argument_list|)
expr_stmt|;
name|pcf
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testSenderAndPublisherDest
parameter_list|()
throws|throws
name|Exception
block|{
name|XaPooledConnectionFactory
name|pcf
init|=
operator|new
name|XaPooledConnectionFactory
argument_list|()
decl_stmt|;
name|pcf
operator|.
name|setConnectionFactory
argument_list|(
operator|new
name|ActiveMQXAConnectionFactory
argument_list|(
literal|"vm://test?broker.persistent=false&broker.useJmx=false"
argument_list|)
argument_list|)
expr_stmt|;
name|QueueConnection
name|connection
init|=
name|pcf
operator|.
name|createQueueConnection
argument_list|()
decl_stmt|;
name|QueueSession
name|session
init|=
name|connection
operator|.
name|createQueueSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|QueueSender
name|sender
init|=
name|session
operator|.
name|createSender
argument_list|(
name|session
operator|.
name|createQueue
argument_list|(
literal|"AA"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|sender
operator|.
name|getQueue
argument_list|()
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|TopicConnection
name|topicConnection
init|=
name|pcf
operator|.
name|createTopicConnection
argument_list|()
decl_stmt|;
name|TopicSession
name|topicSession
init|=
name|topicConnection
operator|.
name|createTopicSession
argument_list|(
literal|false
argument_list|,
name|Session
operator|.
name|AUTO_ACKNOWLEDGE
argument_list|)
decl_stmt|;
name|TopicPublisher
name|topicPublisher
init|=
name|topicSession
operator|.
name|createPublisher
argument_list|(
name|topicSession
operator|.
name|createTopic
argument_list|(
literal|"AA"
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|topicPublisher
operator|.
name|getTopic
argument_list|()
operator|.
name|getTopicName
argument_list|()
argument_list|)
expr_stmt|;
name|topicConnection
operator|.
name|close
argument_list|()
expr_stmt|;
name|pcf
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
specifier|public
name|void
name|testSessionArgsIgnoredWithTm
parameter_list|()
throws|throws
name|Exception
block|{
name|XaPooledConnectionFactory
name|pcf
init|=
operator|new
name|XaPooledConnectionFactory
argument_list|()
decl_stmt|;
name|pcf
operator|.
name|setConnectionFactory
argument_list|(
operator|new
name|ActiveMQXAConnectionFactory
argument_list|(
literal|"vm://test?broker.persistent=false&broker.useJmx=false"
argument_list|)
argument_list|)
expr_stmt|;
comment|// simple TM that with no tx
name|pcf
operator|.
name|setTransactionManager
argument_list|(
operator|new
name|TransactionManager
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|begin
parameter_list|()
throws|throws
name|NotSupportedException
throws|,
name|SystemException
block|{
throw|throw
operator|new
name|SystemException
argument_list|(
literal|"NoTx"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|HeuristicMixedException
throws|,
name|HeuristicRollbackException
throws|,
name|IllegalStateException
throws|,
name|RollbackException
throws|,
name|SecurityException
throws|,
name|SystemException
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"NoTx"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getStatus
parameter_list|()
throws|throws
name|SystemException
block|{
return|return
name|Status
operator|.
name|STATUS_NO_TRANSACTION
return|;
block|}
annotation|@
name|Override
specifier|public
name|Transaction
name|getTransaction
parameter_list|()
throws|throws
name|SystemException
block|{
throw|throw
operator|new
name|SystemException
argument_list|(
literal|"NoTx"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|resume
parameter_list|(
name|Transaction
name|tobj
parameter_list|)
throws|throws
name|IllegalStateException
throws|,
name|InvalidTransactionException
throws|,
name|SystemException
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"NoTx"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|IllegalStateException
throws|,
name|SecurityException
throws|,
name|SystemException
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"NoTx"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setRollbackOnly
parameter_list|()
throws|throws
name|IllegalStateException
throws|,
name|SystemException
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"NoTx"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setTransactionTimeout
parameter_list|(
name|int
name|seconds
parameter_list|)
throws|throws
name|SystemException
block|{             }
annotation|@
name|Override
specifier|public
name|Transaction
name|suspend
parameter_list|()
throws|throws
name|SystemException
block|{
throw|throw
operator|new
name|SystemException
argument_list|(
literal|"NoTx"
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
name|QueueConnection
name|connection
init|=
name|pcf
operator|.
name|createQueueConnection
argument_list|()
decl_stmt|;
comment|// like ee tck
name|assertNotNull
argument_list|(
literal|"can create session(false, 0)"
argument_list|,
name|connection
operator|.
name|createQueueSession
argument_list|(
literal|false
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
name|pcf
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
specifier|static
class|class
name|XAConnectionFactoryOnly
implements|implements
name|XAConnectionFactory
block|{
specifier|private
specifier|final
name|XAConnectionFactory
name|connectionFactory
decl_stmt|;
name|XAConnectionFactoryOnly
parameter_list|(
name|XAConnectionFactory
name|connectionFactory
parameter_list|)
block|{
name|this
operator|.
name|connectionFactory
operator|=
name|connectionFactory
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|XAConnection
name|createXAConnection
parameter_list|()
throws|throws
name|JMSException
block|{
return|return
name|connectionFactory
operator|.
name|createXAConnection
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|XAConnection
name|createXAConnection
parameter_list|(
name|String
name|userName
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|JMSException
block|{
return|return
name|connectionFactory
operator|.
name|createXAConnection
argument_list|(
name|userName
argument_list|,
name|password
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

