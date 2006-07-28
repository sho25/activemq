begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|command
operator|.
name|BaseCommand
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
comment|/**  * Stores a messages/acknowledgements for a transaction  *   * @version $Revision: 1.4 $  */
end_comment

begin_class
class|class
name|KahaTransaction
block|{
specifier|private
specifier|static
specifier|final
name|Log
name|log
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|KahaTransaction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|List
name|list
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|void
name|add
parameter_list|(
name|KahaMessageStore
name|store
parameter_list|,
name|BaseCommand
name|command
parameter_list|)
block|{
name|TxCommand
name|tx
init|=
operator|new
name|TxCommand
argument_list|()
decl_stmt|;
name|tx
operator|.
name|setCommand
argument_list|(
name|command
argument_list|)
expr_stmt|;
name|tx
operator|.
name|setMessageStoreKey
argument_list|(
name|store
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|tx
argument_list|)
expr_stmt|;
block|}
name|Message
index|[]
name|getMessages
parameter_list|()
block|{
name|List
name|result
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|TxCommand
name|command
init|=
operator|(
name|TxCommand
operator|)
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|command
operator|.
name|isAdd
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|command
operator|.
name|getCommand
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Message
index|[]
name|messages
init|=
operator|new
name|Message
index|[
name|result
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
operator|(
name|Message
index|[]
operator|)
name|result
operator|.
name|toArray
argument_list|(
name|messages
argument_list|)
return|;
block|}
name|MessageAck
index|[]
name|getAcks
parameter_list|()
block|{
name|List
name|result
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|TxCommand
name|command
init|=
operator|(
name|TxCommand
operator|)
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|command
operator|.
name|isRemove
argument_list|()
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|command
operator|.
name|getCommand
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|MessageAck
index|[]
name|acks
init|=
operator|new
name|MessageAck
index|[
name|result
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
return|return
operator|(
name|MessageAck
index|[]
operator|)
name|result
operator|.
name|toArray
argument_list|(
name|acks
argument_list|)
return|;
block|}
name|void
name|prepare
parameter_list|()
block|{}
name|void
name|rollback
parameter_list|()
block|{
name|list
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**      * @throws IOException      */
name|void
name|commit
parameter_list|(
name|KahaTransactionStore
name|transactionStore
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|TxCommand
name|command
init|=
operator|(
name|TxCommand
operator|)
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|MessageStore
name|ms
init|=
name|transactionStore
operator|.
name|getStoreById
argument_list|(
name|command
operator|.
name|getMessageStoreKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|command
operator|.
name|isAdd
argument_list|()
condition|)
block|{
name|ms
operator|.
name|addMessage
argument_list|(
literal|null
argument_list|,
operator|(
name|Message
operator|)
name|command
operator|.
name|getCommand
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|TxCommand
name|command
init|=
operator|(
name|TxCommand
operator|)
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|MessageStore
name|ms
init|=
name|transactionStore
operator|.
name|getStoreById
argument_list|(
name|command
operator|.
name|getMessageStoreKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|command
operator|.
name|isRemove
argument_list|()
condition|)
block|{
name|ms
operator|.
name|removeMessage
argument_list|(
literal|null
argument_list|,
operator|(
name|MessageAck
operator|)
name|command
operator|.
name|getCommand
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|List
name|getList
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|(
name|list
argument_list|)
return|;
block|}
name|void
name|setList
parameter_list|(
name|List
name|list
parameter_list|)
block|{
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
block|}
block|}
end_class

end_unit

