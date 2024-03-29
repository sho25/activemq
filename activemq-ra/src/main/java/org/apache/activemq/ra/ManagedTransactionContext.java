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
name|TransactionContext
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
name|transaction
operator|.
name|Synchronization
import|;
end_import

begin_comment
comment|/**  * Allows us to switch between using a shared transaction context, or using a  * local transaction context.  *   *   */
end_comment

begin_class
specifier|public
class|class
name|ManagedTransactionContext
extends|extends
name|TransactionContext
block|{
specifier|private
specifier|final
name|TransactionContext
name|sharedContext
decl_stmt|;
specifier|private
name|boolean
name|useSharedTxContext
decl_stmt|;
specifier|public
name|ManagedTransactionContext
parameter_list|(
name|TransactionContext
name|sharedContext
parameter_list|)
block|{
name|super
argument_list|(
name|sharedContext
operator|.
name|getConnection
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|sharedContext
operator|=
name|sharedContext
expr_stmt|;
name|setLocalTransactionEventListener
argument_list|(
name|sharedContext
operator|.
name|getLocalTransactionEventListener
argument_list|()
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|isInLocalTransaction
argument_list|()
operator|||
name|isInXATransaction
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|JMSException
argument_list|(
literal|"The resource is already being used in transaction context."
argument_list|)
throw|;
block|}
name|useSharedTxContext
operator|=
name|enable
expr_stmt|;
block|}
specifier|public
name|void
name|begin
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|useSharedTxContext
condition|)
block|{
name|sharedContext
operator|.
name|begin
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|begin
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|commit
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|useSharedTxContext
condition|)
block|{
name|sharedContext
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|commit
parameter_list|(
name|Xid
name|xid
parameter_list|,
name|boolean
name|onePhase
parameter_list|)
throws|throws
name|XAException
block|{
if|if
condition|(
name|useSharedTxContext
condition|)
block|{
name|sharedContext
operator|.
name|commit
argument_list|(
name|xid
argument_list|,
name|onePhase
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|commit
argument_list|(
name|xid
argument_list|,
name|onePhase
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|end
parameter_list|(
name|Xid
name|xid
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|XAException
block|{
if|if
condition|(
name|useSharedTxContext
condition|)
block|{
name|sharedContext
operator|.
name|end
argument_list|(
name|xid
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|end
argument_list|(
name|xid
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|forget
parameter_list|(
name|Xid
name|xid
parameter_list|)
throws|throws
name|XAException
block|{
if|if
condition|(
name|useSharedTxContext
condition|)
block|{
name|sharedContext
operator|.
name|forget
argument_list|(
name|xid
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|forget
argument_list|(
name|xid
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|TransactionId
name|getTransactionId
parameter_list|()
block|{
if|if
condition|(
name|useSharedTxContext
condition|)
block|{
return|return
name|sharedContext
operator|.
name|getTransactionId
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getTransactionId
argument_list|()
return|;
block|}
block|}
specifier|public
name|int
name|getTransactionTimeout
parameter_list|()
throws|throws
name|XAException
block|{
if|if
condition|(
name|useSharedTxContext
condition|)
block|{
return|return
name|sharedContext
operator|.
name|getTransactionTimeout
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|getTransactionTimeout
argument_list|()
return|;
block|}
block|}
specifier|public
name|boolean
name|isInLocalTransaction
parameter_list|()
block|{
if|if
condition|(
name|useSharedTxContext
condition|)
block|{
return|return
name|sharedContext
operator|.
name|isInLocalTransaction
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|isInLocalTransaction
argument_list|()
return|;
block|}
block|}
specifier|public
name|boolean
name|isInXATransaction
parameter_list|()
block|{
if|if
condition|(
name|useSharedTxContext
condition|)
block|{
comment|// context considers endesd XA transactions as active, so just check for presence
comment|// of tx when it is shared
return|return
name|sharedContext
operator|.
name|isInTransaction
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|isInXATransaction
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isInTransaction
parameter_list|()
block|{
return|return
name|isInXATransaction
argument_list|()
operator|||
name|isInLocalTransaction
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isSameRM
parameter_list|(
name|XAResource
name|xaResource
parameter_list|)
throws|throws
name|XAException
block|{
if|if
condition|(
name|useSharedTxContext
condition|)
block|{
return|return
name|sharedContext
operator|.
name|isSameRM
argument_list|(
name|xaResource
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|isSameRM
argument_list|(
name|xaResource
argument_list|)
return|;
block|}
block|}
specifier|public
name|int
name|prepare
parameter_list|(
name|Xid
name|xid
parameter_list|)
throws|throws
name|XAException
block|{
if|if
condition|(
name|useSharedTxContext
condition|)
block|{
return|return
name|sharedContext
operator|.
name|prepare
argument_list|(
name|xid
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|prepare
argument_list|(
name|xid
argument_list|)
return|;
block|}
block|}
specifier|public
name|Xid
index|[]
name|recover
parameter_list|(
name|int
name|flag
parameter_list|)
throws|throws
name|XAException
block|{
if|if
condition|(
name|useSharedTxContext
condition|)
block|{
return|return
name|sharedContext
operator|.
name|recover
argument_list|(
name|flag
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|recover
argument_list|(
name|flag
argument_list|)
return|;
block|}
block|}
specifier|public
name|void
name|rollback
parameter_list|()
throws|throws
name|JMSException
block|{
if|if
condition|(
name|useSharedTxContext
condition|)
block|{
name|sharedContext
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|rollback
parameter_list|(
name|Xid
name|xid
parameter_list|)
throws|throws
name|XAException
block|{
if|if
condition|(
name|useSharedTxContext
condition|)
block|{
name|sharedContext
operator|.
name|rollback
argument_list|(
name|xid
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|rollback
argument_list|(
name|xid
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|boolean
name|setTransactionTimeout
parameter_list|(
name|int
name|seconds
parameter_list|)
throws|throws
name|XAException
block|{
if|if
condition|(
name|useSharedTxContext
condition|)
block|{
return|return
name|sharedContext
operator|.
name|setTransactionTimeout
argument_list|(
name|seconds
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|setTransactionTimeout
argument_list|(
name|seconds
argument_list|)
return|;
block|}
block|}
specifier|public
name|void
name|start
parameter_list|(
name|Xid
name|xid
parameter_list|,
name|int
name|flags
parameter_list|)
throws|throws
name|XAException
block|{
if|if
condition|(
name|useSharedTxContext
condition|)
block|{
name|sharedContext
operator|.
name|start
argument_list|(
name|xid
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|start
argument_list|(
name|xid
argument_list|,
name|flags
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addSynchronization
parameter_list|(
name|Synchronization
name|s
parameter_list|)
block|{
if|if
condition|(
name|useSharedTxContext
condition|)
block|{
name|sharedContext
operator|.
name|addSynchronization
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|addSynchronization
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

