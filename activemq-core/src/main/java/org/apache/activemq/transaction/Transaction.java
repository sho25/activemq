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
name|transaction
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
name|Iterator
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
name|command
operator|.
name|TransactionId
import|;
end_import

begin_comment
comment|/**  * Keeps track of all the actions the need to be done when a transaction does a  * commit or rollback.  *   * @version $Revision: 1.5 $  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Transaction
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|START_STATE
init|=
literal|0
decl_stmt|;
comment|// can go to: 1,2,3
specifier|public
specifier|static
specifier|final
name|byte
name|IN_USE_STATE
init|=
literal|1
decl_stmt|;
comment|// can go to: 2,3
specifier|public
specifier|static
specifier|final
name|byte
name|PREPARED_STATE
init|=
literal|2
decl_stmt|;
comment|// can go to: 3
specifier|public
specifier|static
specifier|final
name|byte
name|FINISHED_STATE
init|=
literal|3
decl_stmt|;
specifier|private
name|ArrayList
argument_list|<
name|Synchronization
argument_list|>
name|synchronizations
init|=
operator|new
name|ArrayList
argument_list|<
name|Synchronization
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|byte
name|state
init|=
name|START_STATE
decl_stmt|;
specifier|public
name|byte
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
specifier|public
name|void
name|setState
parameter_list|(
name|byte
name|state
parameter_list|)
block|{
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
block|}
specifier|public
name|void
name|addSynchronization
parameter_list|(
name|Synchronization
name|r
parameter_list|)
block|{
name|synchronizations
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|==
name|START_STATE
condition|)
block|{
name|state
operator|=
name|IN_USE_STATE
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removeSynchronization
parameter_list|(
name|Synchronization
name|r
parameter_list|)
block|{
name|synchronizations
operator|.
name|remove
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|prePrepare
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Is it ok to call prepare now given the state of the
comment|// transaction?
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|START_STATE
case|:
case|case
name|IN_USE_STATE
case|:
break|break;
default|default:
name|XAException
name|xae
init|=
operator|new
name|XAException
argument_list|(
literal|"Prepare cannot be called now."
argument_list|)
decl_stmt|;
name|xae
operator|.
name|errorCode
operator|=
name|XAException
operator|.
name|XAER_PROTO
expr_stmt|;
throw|throw
name|xae
throw|;
block|}
comment|// // Run the prePrepareTasks
comment|// for (Iterator iter = prePrepareTasks.iterator(); iter.hasNext();) {
comment|// Callback r = (Callback) iter.next();
comment|// r.execute();
comment|// }
block|}
specifier|protected
name|void
name|fireAfterCommit
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Synchronization
argument_list|>
name|iter
init|=
name|synchronizations
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Synchronization
name|s
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|s
operator|.
name|afterCommit
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|fireAfterRollback
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|Iterator
argument_list|<
name|Synchronization
argument_list|>
name|iter
init|=
name|synchronizations
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Synchronization
name|s
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|s
operator|.
name|afterRollback
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
operator|+
literal|"[synchronizations="
operator|+
name|synchronizations
operator|+
literal|"]"
return|;
block|}
specifier|public
specifier|abstract
name|void
name|commit
parameter_list|(
name|boolean
name|onePhase
parameter_list|)
throws|throws
name|XAException
throws|,
name|IOException
function_decl|;
specifier|public
specifier|abstract
name|void
name|rollback
parameter_list|()
throws|throws
name|XAException
throws|,
name|IOException
function_decl|;
specifier|public
specifier|abstract
name|int
name|prepare
parameter_list|()
throws|throws
name|XAException
throws|,
name|IOException
function_decl|;
specifier|public
specifier|abstract
name|TransactionId
name|getTransactionId
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|isPrepared
parameter_list|()
block|{
return|return
name|getState
argument_list|()
operator|==
name|PREPARED_STATE
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|synchronizations
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class

end_unit

