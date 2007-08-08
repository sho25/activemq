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
name|command
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|state
operator|.
name|CommandVisitor
import|;
end_import

begin_comment
comment|/**  *   * @openwire:marshaller code="7"  * @version $Revision: 1.10 $  */
end_comment

begin_class
specifier|public
class|class
name|TransactionInfo
extends|extends
name|BaseCommand
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|DATA_STRUCTURE_TYPE
init|=
name|CommandTypes
operator|.
name|TRANSACTION_INFO
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|BEGIN
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|PREPARE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|COMMIT_ONE_PHASE
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|COMMIT_TWO_PHASE
init|=
literal|3
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|ROLLBACK
init|=
literal|4
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|RECOVER
init|=
literal|5
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|FORGET
init|=
literal|6
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|END
init|=
literal|7
decl_stmt|;
specifier|protected
name|byte
name|type
decl_stmt|;
specifier|protected
name|ConnectionId
name|connectionId
decl_stmt|;
specifier|protected
name|TransactionId
name|transactionId
decl_stmt|;
specifier|public
name|TransactionInfo
parameter_list|()
block|{     }
specifier|public
name|TransactionInfo
parameter_list|(
name|ConnectionId
name|connectionId
parameter_list|,
name|TransactionId
name|transactionId
parameter_list|,
name|byte
name|type
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
name|this
operator|.
name|transactionId
operator|=
name|transactionId
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|byte
name|getDataStructureType
parameter_list|()
block|{
return|return
name|DATA_STRUCTURE_TYPE
return|;
block|}
comment|/**      * @openwire:property version=1 cache=true      */
specifier|public
name|ConnectionId
name|getConnectionId
parameter_list|()
block|{
return|return
name|connectionId
return|;
block|}
specifier|public
name|void
name|setConnectionId
parameter_list|(
name|ConnectionId
name|connectionId
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1 cache=true      */
specifier|public
name|TransactionId
name|getTransactionId
parameter_list|()
block|{
return|return
name|transactionId
return|;
block|}
specifier|public
name|void
name|setTransactionId
parameter_list|(
name|TransactionId
name|transactionId
parameter_list|)
block|{
name|this
operator|.
name|transactionId
operator|=
name|transactionId
expr_stmt|;
block|}
comment|/**      * @openwire:property version=1      */
specifier|public
name|byte
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|void
name|setType
parameter_list|(
name|byte
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|Response
name|visit
parameter_list|(
name|CommandVisitor
name|visitor
parameter_list|)
throws|throws
name|Exception
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|TransactionInfo
operator|.
name|BEGIN
case|:
return|return
name|visitor
operator|.
name|processBeginTransaction
argument_list|(
name|this
argument_list|)
return|;
case|case
name|TransactionInfo
operator|.
name|END
case|:
return|return
name|visitor
operator|.
name|processEndTransaction
argument_list|(
name|this
argument_list|)
return|;
case|case
name|TransactionInfo
operator|.
name|PREPARE
case|:
return|return
name|visitor
operator|.
name|processPrepareTransaction
argument_list|(
name|this
argument_list|)
return|;
case|case
name|TransactionInfo
operator|.
name|COMMIT_ONE_PHASE
case|:
return|return
name|visitor
operator|.
name|processCommitTransactionOnePhase
argument_list|(
name|this
argument_list|)
return|;
case|case
name|TransactionInfo
operator|.
name|COMMIT_TWO_PHASE
case|:
return|return
name|visitor
operator|.
name|processCommitTransactionTwoPhase
argument_list|(
name|this
argument_list|)
return|;
case|case
name|TransactionInfo
operator|.
name|ROLLBACK
case|:
return|return
name|visitor
operator|.
name|processRollbackTransaction
argument_list|(
name|this
argument_list|)
return|;
case|case
name|TransactionInfo
operator|.
name|RECOVER
case|:
return|return
name|visitor
operator|.
name|processRecoverTransactions
argument_list|(
name|this
argument_list|)
return|;
case|case
name|TransactionInfo
operator|.
name|FORGET
case|:
return|return
name|visitor
operator|.
name|processForgetTransaction
argument_list|(
name|this
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Transaction info type unknown: "
operator|+
name|type
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

