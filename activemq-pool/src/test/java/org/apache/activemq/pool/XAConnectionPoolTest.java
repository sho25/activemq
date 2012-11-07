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
name|pool
package|;
end_package

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
name|apache
operator|.
name|activemq
operator|.
name|test
operator|.
name|TestSupport
import|;
end_import

begin_class
specifier|public
class|class
name|XAConnectionPoolTest
extends|extends
name|TestSupport
block|{
comment|// https://issues.apache.org/jira/browse/AMQ-3251
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
name|ActiveMQXAConnectionFactory
argument_list|(
literal|"vm://test?broker.persistent=false"
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
block|}
block|}
end_class

end_unit

